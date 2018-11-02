package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.model.applicantDetails.ApplicantDetails;
import com.software.finatech.lslb.cms.service.model.applicantMembers.ApplicantMemberDetails;
import com.software.finatech.lslb.cms.service.model.contactDetails.ApplicantContactDetails;
import com.software.finatech.lslb.cms.service.model.criminalityDetails.ApplicantCriminalityDetails;
import com.software.finatech.lslb.cms.service.model.declaration.ApplicantDeclarationDetails;
import com.software.finatech.lslb.cms.service.model.otherInformation.ApplicantOtherInformation;
import com.software.finatech.lslb.cms.service.model.outletInformation.ApplicantOutletInformation;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.ApplicationFormService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ApplicationFormEmailSenderAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class ApplicationFormServiceImpl implements ApplicationFormService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationFormServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private AuthInfoService authInfoService;
    private PaymentRecordService paymentRecordService;
    private ApplicationFormEmailSenderAsync applicationFormNotificationHelperAsync;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;

    @Autowired
    private LicenseServiceImpl licenseService;

    private static final String applicationAuditActionId = AuditActionReferenceData.APPLICATION_ID;

    @Autowired
    public ApplicationFormServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                      AuthInfoService authInfoService,
                                      PaymentRecordService paymentRecordService,
                                      ApplicationFormEmailSenderAsync applicationFormNotificationHelperAsync,
                                      SpringSecurityAuditorAware springSecurityAuditorAware,
                                      AuditLogHelper auditLogHelper) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.authInfoService = authInfoService;
        this.paymentRecordService = paymentRecordService;
        this.applicationFormNotificationHelperAsync = applicationFormNotificationHelperAsync;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
    }

    @Override
    public Mono<ResponseEntity> createApplicationForm(ApplicationFormCreateDto applicationFormCreateDto, HttpServletRequest request) {
        try {
            Mono<ResponseEntity> validateCreateApplicationFormResponse = validateCreateApplicationForm(applicationFormCreateDto);
            if (validateCreateApplicationFormResponse != null) {
                return validateCreateApplicationFormResponse;
            }

            ApplicationForm applicationForm = fromCreateDto(applicationFormCreateDto);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.CREATED_STATUS_ID);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Created application form : %s ->  Category :%s",
                    applicationForm.getApplicationFormId(), applicationForm.getGameTypeName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the application form";
            return logAndReturnError(logger, errorMsg, e);
        }
    }



    @Override
    public Mono<ResponseEntity> findAllApplicationForm(int page,
                                                       int pageSize,
                                                       String sortDirection,
                                                       String sortProperty,
                                                       String institutionId,
                                                       String applicationFormStatusId,
                                                       String approverId,
                                                       String gameTypeId,
                                                       HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(approverId)) {
                query.addCriteria(Criteria.where("approverId").is(approverId));
            }
            if (!StringUtils.isEmpty(applicationFormStatusId)) {
                query.addCriteria(Criteria.where("applicationFormStatusId").is(applicationFormStatusId));
            }
            if (!StringUtils.isEmpty(gameTypeId)) {
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            }

            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, ApplicationForm.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

            Sort sort;
            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "createdAt");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);
            ArrayList<ApplicationForm> applicationForms = (ArrayList<ApplicationForm>) mongoRepositoryReactive.findAll(query, ApplicationForm.class).toStream().collect(Collectors.toList());
            if (applicationForms == null || applicationForms.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<ApplicationFormDto> applicationFormDtos = new ArrayList<>();

            applicationForms.forEach(applicationForm -> {
                applicationFormDtos.add(applicationForm.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(applicationFormDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while finding application forms";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllApplicationFormStatus() {
        try {
            ArrayList<ApplicationFormStatus> applicationFormStatuses = (ArrayList<ApplicationFormStatus>) mongoRepositoryReactive
                    .findAll(new Query(), ApplicationFormStatus.class).toStream().collect(Collectors.toList());

            if (applicationFormStatuses == null || applicationFormStatuses.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.OK));
            }
            List<EnumeratedFactDto> applicationFormStatusDto = new ArrayList<>();
            applicationFormStatuses.forEach(applicationFormStatus -> {
                applicationFormStatusDto.add(applicationFormStatus.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(applicationFormStatusDto, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while getting all application form statuses";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> getApplicantDetails(String applicationFormId) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("ApplicationForm with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            } else {
                ApplicantDetails applicantDetails = applicationForm.getApplicantDetails();
                if (applicantDetails == null) {
                    return Mono.just(new ResponseEntity<>("No applicant details found for application form", HttpStatus.NOT_FOUND));
                } else {
                    return Mono.just(new ResponseEntity<>(applicantDetails, HttpStatus.OK));
                }
            }
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting applicant details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantDetails(String applicationFormId, ApplicantDetails applicantDetails, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantDetails(applicantDetails);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Saved applicant details for application form : %s ", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving applicant details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getApplicantMembersDetails(String applicationFormId) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("ApplicationForm with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            } else {
                ApplicantMemberDetails applicantMemberDetails = applicationForm.getApplicantMemberDetails();
                if (applicantMemberDetails == null) {
                    return Mono.just(new ResponseEntity<>("No applicant member details found for application form", HttpStatus.NOT_FOUND));
                } else {
                    return Mono.just(new ResponseEntity<>(applicantMemberDetails, HttpStatus.OK));
                }
            }
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting applicant members details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantMembersDetails(String applicationFormId, ApplicantMemberDetails applicantMemberDetails, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantMemberDetails(applicantMemberDetails);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Saved applicant members details for application form : %s", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving applicant members details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getApplicantContactDetails(String applicationFormId) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("ApplicationForm with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            } else {
                ApplicantContactDetails applicantContactDetails = applicationForm.getApplicantContactDetails();
                if (applicantContactDetails == null) {
                    return Mono.just(new ResponseEntity<>("No applicant contact details found for application form", HttpStatus.NOT_FOUND));
                } else {
                    return Mono.just(new ResponseEntity<>(applicantContactDetails, HttpStatus.OK));
                }
            }
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting applicant contact details", e);
        }

    }

    @Override
    public Mono<ResponseEntity> saveApplicantContactDetails(String applicationFormId, ApplicantContactDetails applicantContactDetails, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantContactDetails(applicantContactDetails);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Saved applicant contact details for application form : %s ->  ", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving applicant contact details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getApplicantCriminalityDetails(String applicationFormId) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("ApplicationForm with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            } else {
                ApplicantCriminalityDetails applicantCriminalityDetails = applicationForm.getApplicantCriminalityDetails();
                if (applicantCriminalityDetails == null) {
                    return Mono.just(new ResponseEntity<>("No criminality details found for application form", HttpStatus.NOT_FOUND));
                } else {
                    return Mono.just(new ResponseEntity<>(applicantCriminalityDetails, HttpStatus.OK));
                }
            }
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting applicant criminality details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantCriminalityDetails(String applicationFormId, ApplicantCriminalityDetails applicantCriminalityDetails, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantCriminalityDetails(applicantCriminalityDetails);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Saved applicant criminality details for application form : %s ->  ", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving applicant criminality details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getApplicantDeclarationDetails(String applicationFormId) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("ApplicationForm with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            } else {
                ApplicantDeclarationDetails applicantDeclarationDetails = applicationForm.getApplicantDeclarationDetails();
                if (applicantDeclarationDetails == null) {
                    return Mono.just(new ResponseEntity<>("No applicant declarations details found for application form", HttpStatus.NOT_FOUND));
                } else {
                    return Mono.just(new ResponseEntity<>(applicantDeclarationDetails, HttpStatus.OK));
                }
            }
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting applicant declaration details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantDeclarationDetails(String applicationFormId, ApplicantDeclarationDetails applicantDeclarationDetails, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantDeclarationDetails(applicantDeclarationDetails);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Saved applicant declaration details for application form : %s ->  ", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving applicant declaration details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getApplicantOtherInformation(String applicationFormId) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("ApplicationForm with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            } else {
                ApplicantOtherInformation applicantOtherInformation = applicationForm.getApplicantOtherInformation();
                if (applicantOtherInformation == null) {
                    return Mono.just(new ResponseEntity<>("No applicant other information found for application form", HttpStatus.NOT_FOUND));
                } else {
                    return Mono.just(new ResponseEntity<>(applicantOtherInformation, HttpStatus.OK));
                }
            }
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting applicant other details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantOtherInformation(String applicationFormId, ApplicantOtherInformation applicantOtherInformation, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantOtherInformation(applicantOtherInformation);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Saved applicant other information for application form : %s ->  ", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving applicant other details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getApplicantOutletInformation(String applicationFormId) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("ApplicationForm with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            } else {
                ApplicantOutletInformation applicantOutletInformation = applicationForm.getApplicantOutletInformation();
                if (applicantOutletInformation == null) {
                    return Mono.just(new ResponseEntity<>("No applicant outlet details found for application form", HttpStatus.NOT_FOUND));
                } else {
                    return Mono.just(new ResponseEntity<>(applicantOutletInformation, HttpStatus.OK));
                }
            }
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting applicant outlet details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantOutletInformation(String applicationFormId, ApplicantOutletInformation applicantOutletInformation, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantOutletInformation(applicantOutletInformation);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Saved applicant outlet information for application form : %s ->  ", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving applicant outlet details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> approveApplicationForm(String applicationFormId, String approverId, HttpServletRequest request) {
        try {
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(approverId, AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Approver does not exist on the system", HttpStatus.BAD_REQUEST));
            }

            if (!authInfo.getAllUserPermissionIdsForUser().contains(LSLBAuthPermissionReferenceData.APPROVE_APPLICATION_FORM_ID)) {
                return Mono.just(new ResponseEntity<>("User does not have permission to approve applications", HttpStatus.BAD_REQUEST));
            }

            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }

            if (!StringUtils.equals(ApplicationFormStatusReferenceData.IN_REVIEW_STATUS_ID, applicationForm.getApplicationFormStatusId())) {
                return Mono.just(new ResponseEntity<>("Application form not yet submitted", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.equals(ApplicationFormStatusReferenceData.REJECTED_STATUS_ID, applicationForm.getApplicationFormStatusId())) {
                return Mono.just(new ResponseEntity<>("Application already rejected", HttpStatus.BAD_REQUEST));
            }
            if (!applicationForm.getReadyForApproval()) {
                return Mono.just(new ResponseEntity<>("Not all documents on this application are approved", HttpStatus.BAD_REQUEST));
            }

            applicationForm.setApproverId(approverId);
            String approvedApplicationFormStatusId = ApplicationFormStatusReferenceData.APPROVED_STATUS_ID;
            applicationForm.setApplicationFormStatusId(approvedApplicationFormStatusId);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Approved application form : %s ->  ", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            applicationFormNotificationHelperAsync.sendApprovedMailToInstitutionAdmins(applicationForm);
            return Mono.just(new ResponseEntity<>("Application form approved successfully", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving application form", e);
        }
    }


    @Override
    public Mono<ResponseEntity> approveAIPForm(String institutionId, String gameTypeId, String approverId, HttpServletRequest request) {
        try {
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(approverId, AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Approver does not exist on the system", HttpStatus.BAD_REQUEST));
            }

            if (!authInfo.getAllUserPermissionIdsForUser().contains(LSLBAuthPermissionReferenceData.APPROVE_APPLICATION_FORM_ID)) {
                return Mono.just(new ResponseEntity<>("User does not have permission to approve applications", HttpStatus.BAD_REQUEST));
            }
            Query queryAIPDocumentApproval= new Query();
            queryAIPDocumentApproval.addCriteria(Criteria.where("institutionId").is(institutionId));
            queryAIPDocumentApproval.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));

            AIPDocumentApproval aipDocumentApproval = (AIPDocumentApproval)mongoRepositoryReactive.find(queryAIPDocumentApproval, AIPDocumentApproval.class).block();
            if (aipDocumentApproval == null) {
                return Mono.just(new ResponseEntity<>("AIP form does not exist", HttpStatus.BAD_REQUEST));
            }

            if (!StringUtils.equals(ApplicationFormStatusReferenceData.IN_REVIEW_STATUS_ID, aipDocumentApproval.getFormStatusId())){
                return Mono.just(new ResponseEntity<>("AIP form not yet submitted", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.equals(ApplicationFormStatusReferenceData.REJECTED_STATUS_ID, aipDocumentApproval.getFormStatusId())) {
                return Mono.just(new ResponseEntity<>("AIP already rejected", HttpStatus.BAD_REQUEST));
            }
            if (!aipDocumentApproval.getReadyForApproval()) {
                return Mono.just(new ResponseEntity<>("Not all documents on this application are approved", HttpStatus.BAD_REQUEST));
            }

            aipDocumentApproval.setApproverId(approverId);
            String approvedAIPFormStatusId = ApplicationFormStatusReferenceData.APPROVED_STATUS_ID;
            aipDocumentApproval.setFormStatusId(approvedAIPFormStatusId);
            saveAIPForm(aipDocumentApproval);
            licenseService.updateAIPDocToLicense(institutionId,gameTypeId);


            String verbiage = String.format("Approved AIP form : %s ->  ", aipDocumentApproval.getFormStatusId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), aipDocumentApproval.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            applicationFormNotificationHelperAsync.sendApprovedMailToInstitutionAdmins(aipDocumentApproval);
            return Mono.just(new ResponseEntity<>("Application form approved successfully", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving application form", e);
        }
    }

    @Override
    public Mono<ResponseEntity> completeApplicationForm(String applicationFormId, boolean isResubmit, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }
            String inReviewApplicationFormStatusId = ApplicationFormStatusReferenceData.IN_REVIEW_STATUS_ID;
            applicationForm.setApplicationFormStatusId(inReviewApplicationFormStatusId);
            applicationForm.setSubmissionDate(LocalDate.now());
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Submitted application form : %s ->  ", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            applicationFormNotificationHelperAsync.sendApplicationFormSubmissionMailToLSLBAdmins(applicationForm);
            return Mono.just(new ResponseEntity<>("Application completed successfully and now in review", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while completing application form", e);
        }
    }


    @Override
    public Mono<ResponseEntity> completeAIPForm(String institutionId,String gameTypeId,  HttpServletRequest request) {
        try {
            Query queryDocument= new Query();
            queryDocument.addCriteria(Criteria.where("institutionId").is(institutionId));
            queryDocument.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));

            AIPDocumentApproval aipDocumentApproval = (AIPDocumentApproval) mongoRepositoryReactive.find(queryDocument, AIPDocumentApproval.class).block();
            if (aipDocumentApproval == null) {
                return Mono.just(new ResponseEntity<>("AIP form does not exist", HttpStatus.BAD_REQUEST));
            }
            String inReviewApplicationFormStatusId = ApplicationFormStatusReferenceData.IN_REVIEW_STATUS_ID;
            aipDocumentApproval.setFormStatusId(inReviewApplicationFormStatusId);
            aipDocumentApproval.setSubmissionDate(LocalDate.now());
            saveAIPForm(aipDocumentApproval);
            Query queryAIP= new Query();
            queryAIP.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID));
            queryAIP.addCriteria(Criteria.where("institutionId").is(institutionId));
            queryAIP.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));

            License license=(License)mongoRepositoryReactive.find(queryAIP, License.class).block();
            licenseService.updateToDocumentAIP(license.getId());

            String verbiage = String.format("Submitted AIP form : %s ->  ", aipDocumentApproval.getFormStatusId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), aipDocumentApproval.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            applicationFormNotificationHelperAsync.sendAIPFormSubmissionMailToLSLBAdmins(aipDocumentApproval);
            return Mono.just(new ResponseEntity<>("AIP completed successfully and now in review", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while completing application form", e);
        }
    }
    @Override
    public Mono<ResponseEntity> getPaymentRecordsForApplicationForm(String applicationFormId) {
        try {
            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }
            Set<String> paymentRecordIds = applicationForm.getPaymentRecordIds();
            if (paymentRecordIds == null || paymentRecordIds.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            List<PaymentRecordDto> paymentRecordDtoList = new ArrayList<>();
            for (String paymentRecordId : paymentRecordIds) {
                PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.findById(paymentRecordId, PaymentRecord.class).block();
                if (paymentRecord != null) {
                    paymentRecordDtoList.add(paymentRecord.convertToDto());
                }
            }
            return Mono.just(new ResponseEntity<>(paymentRecordDtoList, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting payment records for application form", e);
        }
    }

    @Override
    public Mono<ResponseEntity> rejectApplicationForm(String applicationFormId, ApplicationFormRejectDto applicationFormRejectDto, HttpServletRequest request) {
        try {
            String rejectorId = applicationFormRejectDto.getUserId();
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(rejectorId, AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Rejecting user does not exist on the system", HttpStatus.BAD_REQUEST));
            }

            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }

            if (StringUtils.equals(ApplicationFormStatusReferenceData.APPROVED_STATUS_ID, applicationForm.getApplicationFormStatusId())) {
                return Mono.just(new ResponseEntity<>("Application already approved", HttpStatus.BAD_REQUEST));
            }
            applicationForm.setRejectorId(rejectorId);
            applicationForm.setReasonForRejection(applicationFormRejectDto.getReason());
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.REJECTED_STATUS_ID);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Rejected application form : %s ->  ", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            applicationFormNotificationHelperAsync.sendRejectionMailToInstitutionAdmins(applicationForm);
            return Mono.just(new ResponseEntity<>("Application form rejected successfully", HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while rejecting application form", e);
        }
    }

    @Override
    public Mono<ResponseEntity> addCommentsToFormFromLslbAdmin(String applicationFormId, ApplicationFormCreateCommentDto formCreateCommentDto, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }
            if (!StringUtils.equals(ApplicationFormStatusReferenceData.IN_REVIEW_STATUS_ID, applicationForm.getApplicationFormStatusId())) {
                return Mono.just(new ResponseEntity<>("Application form status has to be IN REVIEW for you to add a comment", HttpStatus.BAD_REQUEST));
            }
            AuthInfo lslbAdmin = authInfoService.getUserById(formCreateCommentDto.getUserId());
            if (lslbAdmin == null) {
                return Mono.just(new ResponseEntity<>("Commenting user does not exist", HttpStatus.BAD_REQUEST));
            }
            LslbAdminComment lslbAdminComment = new LslbAdminComment(formCreateCommentDto.getUserId(), formCreateCommentDto.getComment());
            applicationForm.setLslbAdminComment(lslbAdminComment);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.PENDING_RESUBMISSON_ID);
            saveApplicationForm(applicationForm);

            String verbiage = String.format("Added comment to application form : %s ->  ", applicationForm.getApplicationFormId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            applicationFormNotificationHelperAsync.sendAdminCommentNotificationToInstitutionAdmins(applicationForm);
            return Mono.just(new ResponseEntity<>("Comment added successfully", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment to application form", e);
        }
    }


    @Override
    public Mono<ResponseEntity> addCommentsToAIPFormFromLslbAdmin(String aipFormId, FormCreateCommentDto formCreateCommentDto, HttpServletRequest request) {
        try {


            AIPDocumentApproval aipDocumentApproval = getAIPFormById(aipFormId);

            if (aipDocumentApproval == null) {
                return Mono.just(new ResponseEntity<>("AIP form does not exist", HttpStatus.BAD_REQUEST));
            }
            if (!StringUtils.equals(ApplicationFormStatusReferenceData.IN_REVIEW_STATUS_ID, aipDocumentApproval.getFormStatusId())) {
                return Mono.just(new ResponseEntity<>("Aip form status has to be IN REVIEW for you to add a comment", HttpStatus.BAD_REQUEST));
            }
            AuthInfo lslbAdmin = authInfoService.getUserById(formCreateCommentDto.getUserId());
            if (lslbAdmin == null) {
                return Mono.just(new ResponseEntity<>("Commenting user does not exist", HttpStatus.BAD_REQUEST));
            }
            LslbAdminComment lslbAdminComment = new LslbAdminComment(formCreateCommentDto.getUserId(), formCreateCommentDto.getComment());
            aipDocumentApproval.setLslbAdminComment(lslbAdminComment);
            aipDocumentApproval.setFormStatusId(ApplicationFormStatusReferenceData.PENDING_RESUBMISSON_ID);
            saveAIPForm(aipDocumentApproval);

            String verbiage = String.format("Added comment to AIP form : %s ->  ", aipDocumentApproval.getFormStatusId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), aipDocumentApproval.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            applicationFormNotificationHelperAsync.sendAdminCommentNotificationToInstitutionAdmins(aipDocumentApproval, lslbAdminComment.getComment());
            return Mono.just(new ResponseEntity<>("Comment added successfully", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment to application form", e);
        }
    }

    @Override
    public boolean institutionHasCompletedApplicationForGameType(String institutionId, String gameTypeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("applicationFormStatusId").is(ApplicationFormStatusReferenceData.APPROVED_STATUS_ID));

        ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.find(query, ApplicationForm.class).block();
        return applicationForm != null;
    }

    @Override
    public ApplicationForm findApplicationFormById(String applicationFormId) {
        if (StringUtils.isEmpty(applicationFormId)) {
            return null;
        }
        return (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
    }

    @Override
    public void approveApplicationFormDocument(Document document) {
        ApplicationForm applicationForm = document.getApplicationForm();
        String documentId = document.getId();
        if (applicationForm != null) {
            FormDocumentApproval formDocumentApproval = applicationForm.getDocumentApproval();
            if (formDocumentApproval != null) {
                Map<String, Boolean> documentApprovalMap = formDocumentApproval.getApprovalMap();
                documentApprovalMap.put(documentId, true);
                Collection<Boolean> values = documentApprovalMap.values();
                int count = 0;
                for (Boolean value : values) {
                    if (value) {
                        count = count + 1;
                    }
                }
                if (count == formDocumentApproval.getSupposedLength()) {
                    applicationFormNotificationHelperAsync.sendApproverMailToFinalApproval(applicationForm);
                    formDocumentApproval.setComplete(true);
                }
                formDocumentApproval.setApprovalMap(documentApprovalMap);
            }
            applicationForm.setDocumentApproval(formDocumentApproval);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);
        }
    }

    @Override
    public void rejectApplicationFormDocument(Document document) {
        ApplicationForm applicationForm = document.getApplicationForm();
        String documentId = document.getId();
        if (applicationForm != null) {
            FormDocumentApproval formDocumentApproval = applicationForm.getDocumentApproval();
            if (formDocumentApproval != null) {
                Map<String, Boolean> documentApprovalMap = formDocumentApproval.getApprovalMap();
                documentApprovalMap.put(documentId, false);
                applicationFormNotificationHelperAsync.sendDocumentReturnMailToInstitutionMembers(applicationForm, document);
                formDocumentApproval.setApprovalMap(documentApprovalMap);
                applicationForm.setDocumentApproval(formDocumentApproval);
            }
            mongoRepositoryReactive.saveOrUpdate(applicationForm);
        }
    }
    @Override
    public void rejectAIPFormDocument(Document document) {
        AIPDocumentApproval aipDocumentApproval = document.getAIPForm();
        String documentId = document.getId();
        if (aipDocumentApproval != null) {
            FormDocumentApproval formDocumentApproval = aipDocumentApproval.getDocumentApproval();
            if (formDocumentApproval != null) {
                Map<String, Boolean> documentApprovalMap = formDocumentApproval.getApprovalMap();
                documentApprovalMap.put(documentId, false);
                applicationFormNotificationHelperAsync.sendDocumentReturnMailToInstitutionMembers(aipDocumentApproval, document);
                formDocumentApproval.setApprovalMap(documentApprovalMap);
                aipDocumentApproval.setDocumentApproval(formDocumentApproval);
            }
            mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
        }
    }

    @Override
    public void doDocumentReuploadNotification(Document document) {
        ApplicationForm applicationForm = document.getApplicationForm();
        String documentId = document.getId();
        if (applicationForm != null) {
            FormDocumentApproval formDocumentApproval = applicationForm.getDocumentApproval();
            formDocumentApproval.getApprovalMap().put(documentId, false);
            applicationForm.setDocumentApproval(formDocumentApproval);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);
            applicationFormNotificationHelperAsync.sendResubmissionNotificationForApplicationForm(applicationForm, document);
        }
    }

    @Override
    public void doAIPDocumentReuploadNotification(Document document) {
        AIPDocumentApproval aipDocumentApproval = document.getAIPForm();
        String documentId = document.getId();
        if (aipDocumentApproval != null) {
            FormDocumentApproval formDocumentApproval = aipDocumentApproval.getDocumentApproval();
            formDocumentApproval.getApprovalMap().put(documentId, false);
            aipDocumentApproval.setDocumentApproval(formDocumentApproval);
            mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
            applicationFormNotificationHelperAsync.sendResubmissionNotificationForApplicationForm(aipDocumentApproval, document);
        }
    }

    @Override
    public void approveAIPFormDocument(Document document) {
        AIPDocumentApproval aipDocumentApproval = document.getAIPForm();
        String documentId = document.getId();
        if (aipDocumentApproval != null) {
            FormDocumentApproval formDocumentApproval = aipDocumentApproval.getDocumentApproval();
            if (formDocumentApproval != null) {
                Map<String, Boolean> documentApprovalMap = formDocumentApproval.getApprovalMap();
                documentApprovalMap.put(documentId, true);
                Collection<Boolean> values = documentApprovalMap.values();
                int count = 0;
                for (Boolean value : values) {
                    if (value) {
                        count = count + 1;
                    }
                }
                if (count == formDocumentApproval.getSupposedLength()) {
                    applicationFormNotificationHelperAsync.sendApproverMailToFinalApproval(aipDocumentApproval);
                    formDocumentApproval.setComplete(true);
                }
                formDocumentApproval.setApprovalMap(documentApprovalMap);
            }
            aipDocumentApproval.setDocumentApproval(formDocumentApproval);
            mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
        }
    }

    @Override
    public Mono<ResponseEntity> addCommentsToForm(String applicationFormId, AddCommentDto addCommentDto, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = findApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            FormComment comment = new FormComment();
            comment.setTimeCreated(LocalDateTime.now());
            comment.setUserFullName(loggedInUser.getFullName());
            comment.setComment(addCommentDto.getComment());
            applicationForm.getFormComments().add(comment);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);

            String verbiage = String.format("Added comment to application form  :Form Id -> %s ->  ,Category : -> %s , Comment -> %s",
                    applicationForm.getApplicationFormId(), applicationForm.getGameTypeName(), addCommentDto.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantDetailsComment(String applicationFormId, AddCommentDto addCommentDto, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = findApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            ApplicantDetails applicantDetails = applicationForm.getApplicantDetails();
            if (applicantDetails == null) {
                return Mono.just(new ResponseEntity<>("Applicant has not filled applicant details", HttpStatus.BAD_REQUEST));
            }

            CommentDto comment = new CommentDto();
            comment.setCommentDate(LocalDate.now().toString("dd-MM-yyyy"));
            comment.setCommentTime(LocalDateTime.now().toString("HH:mm:ss a"));
            comment.setUserFullName(loggedInUser.getFullName());
            comment.setComment(addCommentDto.getComment());
            applicationForm.getApplicantDetails().getComments().add(comment);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);

            String verbiage = String.format("Added comment to application form  , Applicant Details :Form Id -> %s ->  ,Category : -> %s , Comment -> %s",
                    applicationForm.getApplicationFormId(), applicationForm.getGameTypeName(), addCommentDto.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantMembersDetailsComment(String applicationFormId, AddCommentDto addCommentDto, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = findApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            ApplicantMemberDetails memberDetails = applicationForm.getApplicantMemberDetails();
            if (memberDetails == null) {
                return Mono.just(new ResponseEntity<>("Applicant has not filled members details", HttpStatus.BAD_REQUEST));
            }

            CommentDto comment = new CommentDto();
            comment.setCommentDate(LocalDate.now().toString("dd-MM-yyyy"));
            comment.setCommentTime(LocalDateTime.now().toString("HH:mm:ss a"));
            comment.setUserFullName(loggedInUser.getFullName());
            comment.setComment(addCommentDto.getComment());
            applicationForm.getApplicantMemberDetails().getComments().add(comment);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);

            String verbiage = String.format("Added comment to application form  , Applicant Member Details :Form Id -> %s ->  ,Category : -> %s , Comment -> %s",
                    applicationForm.getApplicationFormId(), applicationForm.getGameTypeName(), addCommentDto.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantContactDetailsComment(String applicationFormId, AddCommentDto addCommentDto, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = findApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            ApplicantContactDetails contactDetails = applicationForm.getApplicantContactDetails();
            if (contactDetails == null) {
                return Mono.just(new ResponseEntity<>("Applicant has not filled contact details", HttpStatus.BAD_REQUEST));
            }

            CommentDto comment = new CommentDto();
            comment.setCommentDate(LocalDate.now().toString("dd-MM-yyyy"));
            comment.setCommentTime(LocalDateTime.now().toString("HH:mm:ss a"));
            comment.setUserFullName(loggedInUser.getFullName());
            comment.setComment(addCommentDto.getComment());
            applicationForm.getApplicantContactDetails().getComments().add(comment);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);

            String verbiage = String.format("Added comment to application form  , Applicant Contact Details :Form Id -> %s ->  ,Category : -> %s , Comment -> %s",
                    applicationForm.getApplicationFormId(), applicationForm.getGameTypeName(), addCommentDto.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantCriminalityDetailsComment(String applicationFormId, AddCommentDto addCommentDto, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = findApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            ApplicantCriminalityDetails criminalityDetails = applicationForm.getApplicantCriminalityDetails();
            if (criminalityDetails == null) {
                return Mono.just(new ResponseEntity<>("Applicant has not filled criminality details", HttpStatus.BAD_REQUEST));
            }

            CommentDto comment = new CommentDto();
            comment.setCommentDate(LocalDate.now().toString("dd-MM-yyyy"));
            comment.setCommentTime(LocalDateTime.now().toString("HH:mm:ss a"));
            comment.setUserFullName(loggedInUser.getFullName());
            comment.setComment(addCommentDto.getComment());
            applicationForm.getApplicantCriminalityDetails().getComments().add(comment);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);

            String verbiage = String.format("Added comment to application form  , Applicant Criminality Details :Form Id -> %s ->  ,Category : -> %s , Comment -> %s",
                    applicationForm.getApplicationFormId(), applicationForm.getGameTypeName(), addCommentDto.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantDeclarationDetailsComment(String applicationFormId, AddCommentDto addCommentDto, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = findApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            CommentDto comment = new CommentDto();
            comment.setCommentDate(LocalDate.now().toString("dd-MM-yyyy"));
            comment.setCommentTime(LocalDateTime.now().toString("HH:mm:ss a"));
            comment.setUserFullName(loggedInUser.getFullName());
            comment.setComment(addCommentDto.getComment());
            ApplicantDeclarationDetails declarationDetails = applicationForm.getApplicantDeclarationDetails();
            if (declarationDetails == null) {
                return Mono.just(new ResponseEntity<>("Applicant has not filled declaration details", HttpStatus.BAD_REQUEST));
            }
            applicationForm.getApplicantDeclarationDetails().getComments().add(comment);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);

            String verbiage = String.format("Added comment to application form  , Applicant Declaration Details :Form Id -> %s ->  ,Category : -> %s , Comment -> %s",
                    applicationForm.getApplicationFormId(), applicationForm.getGameTypeName(), addCommentDto.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantOtherInformationComment(String applicationFormId, AddCommentDto addCommentDto, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = findApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            ApplicantOtherInformation otherInformation = applicationForm.getApplicantOtherInformation();
            if (otherInformation == null) {
                return Mono.just(new ResponseEntity<>("Applicant has not filled other information", HttpStatus.BAD_REQUEST));
            }

            CommentDto comment = new CommentDto();
            comment.setCommentDate(LocalDate.now().toString("dd-MM-yyyy"));
            comment.setCommentTime(LocalDateTime.now().toString("HH:mm:ss a"));
            comment.setUserFullName(loggedInUser.getFullName());
            comment.setComment(addCommentDto.getComment());
            applicationForm.getApplicantOtherInformation().getComments().add(comment);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);

            String verbiage = String.format("Added comment to application form  , Applicant Details :Form Id -> %s ->  ,Category : -> %s , Comment -> %s",
                    applicationForm.getApplicationFormId(), applicationForm.getGameTypeName(), addCommentDto.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment", e);
        }
    }

    @Override
    public Mono<ResponseEntity> saveApplicantOutletInformationComment(String applicationFormId, AddCommentDto addCommentDto, HttpServletRequest request) {
        try {
            ApplicationForm applicationForm = findApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            ApplicantOutletInformation outletInformation = applicationForm.getApplicantOutletInformation();
            if (outletInformation == null) {
                return Mono.just(new ResponseEntity<>("Applicant has not filled outlet information", HttpStatus.BAD_REQUEST));
            }

            CommentDto comment = new CommentDto();
            comment.setCommentDate(LocalDate.now().toString("dd-MM-yyyy"));
            comment.setCommentTime(LocalDateTime.now().toString("HH:mm:ss a"));
            comment.setUserFullName(loggedInUser.getFullName());
            comment.setComment(addCommentDto.getComment());
            applicationForm.getApplicantOutletInformation().getComments().add(comment);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);

            String verbiage = String.format("Added comment to application form  , Applicant Outlet information :Form Id -> %s ->  ,Category : -> %s , Comment -> %s",
                    applicationForm.getApplicationFormId(), applicationForm.getGameTypeName(), addCommentDto.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(applicationAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), applicationForm.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment", e);
        }
    }

    private ApplicationForm fromCreateDto(ApplicationFormCreateDto applicationFormCreateDto) {
        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setId(UUID.randomUUID().toString());
        BeanUtils.copyProperties(applicationFormCreateDto, applicationForm);
        GameType gameType = applicationForm.getGameType();
        String gameTypePref = "";
        if (gameType != null && !StringUtils.isEmpty(gameType.getShortCode())) {
            gameTypePref = String.format("-%s", gameType.getShortCode());
        }
        LocalDateTime presentTime = LocalDateTime.now();
        String applicationFormId = String.format("LSLB-APP%s-%s%s%s", gameTypePref, NumberUtil.getRandomNumberInRange(10, 100), presentTime.getSecondOfMinute(), presentTime.getMinuteOfHour());
        applicationForm.setApplicationFormId(applicationFormId);
        return applicationForm;
    }

    public ApplicationForm getApplicationFormById(String applicationFormId) {
        return (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
    }

    public AIPDocumentApproval getAIPFormById(String aipFormId) {
        return (AIPDocumentApproval) mongoRepositoryReactive.findById(aipFormId, AIPDocumentApproval.class).block();
    }

    public void saveApplicationForm(ApplicationForm applicationForm) {
        mongoRepositoryReactive.saveOrUpdate(applicationForm);
    }

    public void saveAIPForm(AIPDocumentApproval aipDocumentApproval) {
        mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
    }

    private Mono<ResponseEntity> validateCreateApplicationForm(ApplicationFormCreateDto applicationFormCreateDto) {
        String institutionId = applicationFormCreateDto.getInstitutionId();
        String gameTypeId = applicationFormCreateDto.getGameTypeId();
        String rejectedStatusId = ApplicationFormStatusReferenceData.REJECTED_STATUS_ID;
        Query query = new Query();
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("applicationFormStatusId").ne(rejectedStatusId));

        ApplicationForm existingApplicationFormForInstitutionWithGameTypeAndApplicationType = (ApplicationForm) mongoRepositoryReactive.find(query, ApplicationForm.class).block();
        if (existingApplicationFormForInstitutionWithGameTypeAndApplicationType != null) {
            return Mono.just(new ResponseEntity<>("You already have an ongoing application in the current category", HttpStatus.BAD_REQUEST));
        }

        PaymentRecord existingConfirmedPaymentRecord = paymentRecordService.findExistingConfirmedApplicationFeeForInstitutionAndGameType(institutionId, gameTypeId);
        if (existingConfirmedPaymentRecord == null) {
            return Mono.just(new ResponseEntity<>("Kindly make application fee payment for category to proceed", HttpStatus.BAD_REQUEST));
        }
        return null;
    }
}
