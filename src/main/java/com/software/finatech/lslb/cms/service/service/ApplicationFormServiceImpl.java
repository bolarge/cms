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
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.ApplicationFormService;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class ApplicationFormServiceImpl implements ApplicationFormService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationFormServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public Mono<ResponseEntity> createApplicationForm(ApplicationFormCreateDto applicationFormCreateDto) {
        try {
            Mono<ResponseEntity> validateCreateApplicationFormResponse = validateCreateApplicationForm(applicationFormCreateDto);
            if (validateCreateApplicationFormResponse != null) {
                return validateCreateApplicationFormResponse;
            }

            ApplicationForm applicationForm = fromCreateDto(applicationFormCreateDto);
            applicationForm.setApplicationFormStatusId("1");
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

            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, ApplicationForm.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

            Sort sort;
            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "id");
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
        String lslbAdminAuthRoleId = "4";
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
            applicationForm.setApplicationFormStatusId("2");
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>("Saved successfully", HttpStatus.OK));
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
            applicationForm.setApplicationFormStatusId("2");
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>("Saved successfully", HttpStatus.OK));
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
            applicationForm.setApplicationFormStatusId("2");
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>("Saved successfully", HttpStatus.OK));
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
            applicationForm.setApplicationFormStatusId("2");
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>("Saved successfully", HttpStatus.OK));
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
            applicationForm.setApplicationFormStatusId("2");
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>("Saved successfully", HttpStatus.OK));
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
            applicationForm.setApplicationFormStatusId("2");
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>("Saved successfully", HttpStatus.OK));
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
            applicationForm.setApplicationFormStatusId("2");
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>("Saved successfully", HttpStatus.OK));
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
            String approvedApplicationFormStatusId = "4";
            applicationForm.setApplicationFormStatusId(approvedApplicationFormStatusId);
            saveApplicationForm(applicationForm);
            return Mono.just(new ResponseEntity<>("Application form approved successfully", HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving application form", e);
        }
    }

    @Override
    public Mono<ResponseEntity> completeApplicationForm(String applicationFormId) {
        try {
            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }
            String inReviewApplicationFormStatusId = "3";
            applicationForm.setApplicationFormStatusId(inReviewApplicationFormStatusId);
            saveApplicationForm(applicationForm);
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
                return Mono.just(new ResponseEntity<>("Approver does not exist on the system", HttpStatus.BAD_REQUEST));
            }

            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.findById(applicationFormId, ApplicationForm.class).block();
            if (applicationForm == null) {
                return Mono.just(new ResponseEntity<>("Application form does not exist", HttpStatus.BAD_REQUEST));
            }
            applicationForm.setRejectorId(rejectorId);
            String rejectedApplicationFormStatusId = "5";
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
    }*/

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
        Query query = new Query();
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("applicationFormTypeId").is(applicationFormTypeId));

        ApplicationForm existingApplicationFormForInstitutionWithGameTypeAndApplicationType = (ApplicationForm) mongoRepositoryReactive.find(query, ApplicationForm.class).block();
        if (existingApplicationFormForInstitutionWithGameTypeAndApplicationType != null) {
            return Mono.just(new ResponseEntity<>("An application form already exists for the institution with the same game type and application type", HttpStatus.BAD_REQUEST));
        }

        String applicationFeeTypeId = FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID;
        Query queryForExistingFee = new Query();
        queryForExistingFee.addCriteria(Criteria.where("feePaymentTypeId").is(applicationFeeTypeId));
        queryForExistingFee.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));

        Fee applicationFeeForGameType = (Fee) mongoRepositoryReactive.find(queryForExistingFee, Fee.class).block();
        if (applicationFeeForGameType == null) {
            return Mono.just(new ResponseEntity<>("No Application fee configured for Game Type", HttpStatus.EXPECTATION_FAILED));
        }

        Query queryForExistingConfirmedPaymentRecord = new Query();
        String confirmedPaymentStatusId = PaymentStatusReferenceData.CONFIRMED_PAYMENT_STATUS_ID;
        queryForExistingConfirmedPaymentRecord.addCriteria(Criteria.where("feeId").is(applicationFeeForGameType.getId()));
        queryForExistingConfirmedPaymentRecord.addCriteria(Criteria.where("institutionId").is(institutionId));
        queryForExistingConfirmedPaymentRecord.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        queryForExistingConfirmedPaymentRecord.addCriteria(Criteria.where("paymentStatusId").is(confirmedPaymentStatusId));

        PaymentRecord existingConfirmedPaymentRecord = (PaymentRecord) mongoRepositoryReactive.find(queryForExistingConfirmedPaymentRecord, PaymentRecord.class).block();

        if (existingConfirmedPaymentRecord == null) {
            return Mono.just(new ResponseEntity<>("There is no confirmed application fee payment record for game type", HttpStatus.BAD_REQUEST));
        }
        return null;
    }

    private List<DocumentTypeDto> getDocumentTypesFromDocumentIds(Set<String> documentIds) throws FactNotFoundException {
        List<DocumentTypeDto> documentTypeDtos = new ArrayList<>();
        for (String documentId : documentIds) {
            Document document = (Document) mongoRepositoryReactive.findById(documentId, Document.class).block();
            if (document != null) {
                document.setAssociatedProperties();
                DocumentType documentType = document.getDocumentType();
                documentTypeDtos.add(documentType.convertToDto());
            }
        }
        return documentTypeDtos;
    }
}
