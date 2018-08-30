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
        return (AuthInfo) mongoRepositoryReactive.findById(authInfoId, AuthInfo.class).block();
    }

    private GameType getGameType() {
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

    private ApplicationFormStatus getStatus() {
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

    private ApplicationFormType getApplicationFormType() {
        Map applicationFormTypeMap = Mapstore.STORE.get("ApplicationFormType");
        ApplicationFormType applicationFormType = null;
        if (applicationFormTypeMap != null) {
            applicationFormType = (ApplicationFormType) applicationFormTypeMap.get(applicationFormTypeId);
        }
        if (applicationFormType == null) {
            applicationFormType = (ApplicationFormType) mongoRepositoryReactive.findById(applicationFormTypeId, ApplicationFormType.class).block();
            if (applicationFormType != null && applicationFormTypeMap != null) {
                applicationFormTypeMap.put(applicationFormTypeId, applicationFormType);
            }
        }
        return applicationFormType;
    }

    public Institution getInstitution() {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public ApplicationFormDto convertToDto() {
        ApplicationFormDto applicationFormDto = new ApplicationFormDto();
        GameType gameType = getGameType();
        if (gameType != null) {
            applicationFormDto.setGameType(gameType.convertToDto());
        }
        ApplicationFormStatus applicationFormStatus = getStatus();
        if (applicationFormStatus != null) {
            applicationFormDto.setStatus(applicationFormStatus.convertToDto());
        }

        ApplicationFormType applicationFormType = getApplicationFormType();
        if (applicationFormType != null) {
            applicationFormDto.setApplicationFormType(applicationFormType.convertToDto());
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
        applicationFormDto.setId(getId());
        applicationFormDto.setFormName(getFormName());
        return applicationFormDto;
    }

    @Override
    public String getFactName() {
        return "ApplicationForm";
    }
}
