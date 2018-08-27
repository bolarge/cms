package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.ApplicationForm;
import com.software.finatech.lslb.cms.service.dto.ApplicationFormCreateDto;
import com.software.finatech.lslb.cms.service.model.applicantDetails.ApplicantDetails;
import com.software.finatech.lslb.cms.service.model.applicantMembers.ApplicantMemberDetails;
import com.software.finatech.lslb.cms.service.model.contactDetails.ApplicantContactDetails;
import com.software.finatech.lslb.cms.service.model.criminalityDetails.ApplicantCriminalityDetails;
import com.software.finatech.lslb.cms.service.model.declaration.ApplicantDeclarationDetails;
import com.software.finatech.lslb.cms.service.model.otherInformation.ApplicantOtherInformation;
import com.software.finatech.lslb.cms.service.model.outletInformation.ApplicantOutletInformation;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface ApplicationFormService {

    Mono<ResponseEntity> createApplicationForm(ApplicationFormCreateDto applicationFormCreateDto);

    Mono<ResponseEntity> findAllApplicationForm(int page,
                                                int pageSize,
                                                String sortDirection,
                                                String sortProperty,
                                                String institutionId,
                                                String applicationFormTypeId,
                                                String applicationFormStatusId,
                                                String approverId,
                                                HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllApplicationFormTypes();
    Mono<ResponseEntity> getAllApplicationFormStatus();
    Mono<ResponseEntity> getAllApprovers();

    Mono<ResponseEntity> getApplicantDetails(String applicationFormId);
    Mono<ResponseEntity> saveApplicantDetails(String applicationFormId, ApplicantDetails applicantDetails);

    Mono<ResponseEntity> getApplicantMembersDetails(String applicationFormId);
    Mono<ResponseEntity> saveApplicantMembersDetails(String applicationFormId, ApplicantMemberDetails applicantMemberDetails);

    Mono<ResponseEntity> getApplicantContactDetails(String applicationFormId);
    Mono<ResponseEntity> saveApplicantContactDetails(String applicationFormId, ApplicantContactDetails applicantContactDetails);

    Mono<ResponseEntity> getApplicantCriminalityDetails(String applicationFormId);
    Mono<ResponseEntity> saveApplicantCriminalityDetails(String applicationFormId, ApplicantCriminalityDetails applicantCriminalityDetails);

    Mono<ResponseEntity> getApplicantDeclarationDetails(String applicationFormId);
    Mono<ResponseEntity> saveApplicantDeclarationDetails(String applicationFormId, ApplicantDeclarationDetails applicantDeclarationDetails);

    Mono<ResponseEntity> getApplicantOtherInformation(String applicationFormId);
    Mono<ResponseEntity> saveApplicantOtherInformation(String applicationFormId, ApplicantOtherInformation applicantOtherInformation);

    Mono<ResponseEntity> getApplicantOutletInformation(String applicationFormId);
    Mono<ResponseEntity> saveApplicantOutletInformation(String applicationFormId, ApplicantOutletInformation applicantOutletInformation);

    Mono<ResponseEntity> approveApplicationForm(String applicationFormId, String  approverId);
    Mono<ResponseEntity> rejectApplicationForm(String applicationFormId, String rejectorId);
    Mono<ResponseEntity> completeApplicationForm(String applicationFormId);
    Mono<ResponseEntity> getPaymentRecordsForApplicationForm(String applicationFormId);

}
