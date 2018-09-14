package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.model.applicantDetails.ApplicantDetails;
import com.software.finatech.lslb.cms.service.model.applicantMembers.ApplicantMemberDetails;
import com.software.finatech.lslb.cms.service.model.contactDetails.ApplicantContactDetails;
import com.software.finatech.lslb.cms.service.model.criminalityDetails.ApplicantCriminalityDetails;
import com.software.finatech.lslb.cms.service.model.declaration.ApplicantDeclarationDetails;
import com.software.finatech.lslb.cms.service.model.otherInformation.ApplicantOtherInformation;
import com.software.finatech.lslb.cms.service.model.outletInformation.ApplicantOutletInformation;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.ApplicationFormStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.ApplicationFormService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class ApplicationFormServiceImpl implements ApplicationFormService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationFormServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private MailContentBuilderService mailContentBuilderService;
    private EmailService emailService;
    private AuthInfoService authInfoService;
    private PaymentRecordService paymentRecordService;

    @Autowired
    public ApplicationFormServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                      MailContentBuilderService mailContentBuilderService,
                                      EmailService emailService,
                                      AuthInfoService authInfoService,
                                      PaymentRecordService paymentRecordService) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.mailContentBuilderService = mailContentBuilderService;
        this.emailService = emailService;
        this.authInfoService = authInfoService;
        this.paymentRecordService = paymentRecordService;
    }

    @Override
    public Mono<ResponseEntity> createApplicationForm(ApplicationFormCreateDto applicationFormCreateDto) {
        try {
            Mono<ResponseEntity> validateCreateApplicationFormResponse = validateCreateApplicationForm(applicationFormCreateDto);
            if (validateCreateApplicationFormResponse != null) {
                return validateCreateApplicationFormResponse;
            }

            ApplicationForm applicationForm = fromCreateDto(applicationFormCreateDto);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.CREATED_STATUS_ID);
            saveApplicationForm(applicationForm);
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
                                                       String applicationFormTypeId,
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
            if (!StringUtils.isEmpty(applicationFormTypeId)) {
                query.addCriteria(Criteria.where("applicationFormTypeId").is(applicationFormTypeId));
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
    public Mono<ResponseEntity> getAllApplicationFormTypes() {
        try {
            ArrayList<ApplicationFormType> applicationFormTypes = (ArrayList<ApplicationFormType>) mongoRepositoryReactive
                    .findAll(new Query(), ApplicationFormType.class).toStream().collect(Collectors.toList());

            if (applicationFormTypes == null || applicationFormTypes.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.OK));
            }
            List<EnumeratedFactDto> applicationFormTypeDtos = new ArrayList<>();
            applicationFormTypes.forEach(applicationFormType -> {
                applicationFormTypeDtos.add(applicationFormType.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(applicationFormTypeDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while getting all application form types";
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

    //TODO: find way to get all approverID distinct in application form table
    @Override
    public Mono<ResponseEntity> getAllApprovers() {
        String lslbAdminAuthRoleId = LSLBAuthRoleReferenceData.LSLB_ADMIN_ROLE_ID;
        Query query = new Query();
        query.addCriteria(Criteria.where("authRoleId").is(lslbAdminAuthRoleId));
        ArrayList<AuthInfo> authInfos = (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());
        if (authInfos == null || authInfos.isEmpty()) {
            return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
        }
        List<AuthInfoDto> authInfoDtos = new ArrayList<>();
        authInfos.forEach(authInfo -> {
            try {
                authInfo.setAssociatedProperties();
                AuthInfoDto authInfoDto = new AuthInfoDto();
                authInfoDto.setId(authInfo.getId());
                authInfoDto.setFullName(authInfo.getFullName());
                authInfoDtos.add(authInfoDto);
            } catch (FactNotFoundException e) {
                e.printStackTrace();
            }
        });
        return Mono.just(new ResponseEntity<>(authInfoDtos, HttpStatus.OK));
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
    public Mono<ResponseEntity> saveApplicantDetails(String applicationFormId, ApplicantDetails applicantDetails) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantDetails(applicantDetails);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);
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
    public Mono<ResponseEntity> saveApplicantMembersDetails(String applicationFormId, ApplicantMemberDetails applicantMemberDetails) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantMemberDetails(applicantMemberDetails);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);
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
    public Mono<ResponseEntity> saveApplicantContactDetails(String applicationFormId, ApplicantContactDetails applicantContactDetails) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantContactDetails(applicantContactDetails);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);
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
    public Mono<ResponseEntity> saveApplicantCriminalityDetails(String applicationFormId, ApplicantCriminalityDetails applicantCriminalityDetails) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantCriminalityDetails(applicantCriminalityDetails);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);
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
    public Mono<ResponseEntity> saveApplicantDeclarationDetails(String applicationFormId, ApplicantDeclarationDetails applicantDeclarationDetails) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantDeclarationDetails(applicantDeclarationDetails);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);
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
    public Mono<ResponseEntity> saveApplicantOtherInformation(String applicationFormId, ApplicantOtherInformation applicantOtherInformation) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantOtherInformation(applicantOtherInformation);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);
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
    public Mono<ResponseEntity> saveApplicantOutletInformation(String applicationFormId, ApplicantOutletInformation applicantOutletInformation) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>(String.format("Application form with id %s does not exist", applicationFormId), HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApplicantOutletInformation(applicantOutletInformation);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.IN_PROGRESS_STATUS_ID);
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving applicant outlet details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> approveApplicationForm(String applicationFormId, String approverId) {
        try {
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(approverId, AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Approver does not exist on the system", HttpStatus.BAD_REQUEST));
            }

            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }
            applicationForm.setApproverId(approverId);
            String approvedApplicationFormStatusId = ApplicationFormStatusReferenceData.APPROVED_STATUS_ID;
            applicationForm.setApplicationFormStatusId(approvedApplicationFormStatusId);
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>("Application form approved successfully", HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving application form", e);
        }
    }

    @Override
    public Mono<ResponseEntity> completeApplicationForm(String applicationFormId, boolean isResubmit) {
        try {
            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }
            String inReviewApplicationFormStatusId = ApplicationFormStatusReferenceData.IN_REVIEW_STATUS_ID;
            applicationForm.setApplicationFormStatusId(inReviewApplicationFormStatusId);
            saveApplicationForm(applicationForm);

            if (isResubmit) {
                sendCompleteApplicationNotificationToLslbAdmin(applicationForm);
            }
            return Mono.just(new ResponseEntity<>("Application completed successfully and now in review", HttpStatus.OK));
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
    public Mono<ResponseEntity> rejectApplicationForm(String applicationFormId, String rejectorId) {
        try {
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(rejectorId, AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Rejecting user does not exist on the system", HttpStatus.BAD_REQUEST));
            }

            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }
            applicationForm.setRejectorId(rejectorId);
            String rejectedApplicationFormStatusId = ApplicationFormStatusReferenceData.REJECTED_STATUS_ID;
            applicationForm.setApplicationFormStatusId(rejectedApplicationFormStatusId);
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>("Application form rejected successfully", HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while rejecting application form", e);
        }
    }

    /*@Override
    public Mono<ResponseEntity> getDocumentTypesForApplicationForm(String applicationFormId) {
        try {
            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }

            Query queryForApplicationFormDocumentTypes = new Query();
            String applicationFormDocumentPurposeId = DocumentPurposeReferenceData.APPLICATION_FORM_DOCUMENT_PURPOSE_ID;
            String gameTypeId = applicationForm.getGameTypeId();

            queryForApplicationFormDocumentTypes.addCriteria(Criteria.where("documentPurposeId").is(applicationFormDocumentPurposeId));
            queryForApplicationFormDocumentTypes.addCriteria(Criteria.where("gameTypeIds").in(gameTypeId));
            queryForApplicationFormDocumentTypes.addCriteria(Criteria.where("active").is(true));

            ArrayList<DocumentType> documentTypes = (ArrayList<DocumentType>) mongoRepositoryReactive.findAll(queryForApplicationFormDocumentTypes, DocumentType.class).toStream().collect(Collectors.toList());
            if (documentTypes == null || documentTypes.isEmpty()){
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            ArrayList<ApplicationFormDocumentDto> applicationFormDocumentDtos = new ArrayList<>();
            documentTypes.forEach(documentType -> {
                applicationFormDocumentDtos.add(getApplicationFormDocumentDto(applicationForm, documentType));
            });
            return Mono.just(new ResponseEntity<>(applicationFormDocumentDtos, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting documents for application form", e);
        }
    }

    private ApplicationFormDocumentDto getApplicationFormDocumentDto(ApplicationForm applicationForm, DocumentType documentType) {
        ApplicationFormDocumentDto applicationFormDocumentDto = new ApplicationFormDocumentDto();
        applicationFormDocumentDto.setRequired(documentType.isRequired());
        applicationFormDocumentDto.setActive(documentType.isActive());
        applicationFormDocumentDto.setName(documentType.getName());
        applicationFormDocumentDto.setDescription(documentType.getDescription());
        applicationFormDocumentDto.setId(documentType.getId());

        Query queryForDocumentWithTypeAndApplicationForm = new Query();
        queryForDocumentWithTypeAndApplicationForm.addCriteria(Criteria.where("applicationFormId").is(applicationForm.getId()));
        queryForDocumentWithTypeAndApplicationForm.addCriteria(Criteria.where("documentTypeId").is(documentType.getId()));

        Document document = (Document) mongoRepositoryReactive.find(queryForDocumentWithTypeAndApplicationForm, Document.class).block();

        if (document != null) {
            String documentId = document.getId();
            Set<String> applicationFormDocumentIds = applicationForm.getDocumentIds();
            applicationFormDocumentDto.setUploaded(applicationFormDocumentIds.contains(documentId));
        }
        return applicationFormDocumentDto;
    }*/

    @Override
    public Mono<ResponseEntity> addCommentsToFormFromLslbAdmin(String applicationFormId, ApplicationFormCreateCommentDto applicationFormCreateCommentDto) {
        try {
            ApplicationForm applicationForm = getApplicationFormById(applicationFormId);
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }
            if (!StringUtils.equals(ApplicationFormStatusReferenceData.IN_REVIEW_STATUS_ID, applicationForm.getApplicationFormStatusId())) {
                return Mono.just(new ResponseEntity<>("Application form status has to be IN REVIEW for you to add a comment", HttpStatus.BAD_REQUEST));
            }
            AuthInfo lslbAdmin = authInfoService.getUserById(applicationFormCreateCommentDto.getUserId());
            if (lslbAdmin == null) {
                return Mono.just(new ResponseEntity<>("Commenting user does not exist", HttpStatus.BAD_REQUEST));
            }
            LslbAdminComment lslbAdminComment = new LslbAdminComment(applicationFormCreateCommentDto.getUserId(), applicationFormCreateCommentDto.getComment());
            applicationForm.setLslbAdminComment(lslbAdminComment);
            applicationForm.setApplicationFormStatusId(ApplicationFormStatusReferenceData.PENDING_RESUBMISSON_ID);
            saveApplicationForm(applicationForm);
            sendAdminCommentNotificationToInstitutionAdmins(applicationForm, lslbAdminComment.getComment());
            return Mono.just(new ResponseEntity<>("Comment added successfully", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment to application form", e);
        }
    }

    public void sendCompleteApplicationNotificationToLslbAdmin(ApplicationForm applicationForm) {
        LslbAdminComment lslbAdminComment = applicationForm.getLslbAdminComment();
        if (lslbAdminComment == null) {
            return;
        }
        sendCompletionNotificationToLslbAdmin(applicationForm, lslbAdminComment.getUserId());
    }

    private void sendCompletionNotificationToLslbAdmin(ApplicationForm applicationForm, String lslbAdminId) {
        Institution institution = applicationForm.getInstitution();
        AuthInfo lslbAdmin = authInfoService.getUserById(lslbAdminId);
        String presentDate = DateTime.now().toString("dd/MM/yyyy");
        String gameTypeName = applicationForm.getGameTypeName();
        String institutionName = institution.getInstitutionName();


        HashMap<String, Object> model = new HashMap<>();
        model.put("name", lslbAdmin.getFullName());
        model.put("institutionName", institutionName);
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);

        String content = mailContentBuilderService.build(model, "ApplicationFormCompleteUploadNotificationLslbAdmin");
        String mailSubject = String.format("%s has re application form for %s", institutionName, gameTypeName);

        emailService.sendEmail(content, mailSubject, lslbAdmin.getEmailAddress());
    }

    private void sendAdminCommentNotificationToInstitutionAdmins(ApplicationForm applicationForm, String comment) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(applicationForm.getInstitutionId());
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendCommentNotificationToInstitutionUser(institutionAdmin, comment, applicationForm);
        }
    }

    private void sendCommentNotificationToInstitutionUser(AuthInfo institutionAdmin, String comment, ApplicationForm applicationForm) {
        String institutionAdminName = institutionAdmin.getFullName();
        String presentDate = DateTime.now().toString("dd/MM/yyyy ");
        String gameTypeName = applicationForm.getGameTypeName();


        HashMap<String, Object> model = new HashMap<>();
        model.put("name", institutionAdminName);
        model.put("comment", comment);
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);

        String mailSubject = String.format("Notification on your application for %s license", gameTypeName);
        String content = mailContentBuilderService.build(model, "ApplicationFormPendingUploadGAadmin");
        emailService.sendEmail(content, mailSubject, institutionAdmin.getEmailAddress());
    }

    private ApplicationForm fromCreateDto(ApplicationFormCreateDto applicationFormCreateDto) {
        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setId(UUID.randomUUID().toString());
        BeanUtils.copyProperties(applicationFormCreateDto, applicationForm);
        return applicationForm;
    }

    public ApplicationForm getApplicationFormById(String applicationFormId) {
        return (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
    }

    public void saveApplicationForm(ApplicationForm applicationForm) {
        mongoRepositoryReactive.saveOrUpdate(applicationForm);
    }

    private Mono<ResponseEntity> validateCreateApplicationForm(ApplicationFormCreateDto applicationFormCreateDto) {
        String institutionId = applicationFormCreateDto.getInstitutionId();
        String gameTypeId = applicationFormCreateDto.getGameTypeId();
        String applicationFormTypeId = applicationFormCreateDto.getApplicationFormTypeId();
        String rejectedStatusId = ApplicationFormStatusReferenceData.REJECTED_STATUS_ID;
        Query query = new Query();
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("applicationFormTypeId").is(applicationFormTypeId));
        query.addCriteria(Criteria.where("applicationFormStatusId").ne(rejectedStatusId));

        ApplicationForm existingApplicationFormForInstitutionWithGameTypeAndApplicationType = (ApplicationForm) mongoRepositoryReactive.find(query, ApplicationForm.class).block();
        if (existingApplicationFormForInstitutionWithGameTypeAndApplicationType != null) {
            return Mono.just(new ResponseEntity<>("An application form already exists for the institution with the same game type and application type", HttpStatus.BAD_REQUEST));
        }

        PaymentRecord existingConfirmedPaymentRecord = paymentRecordService.findExistingConfirmedApplicationFeeForInstitutionAndGameType(institutionId, gameTypeId);
        if (existingConfirmedPaymentRecord == null) {
            return Mono.just(new ResponseEntity<>("There is no confirmed application fee payment record for game type", HttpStatus.BAD_REQUEST));
        }
        return null;
    }
}
