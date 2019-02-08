package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordService;
import com.software.finatech.lslb.cms.service.service.contracts.RenewalFormService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ApplicationFormEmailSenderAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class RenewalFormServiceImpl implements RenewalFormService {

    private static final Logger logger = LoggerFactory.getLogger(RenewalFormServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private AuthInfoService authInfoService;
    private PaymentRecordService paymentRecordService;
    private ApplicationFormEmailSenderAsync renewalFormNotificationHelperAsync;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private LicenseServiceImpl licenseService;
    private static final String applicationAuditActionId = AuditActionReferenceData.APPLICATION_ID;

    @Autowired
    public RenewalFormServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                  AuthInfoService authInfoService,
                                  PaymentRecordService paymentRecordService,
                                  ApplicationFormEmailSenderAsync renewalFormNotificationHelperAsync,
                                  SpringSecurityAuditorAware springSecurityAuditorAware,
                                  AuditLogHelper auditLogHelper) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.authInfoService = authInfoService;
        this.paymentRecordService = paymentRecordService;
        this.renewalFormNotificationHelperAsync = renewalFormNotificationHelperAsync;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
    }

    @Override
    public Mono<ResponseEntity> approveRenewalForm(String renewalFormId, String approverId, HttpServletRequest request) {
        try {
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(approverId, AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Approver does not exist on the system", HttpStatus.BAD_REQUEST));
            }

            if (!authInfo.getAllUserPermissionIdsForUser().contains(LSLBAuthPermissionReferenceData.APPROVE_APPLICATION_FORM_ID)) {
                return Mono.just(new ResponseEntity<>("User does not have permission to approve applications", HttpStatus.BAD_REQUEST));
            }

            RenewalForm renewalForm = (RenewalForm) mongoRepositoryReactive.findById(renewalFormId, RenewalForm.class).block();
            if (renewalForm == null) {
                return Mono.just(new ResponseEntity<>("Renewal form does not exist", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.equals(RenewalFormStatusReferenceData.PENDING_DOCUMENT_UPLOAD, renewalForm.getFormStatusId())) {
                return Mono.just(new ResponseEntity<>("No document uploaded", HttpStatus.BAD_REQUEST));
            }
            if (!StringUtils.equals(RenewalFormStatusReferenceData.SUBMITTED, renewalForm.getFormStatusId())) {
                return Mono.just(new ResponseEntity<>("Form has not be submitted", HttpStatus.BAD_REQUEST));
            }
            Query query = new Query();
            query.addCriteria(Criteria.where("documentPurposeId").is(DocumentPurposeReferenceData.RENEWAL_LICENSE_ID));
            query.addCriteria(Criteria.where("active").is(true));
            query.addCriteria(Criteria.where("gameTypeIds").in(renewalForm.getGameTypeId()));

            //  query.addCriteria(Criteria.where("approverId").is(null));
            List<DocumentType> documentTypes = (List<DocumentType>) mongoRepositoryReactive.findAll(query, DocumentType.class).toStream().collect(Collectors.toList());
            int notApprrovalRequired=0;
            for(DocumentType documentType: documentTypes){
                if(documentType.getApproverId().isEmpty()||documentType.getApproverId()==null){
                    notApprrovalRequired=notApprrovalRequired+1;
                }
            }

            if(notApprrovalRequired==documentTypes.size()){
               renewalForm.setReadyForApproval(true);
            }

            if (renewalForm.getReadyForApproval() == false) {
                return Mono.just(new ResponseEntity<>("Not all documents on this application are approved", HttpStatus.BAD_REQUEST));
            }
            renewalForm.setApproverId(approverId);
            String approvedRenewalFormStatusId = RenewalFormStatusReferenceData.APPROVED;
            renewalForm.setFormStatusId(approvedRenewalFormStatusId);
            String status = licenseService.updateInReviewToLicense(renewalForm.getPaymentRecordId());
            if (status == "No License Record") {
                return Mono.just(new ResponseEntity<>("No License Record", HttpStatus.BAD_REQUEST));

            } else if (status == "Error! Please contact admin") {
                return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

            } else if (status == "OK") {
                saveRenewalForm(renewalForm);
            }


            String verbiage = String.format("Approved application form : %s ->  ", renewalForm.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.RENEWAL_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), renewalForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            //    renewalFormNotificationHelperAsync.sendApprovedMailToInstitutionAdmins(renewalForm);

            //changed renewal status of license after the payment
            License license = renewalForm.getLicense();
            if (license != null) {
                license.setRenewalStatus("false");
                mongoRepositoryReactive.saveOrUpdate(license);
            }
            return Mono.just(new ResponseEntity<>("Renewal form approved successfully", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving application form", e);
        }
    }

    @Override
    public Mono<ResponseEntity> rejectRenewalForm(String renewalFormId, RenewalFormRejectDto renewalFormRejectDto, HttpServletRequest request) {
        try {
            String rejectorId = renewalFormRejectDto.getUserId();
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(rejectorId, AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Rejecting user does not exist on the system", HttpStatus.BAD_REQUEST));
            }

            RenewalForm renewalForm = (RenewalForm) mongoRepositoryReactive.findById(renewalFormId, RenewalForm.class).block();
            if (renewalForm == null) {
                return Mono.just(new ResponseEntity<>("Renewal Form does not exist", HttpStatus.BAD_REQUEST));
            }

            if (StringUtils.equals(RenewalFormStatusReferenceData.APPROVED, renewalForm.getFormStatusId())) {
                return Mono.just(new ResponseEntity<>("Renewal Application already approved", HttpStatus.BAD_REQUEST));
            }
            renewalForm.setRejectorId(rejectorId);
            renewalForm.setReasonForRejection(renewalFormRejectDto.getReason());
            renewalForm.setFormStatusId(RenewalFormStatusReferenceData.PENDING);
            licenseService.updateRenewalReviewToInProgress(renewalForm);
            saveRenewalForm(renewalForm);
            String verbiage = String.format("Rejected application form : %s ->  ", renewalForm.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.RENEWAL_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), renewalForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            renewalFormNotificationHelperAsync.sendRejectionMailToInstitutionAdmins(renewalForm);
            return Mono.just(new ResponseEntity<>("Renewal Form rejected successfully", HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while rejecting application form", e);
        }
    }

    @Override
    public Mono<ResponseEntity> createRenewalForm(RenewalFormCreateDto renewalFormCreateDto) {
        PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.findById(renewalFormCreateDto.getPaymentRecordId(), PaymentRecord.class).block();

        if (!paymentRecord.getInstitutionId().equals(renewalFormCreateDto.getInstitutionId())) {
            return Mono.just(new ResponseEntity<>("Invalid Institution Selected", HttpStatus.BAD_REQUEST));

        }
        if (!paymentRecord.convertToDto().getGameTypeId().equals(renewalFormCreateDto.getGameTypeId())) {
            return Mono.just(new ResponseEntity<>("Invalid institution Selected", HttpStatus.BAD_REQUEST));
        }
        if (paymentRecord.convertToDto().getFeePaymentTypeId().equals(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID)) {
            return Mono.just(new ResponseEntity<>("Invalid Payment Record Selected", HttpStatus.BAD_REQUEST));
        }

        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckChangeInGamingMachines())) {
//            return Mono.just(new ResponseEntity<>("Enter CheckChangeInGamingMachines", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckConvictedCrime())) {
            return Mono.just(new ResponseEntity<>("Enter CheckConvictedCrime", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckNewInvestors())) {
            return Mono.just(new ResponseEntity<>("Enter CheckNewInvestors", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckPoliticalOffice())) {
            return Mono.just(new ResponseEntity<>("Enter CheckPoliticalOffice", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckPoliticalParty())) {
            return Mono.just(new ResponseEntity<>("Enter CheckPoliticalParty", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckTechnicalPartner())) {
            return Mono.just(new ResponseEntity<>("Enter CheckTechnicalPartner", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckStakeHoldersChange())) {
            return Mono.just(new ResponseEntity<>("Enter CheckStakeHoldersChange", HttpStatus.BAD_REQUEST));
        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckSharesAquisition())) {
            return Mono.just(new ResponseEntity<>("Enter CheckSharesAquisition", HttpStatus.BAD_REQUEST));

        }
        try {
            Query queryRenewal = new Query();
            queryRenewal.addCriteria(Criteria.where("paymentRecordId").is(renewalFormCreateDto.getPaymentRecordId()));
            RenewalForm renewalFormCheck = (RenewalForm) mongoRepositoryReactive.find(queryRenewal, RenewalForm.class).block();
            if (renewalFormCheck != null) {
                return Mono.just(new ResponseEntity<>("An existing renewal application is tied to this payment", HttpStatus.BAD_REQUEST));
            }
            Query queryLicense = new Query();
            queryLicense.addCriteria(Criteria.where("paymentRecordId").is(renewalFormCreateDto.getPaymentRecordId()));
            License license = (License) mongoRepositoryReactive.findById(renewalFormCreateDto.getLicenseId(), License.class).block();

            RenewalForm renewalForm = new RenewalForm();
            renewalForm.setId(UUID.randomUUID().toString());
            renewalForm.setLicensedId(license.getId());
            renewalForm.setPaymentRecordId(renewalFormCreateDto.getPaymentRecordId());
            renewalForm.setCheckChangeInGamingMachines(renewalFormCreateDto.getCheckChangeInGamingMachines());
            renewalForm.setCheckConvictedCrime(renewalFormCreateDto.getCheckConvictedCrime());
            renewalForm.setCheckNewInvestors(renewalFormCreateDto.getCheckNewInvestors());
            renewalForm.setCheckPoliticalOffice(renewalFormCreateDto.getCheckPoliticalOffice());
            renewalForm.setCheckPoliticalParty(renewalFormCreateDto.getCheckPoliticalParty());
            renewalForm.setCheckSharesAquisition(renewalFormCreateDto.getCheckSharesAquisition());
            renewalForm.setCheckStakeHoldersChange(renewalFormCreateDto.getCheckStakeHoldersChange());
            renewalForm.setCheckTechnicalPartner(renewalFormCreateDto.getCheckTechnicalPartner());
            renewalForm.setChangeInGamingMachines(renewalFormCreateDto.getChangeInGamingMachines());
            renewalForm.setNewInvestors(renewalFormCreateDto.getNewInvestors());
            renewalForm.setPoliticalParty(renewalFormCreateDto.getPoliticalParty());
            renewalForm.setPoliticalOffice(renewalFormCreateDto.getPoliticalOffice());
            renewalForm.setConvictedCrime(renewalFormCreateDto.getConvictedCrime());
            renewalForm.setSharesAquisition(renewalFormCreateDto.getSharesAquisition());
            renewalForm.setStakeHoldersChange(renewalFormCreateDto.getStakeHoldersChange());
            renewalForm.setTechnicalPartner(renewalFormCreateDto.getTechnicalPartner());
            renewalForm.setInstitutionId(renewalFormCreateDto.getInstitutionId());
            renewalForm.setGameTypeId(renewalFormCreateDto.getGameTypeId());
            renewalForm.setFormStatusId(RenewalFormStatusReferenceData.PENDING_DOCUMENT_UPLOAD);
            renewalForm.setReadyForApproval(false);
            mongoRepositoryReactive.saveOrUpdate(renewalForm);
            String verbiage = getInstitution(license.getInstitutionId()).getInstitutionName() + " submitted a renewal form";
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.RENEWAL_ID,
                    springSecurityAuditorAware.getCurrentAuditor().get(), getInstitution(license.getInstitutionId()).getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            //save the renewalForm to the license
            license.setRenewalFormId(renewalForm.getId());
            license.setRenewalInProgress(true);
            mongoRepositoryReactive.saveOrUpdate(license);

            return Mono.just(new ResponseEntity<>(renewalForm.convertToDto(), HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));
        }
    }

    @Override
    public Mono<ResponseEntity> getAllRenewalForms(int page, int pageSize, String sortType, String sortParam, String institutionId, String formStatusId, String gameTypeIds, String renewalId, HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(gameTypeIds)) {
                List<String> gameTypeIdList = Arrays.asList(gameTypeIds.split("-"));
                query.addCriteria(Criteria.where("gameTypeId").in(gameTypeIdList));
            }
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(renewalId)) {
                query.addCriteria(Criteria.where("id").is(renewalId));
            }
            if (!StringUtils.isEmpty(formStatusId)) {
                query.addCriteria(Criteria.where("formStatusId").is(formStatusId));
            }
            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, RenewalForm.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }
            Sort sort;
            if (!StringUtils.isEmpty(sortType) && !StringUtils.isEmpty(sortParam)) {
                sort = new Sort((sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortParam);
            } else {
                sort = new Sort(Sort.Direction.DESC, "id");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            ArrayList<RenewalForm> renewalForms = (ArrayList<RenewalForm>) mongoRepositoryReactive
                    .findAll(query, RenewalForm.class).toStream().collect(Collectors.toList());
            if (renewalForms.size() == 0) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.BAD_REQUEST));
            }
            ArrayList<RenewalFormDto> renewalFormDtos = new ArrayList<>();
            renewalForms.forEach(entry -> {
                renewalFormDtos.add(entry.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(renewalFormDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all institutions";
            return Mono.just(new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST));

        }
    }

    @Override
    public Mono<ResponseEntity> updateRenewalForm(RenewalFormUpdateDto renewalFormUpdateDto) {
        try {
            RenewalForm renewalForm = (RenewalForm) mongoRepositoryReactive.findById(renewalFormUpdateDto.getId(), RenewalForm.class).block();
            if (renewalForm == null) {
                return Mono.just(new ResponseEntity<>("Invalid Renewal Form Selected", HttpStatus.BAD_REQUEST));
            }
            if(!StringUtils.isEmpty(renewalFormUpdateDto.getCheckChangeInGamingMachines())){
                renewalForm.setCheckChangeInGamingMachines(renewalFormUpdateDto.getCheckChangeInGamingMachines());
            }
            renewalForm.setCheckConvictedCrime(renewalFormUpdateDto.getCheckConvictedCrime());
            renewalForm.setCheckNewInvestors(renewalFormUpdateDto.getCheckNewInvestors());
            renewalForm.setCheckPoliticalOffice(renewalFormUpdateDto.getCheckPoliticalOffice());
            renewalForm.setCheckPoliticalParty(renewalFormUpdateDto.getCheckPoliticalParty());
            renewalForm.setCheckSharesAquisition(renewalFormUpdateDto.getCheckSharesAquisition());
            renewalForm.setCheckStakeHoldersChange(renewalFormUpdateDto.getCheckStakeHoldersChange());
            renewalForm.setCheckTechnicalPartner(renewalFormUpdateDto.getCheckTechnicalPartner());
            renewalForm.setChangeInGamingMachines(renewalFormUpdateDto.getChangeInGamingMachines());
            renewalForm.setNewInvestors(renewalFormUpdateDto.getNewInvestors());
            renewalForm.setPoliticalParty(renewalFormUpdateDto.getPoliticalParty());
            renewalForm.setPoliticalOffice(renewalFormUpdateDto.getPoliticalOffice());
            renewalForm.setConvictedCrime(renewalFormUpdateDto.getConvictedCrime());
            renewalForm.setSharesAquisition(renewalFormUpdateDto.getSharesAquisition());
            renewalForm.setStakeHoldersChange(renewalFormUpdateDto.getStakeHoldersChange());
            renewalForm.setTechnicalPartner(renewalFormUpdateDto.getTechnicalPartner());
            renewalForm.setFormStatusId(RenewalFormStatusReferenceData.PENDING_DOCUMENT_UPLOAD);

            mongoRepositoryReactive.saveOrUpdate(renewalForm);
            String verbiage = getInstitution(renewalForm.getInstitutionId()).getInstitutionName() + " updated its renewal form";
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.RENEWAL_ID,
                    springSecurityAuditorAware.getCurrentAuditor().get(), getInstitution(renewalForm.getInstitutionId()).getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(renewalForm.convertToDto(), HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }

    @Override
    public Mono<ResponseEntity> getRenewalFormStatus() {
        try {
            List<RenewalFormStatus> renewalFormStatuses = (List<RenewalFormStatus>) mongoRepositoryReactive.findAll(new Query(), RenewalFormStatus.class).toStream().collect(Collectors.toList());
            List<RenewalFormStatusDto> renewalFormStatusDtos = new ArrayList<>();
            renewalFormStatuses.stream().forEach(renewalFormStatus -> {
                renewalFormStatusDtos.add(renewalFormStatus.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(renewalFormStatusDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all formStatuses";
            return Mono.just(new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST));
        }
    }

    @Override
    public Mono<ResponseEntity> getAllRenewalForms(String institutionId) {
        try {
            Query query = new Query();

            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").in(institutionId));
            }

            ArrayList<RenewalForm> renewalForms = (ArrayList<RenewalForm>) mongoRepositoryReactive
                    .findAll(query, RenewalForm.class).toStream().collect(Collectors.toList());
            if (renewalForms.size() == 0) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.BAD_REQUEST));
            }
            ArrayList<RenewalFormDto> renewalFormDtos = new ArrayList<>();
            renewalForms.forEach(entry -> {
                renewalFormDtos.add(entry.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(renewalFormDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all institutions";
            return Mono.just(new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST));
        }
    }

    @Override
    public Mono<ResponseEntity> addCommentsToForm(String renewalFormId, AddCommentDto addCommentDto, HttpServletRequest request) {
        try {
            RenewalForm renewalForm = getRenewalFormById(renewalFormId);
            if (renewalForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Renewal form with id %s does not exist", renewalFormId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            FormComment comment = new FormComment();
            comment.setTimeCreated(LocalDateTime.now());
            comment.setUserFullName(loggedInUser.getFullName());
            comment.setComment(addCommentDto.getComment());
            renewalForm.getFormComments().add(comment);
            mongoRepositoryReactive.saveOrUpdate(renewalForm);

            String verbiage = String.format("Added comment to renewal form  :Form Id -> %s ->  ,Category : -> %s , Comment -> %s",
                    renewalForm.getId(), renewalForm.getGameTypeName(), addCommentDto.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), renewalForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(renewalForm.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment", e);
        }
    }

    public RenewalForm getRenewalFormById(String renewalFormId) {
        return (RenewalForm) mongoRepositoryReactive.findById(renewalFormId, RenewalForm.class).block();
    }

    @Override
    public void approveRenewalFormDocument(Document document) {
        RenewalForm renewalForm = document.getRenewalForm();
        Query query = new Query();
        query.addCriteria(Criteria.where("entityId").is(renewalForm.getId()));
        query.addCriteria(Criteria.where("isCurrent").is(true));
        List<Document> documents = (List<Document>) mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());
      //  int countDocumentWithApproval = 0;
        int countApprovedDocument = 0;
//        Query queryDocumentType = new Query();
//        queryDocumentType.addCriteria(Criteria.where("documentPurposeId").is(DocumentPurposeReferenceData.RENEWAL_LICENSE_ID));
//        queryDocumentType.addCriteria(Criteria.where("active").is(true));
//        queryDocumentType.addCriteria(Criteria.where("gameTypeIds").in(renewalForm.getGameTypeId()));
//        List<DocumentType> approvalDocumentTypes = (List<DocumentType>) mongoRepositoryReactive.findAll(queryDocumentType, DocumentType.class).toStream().collect(Collectors.toList());
        int countUnApprovedDocument=0;
        for (Document doc : documents) {
            if (!StringUtils.isEmpty(doc.getApprovalRequestStatusId())) {
                //countDocumentWithApproval = +1;
                if (doc.getApprovalRequestStatusId().equals(ApprovalRequestStatusReferenceData.APPROVED_ID)) {
                    countApprovedDocument = countApprovedDocument+1;
                }else if(doc.getApprovalRequestStatusId().equals(ApprovalRequestStatusReferenceData.PENDING_ID)){
                    countUnApprovedDocument = countUnApprovedDocument+1;
                }
            }
        }
        if (countUnApprovedDocument==0) {
            if (renewalForm.getFormStatusId().equals(RenewalFormStatusReferenceData.SUBMITTED)) {
                renewalForm.setReadyForApproval(true);
            }

            renewalFormNotificationHelperAsync.sendApproverMailToFinalApproval(renewalForm);
        }

        mongoRepositoryReactive.saveOrUpdate(renewalForm);

    }

    public void rejectRenewalFormDocument(Document document, String comment) {
        RenewalForm renewalForm = document.getRenewalForm();
        if (renewalForm != null) {
            document.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
            mongoRepositoryReactive.saveOrUpdate(document);
            renewalFormNotificationHelperAsync.sendDocumentReturnMailToInstitutionMembers(renewalForm, document, comment);
        }
    }

    public void doDocumentReuploadNotification(Document document) {
        RenewalForm renewalForm = document.getRenewalForm();
        if (renewalForm != null) {
            renewalFormNotificationHelperAsync.sendResubmissionNotificationForRenewalForm(renewalForm, document);
        }
    }

    @Override
    public Mono<ResponseEntity> completeRenewalForm(String renewalFormId, boolean isResubmit, HttpServletRequest request) {
        try {
            RenewalForm renewalForm = (RenewalForm) mongoRepositoryReactive.findById(renewalFormId, RenewalForm.class).block();
            if (renewalForm == null) {
                return Mono.just(new ResponseEntity<>("Renewal form does not exist", HttpStatus.BAD_REQUEST));
            }
            String inReviewFormStatusId = RenewalFormStatusReferenceData.IN_REVIEW;
            renewalForm.setFormStatusId(inReviewFormStatusId);
            renewalForm.setSubmissionDate(LocalDate.now());
            saveRenewalForm(renewalForm);

            String verbiage = String.format("Submitted renewal application form : %s ->  ", renewalForm.getFormStatusId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), renewalForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            renewalFormNotificationHelperAsync.sendRenewalFormSubmissionMailToLSLBAdmins(renewalForm);

            //set renewal license to false renewal in progress
            License license = renewalForm.getLicense();
            if (license != null) {
                license.setRenewalInProgress(false);
                mongoRepositoryReactive.saveOrUpdate(license);
            }
            return Mono.just(new ResponseEntity<>("Renewal Application completed successfully and now in review", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while completing application form", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getRenewalFormFullDetailById(String renewalFormId) {
        try {
            RenewalForm renewalForm = getRenewalFormById(renewalFormId);
            if (renewalForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("renewal form with id %s not found", renewalFormId), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(renewalForm.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting renewalform by id", e);
        }
    }

    public void saveRenewalForm(RenewalForm renewalForm) {
        mongoRepositoryReactive.saveOrUpdate(renewalForm);
    }

    public Institution getInstitution(String institutionId) {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }
}
