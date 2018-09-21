package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.ApplicationFormDto;
import com.software.finatech.lslb.cms.service.model.applicantDetails.ApplicantDetails;
import com.software.finatech.lslb.cms.service.model.applicantMembers.ApplicantMemberDetails;
import com.software.finatech.lslb.cms.service.model.contactDetails.ApplicantContactDetails;
import com.software.finatech.lslb.cms.service.model.criminalityDetails.ApplicantCriminalityDetails;
import com.software.finatech.lslb.cms.service.model.declaration.ApplicantDeclarationDetails;
import com.software.finatech.lslb.cms.service.model.otherInformation.ApplicantOtherInformation;
import com.software.finatech.lslb.cms.service.model.outletInformation.ApplicantOutletInformation;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "ApplicationForms")
public class ApplicationForm extends AbstractFact {

    protected String institutionId;
    protected String gameTypeId;
    protected String applicationFormStatusId;
    protected Set<String> paymentRecordIds;
    protected String applicationFormTypeId;
    protected String formName;
    protected String approverId;
    protected String rejectorId;
    protected ApplicantDetails applicantDetails;
    protected ApplicantMemberDetails applicantMemberDetails;
    protected ApplicantCriminalityDetails applicantCriminalityDetails;
    protected ApplicantDeclarationDetails applicantDeclarationDetails;
    protected ApplicantOutletInformation applicantOutletInformation;
    protected ApplicantOtherInformation applicantOtherInformation;
    protected ApplicantContactDetails applicantContactDetails;
    protected LslbAdminComment lslbAdminComment;
    protected String reasonForRejection;
    protected LocalDate submissionDate;

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getReasonForRejection() {
        return reasonForRejection;
    }

    public void setReasonForRejection(String reasonForRejection) {
        this.reasonForRejection = reasonForRejection;
    }

    public LslbAdminComment getLslbAdminComment() {
        return lslbAdminComment;
    }

    public void setLslbAdminComment(LslbAdminComment lslbAdminComment) {
        this.lslbAdminComment = lslbAdminComment;
    }

    public String getRejectorId() {
        return rejectorId;
    }

    public void setRejectorId(String rejectorId) {
        this.rejectorId = rejectorId;
    }


    public ApplicantDetails getApplicantDetails() {
        return applicantDetails;
    }

    public void setApplicantDetails(ApplicantDetails applicantDetails) {
        this.applicantDetails = applicantDetails;
    }

    public ApplicantMemberDetails getApplicantMemberDetails() {
        return applicantMemberDetails;
    }

    public void setApplicantMemberDetails(ApplicantMemberDetails applicantMemberDetails) {
        this.applicantMemberDetails = applicantMemberDetails;
    }

    public ApplicantCriminalityDetails getApplicantCriminalityDetails() {
        return applicantCriminalityDetails;
    }

    public void setApplicantCriminalityDetails(ApplicantCriminalityDetails applicantCriminalityDetails) {
        this.applicantCriminalityDetails = applicantCriminalityDetails;
    }

    public ApplicantDeclarationDetails getApplicantDeclarationDetails() {
        return applicantDeclarationDetails;
    }

    public void setApplicantDeclarationDetails(ApplicantDeclarationDetails applicantDeclarationDetails) {
        this.applicantDeclarationDetails = applicantDeclarationDetails;
    }

    public ApplicantOutletInformation getApplicantOutletInformation() {
        return applicantOutletInformation;
    }

    public void setApplicantOutletInformation(ApplicantOutletInformation applicantOutletInformation) {
        this.applicantOutletInformation = applicantOutletInformation;
    }

    public ApplicantOtherInformation getApplicantOtherInformation() {
        return applicantOtherInformation;
    }

    public void setApplicantOtherInformation(ApplicantOtherInformation applicantOtherInformation) {
        this.applicantOtherInformation = applicantOtherInformation;
    }

    public ApplicantContactDetails getApplicantContactDetails() {
        return applicantContactDetails;
    }

    public void setApplicantContactDetails(ApplicantContactDetails applicantContactDetails) {
        this.applicantContactDetails = applicantContactDetails;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getApplicationFormStatusId() {
        return applicationFormStatusId;
    }

    public void setApplicationFormStatusId(String applicationFormStatusId) {
        this.applicationFormStatusId = applicationFormStatusId;
    }

    public Set<String> getPaymentRecordIds() {
        return paymentRecordIds;
    }

    public void setPaymentRecordIds(Set<String> paymentRecordIds) {
        this.paymentRecordIds = paymentRecordIds;
    }

    public String getApplicationFormTypeId() {
        return applicationFormTypeId;
    }

    public void setApplicationFormTypeId(String applicationFormTypeId) {
        this.applicationFormTypeId = applicationFormTypeId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public AuthInfo getAuthInfo(String authInfoId) {
        if (StringUtils.isEmpty(authInfoId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(authInfoId, AuthInfo.class).block();
    }

    private GameType getGameType() {
        if (StringUtils.isEmpty(this.gameTypeId)) {
            return null;
        }
        Map gameTypeMap = Mapstore.STORE.get("GameType");
        GameType gameType = null;
        if (gameTypeMap != null) {
            gameType = (GameType) gameTypeMap.get(gameTypeId);
        }
        if (gameType == null) {
            gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
            if (gameType != null && gameTypeMap != null) {
                gameTypeMap.put(gameTypeId, gameType);
            }
        }
        return gameType;
    }

    public String getGameTypeName() {
        GameType gameType = getGameType();
        if (gameType == null) {
            return null;
        } else {
            return gameType.getName();
        }
    }

    public String getGameTypeDesciption() {
        GameType gameType = getGameType();
        if (gameType == null) {
            return null;
        } else {
            return gameType.getDescription();
        }
    }

    private ApplicationFormStatus getStatus() {
        if (StringUtils.isEmpty(this.applicationFormStatusId)) {
            return null;
        }
        Map applicationFormStatusMap = Mapstore.STORE.get("ApplicationFormStatus");
        ApplicationFormStatus applicationFormStatus = null;
        if (applicationFormStatusMap != null) {
            applicationFormStatus = (ApplicationFormStatus) applicationFormStatusMap.get(applicationFormStatusId);
        }
        if (applicationFormStatus == null) {
            applicationFormStatus = (ApplicationFormStatus) mongoRepositoryReactive.findById(applicationFormStatusId, ApplicationFormStatus.class).block();
            if (applicationFormStatus != null && applicationFormStatusMap != null) {
                applicationFormStatusMap.put(applicationFormStatusId, applicationFormStatus);
            }
        }
        return applicationFormStatus;
    }

    public Institution getInstitution() {
        if (StringUtils.isEmpty(institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public String getInstitutionName() {
        Institution institution = getInstitution();
        if (institution != null) {
            return institution.getInstitutionName();
        }
        return "";
    }

    public ApplicationFormDto convertToDto() {
        ApplicationFormDto applicationFormDto = new ApplicationFormDto();
        GameType gameType = getGameType();
        if (gameType != null) {
            applicationFormDto.setGameTypeDescription(gameType.getDescription());
            applicationFormDto.setGameTypeName(gameType.getName());
            applicationFormDto.setGameTypeId(gameTypeId);
        }
        ApplicationFormStatus applicationFormStatus = getStatus();
        if (applicationFormStatus != null) {
            applicationFormDto.setStatusName(applicationFormStatus.getName());
            applicationFormDto.setStatusId(getApplicationFormStatusId());
        }

        Institution institution = getInstitution();
        if (institution != null) {
            applicationFormDto.setInstitutionName(institution.getInstitutionName());
            applicationFormDto.setInstitutionId(institutionId);
        }
        AuthInfo approver = getAuthInfo(approverId);
        if (approver != null) {
            applicationFormDto.setApproverId(approverId);
            applicationFormDto.setApproverName(approver.getFullName());
        }
        AuthInfo rejector = getAuthInfo(rejectorId);
        if (rejector != null) {
            applicationFormDto.setRejectorId(rejectorId);
            applicationFormDto.setRejectorName(rejector.getFullName());
        }
        applicationFormDto.setRejectionReason(getReasonForRejection());
        applicationFormDto.setId(getId());
        applicationFormDto.setFormName(getFormName());

        LslbAdminComment lslbAdminComment = getLslbAdminComment();
        if (lslbAdminComment != null) {
            applicationFormDto.setLslbAdminComment(lslbAdminComment.getComment());
            applicationFormDto.setLslbAdminCommented(true);
            AuthInfo admin = getAuthInfo(lslbAdminComment.getUserId());
            if (admin != null) {
                applicationFormDto.setLslbAdminName(admin.getFullName());
            }
        }
        applicationFormDto.setFilledApplicantContactDetails(getApplicantContactDetails() != null);
        applicationFormDto.setFilledApplicantCriminalityDetails(getApplicantCriminalityDetails() != null);
        applicationFormDto.setFilledApplicantDeclarationDetails(getApplicantDeclarationDetails() != null);
        applicationFormDto.setFilledApplicantDetails(getApplicantDetails() != null);
        applicationFormDto.setFilledApplicantOtherInformation(getApplicantOtherInformation() != null);
        applicationFormDto.setFilledApplicantOutletInformation(getApplicantOutletInformation() != null);
        applicationFormDto.setFilledApplicantDetails(getApplicantDetails() != null);
        applicationFormDto.setStatusId(applicationFormStatusId);
        return applicationFormDto;
    }

    @Override
    public String getFactName() {
        return "ApplicationForm";
    }
}
