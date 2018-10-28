package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.ApplicationForm;
import com.software.finatech.lslb.cms.service.domain.Document;
import com.software.finatech.lslb.cms.service.dto.FormCreateCommentDto;
import com.software.finatech.lslb.cms.service.dto.ApplicationFormCreateDto;
import com.software.finatech.lslb.cms.service.dto.ApplicationFormRejectDto;
import com.software.finatech.lslb.cms.service.model.applicantDetails.ApplicantDetails;
import com.software.finatech.lslb.cms.service.model.applicantMembers.ApplicantMemberDetails;
import com.software.finatech.lslb.cms.service.model.contactDetails.ApplicantContactDetails;
import com.software.finatech.lslb.cms.service.model.criminalityDetails.ApplicantCriminalityDetails;
import com.software.finatech.lslb.cms.service.model.declaration.ApplicantDeclarationDetails;
import com.software.finatech.lslb.cms.service.model.otherInformation.ApplicantOtherInformation;
import com.software.finatech.lslb.cms.service.model.outletInformation.ApplicantOutletInformation;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ApplicationFormService {

    Mono<ResponseEntity> createApplicationForm(ApplicationFormCreateDto applicationFormCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> findAllApplicationForm(int page,
                                                int pageSize,
                                                String sortDirection,
                                                String sortProperty,
                                                String institutionId,
                                                String applicationFormStatusId,
                                                String approverId,
                                                String gameTypeId,
                                                HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllApplicationFormStatus();
    
    Mono<ResponseEntity> getApplicantDetails(String applicationFormId);

    Mono<ResponseEntity> saveApplicantDetails(String applicationFormId, ApplicantDetails applicantDetails, HttpServletRequest request);

    Mono<ResponseEntity> getApplicantMembersDetails(String applicationFormId);

    Mono<ResponseEntity> saveApplicantMembersDetails(String applicationFormId, ApplicantMemberDetails applicantMemberDetails, HttpServletRequest request);

    Mono<ResponseEntity> getApplicantContactDetails(String applicationFormId);

    Mono<ResponseEntity> saveApplicantContactDetails(String applicationFormId, ApplicantContactDetails applicantContactDetails, HttpServletRequest request);

    Mono<ResponseEntity> getApplicantCriminalityDetails(String applicationFormId);

    Mono<ResponseEntity> saveApplicantCriminalityDetails(String applicationFormId, ApplicantCriminalityDetails applicantCriminalityDetails, HttpServletRequest request);

    Mono<ResponseEntity> getApplicantDeclarationDetails(String applicationFormId);

    Mono<ResponseEntity> saveApplicantDeclarationDetails(String applicationFormId, ApplicantDeclarationDetails applicantDeclarationDetails, HttpServletRequest request);

    Mono<ResponseEntity> getApplicantOtherInformation(String applicationFormId);

    Mono<ResponseEntity> saveApplicantOtherInformation(String applicationFormId, ApplicantOtherInformation applicantOtherInformation, HttpServletRequest request);

    Mono<ResponseEntity> getApplicantOutletInformation(String applicationFormId);

    Mono<ResponseEntity> saveApplicantOutletInformation(String applicationFormId, ApplicantOutletInformation applicantOutletInformation, HttpServletRequest request);

    Mono<ResponseEntity> approveApplicationForm(String applicationFormId, String approverId, HttpServletRequest request);

    Mono<ResponseEntity> rejectApplicationForm(String applicationFormId, ApplicationFormRejectDto applicationFormRejectDto, HttpServletRequest request);

    Mono<ResponseEntity> completeApplicationForm(String applicationFormId, boolean isResubmit, HttpServletRequest request);

    Mono<ResponseEntity> getPaymentRecordsForApplicationForm(String applicationFormId);

    boolean institutionHasCompletedApplicationForGameType(String institutionId, String gameTypeId);

    ApplicationForm findApplicationFormById(String applicationFormId);

    Mono<ResponseEntity> addCommentsToFormFromLslbAdmin(String applicationFormId, FormCreateCommentDto formCreateCommentDto, HttpServletRequest request);

    void approveApplicationFormDocument(Document document);

    void rejectApplicationFormDocument(Document document);

    void doDocumentReuploadNotification(Document document);
}
