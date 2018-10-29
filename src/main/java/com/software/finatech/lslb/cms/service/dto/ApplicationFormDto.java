package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.CommentDto;

import java.util.ArrayList;
import java.util.List;

public class ApplicationFormDto {

    private String gameTypeName;
    private String gameTypeDescription;
    private String gameTypeId;
    private String statusName;
    private String statusId;
    private String applicationFormTypeName;
    private String formName;
    private String institutionId;
    private String institutionName;
    private String approverId;
    private String approverName;
    private String id;
    private String rejectorId;
    private String rejectorName;
    private String rejectionReason;
    private Boolean filledApplicantDetails;
    private Boolean filledApplicantCriminalityDetails;
    private Boolean filledApplicantDeclarationDetails;
    private Boolean filledApplicantOutletInformation;
    private Boolean filledApplicantOtherInformation;
    private Boolean filledApplicantContactDetails;
    private Boolean lslbAdminCommented;
    private String lslbAdminName;
    private String lslbAdminComment;
    private String applicationFormId;
    private Boolean readyForApproval;
    private List<CommentDto> comments = new ArrayList<>();


    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public Boolean getReadyForApproval() {
        return readyForApproval;
    }

    public void setReadyForApproval(Boolean readyForApproval) {
        this.readyForApproval = readyForApproval;
    }

    public String getApplicationFormId() {
        return applicationFormId;
    }

    public void setApplicationFormId(String applicationFormId) {
        this.applicationFormId = applicationFormId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getGameTypeDescription() {
        return gameTypeDescription;
    }

    public void setGameTypeDescription(String gameTypeDescription) {
        this.gameTypeDescription = gameTypeDescription;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getApplicationFormTypeName() {
        return applicationFormTypeName;
    }

    public void setApplicationFormTypeName(String applicationFormTypeName) {
        this.applicationFormTypeName = applicationFormTypeName;
    }

    public Boolean getFilledApplicantDetails() {
        return filledApplicantDetails;
    }

    public void setFilledApplicantDetails(Boolean filledApplicantDetails) {
        this.filledApplicantDetails = filledApplicantDetails;
    }

    public Boolean getFilledApplicantCriminalityDetails() {
        return filledApplicantCriminalityDetails;
    }

    public void setFilledApplicantCriminalityDetails(Boolean filledApplicantCriminalityDetails) {
        this.filledApplicantCriminalityDetails = filledApplicantCriminalityDetails;
    }

    public Boolean getFilledApplicantDeclarationDetails() {
        return filledApplicantDeclarationDetails;
    }

    public void setFilledApplicantDeclarationDetails(Boolean filledApplicantDeclarationDetails) {
        this.filledApplicantDeclarationDetails = filledApplicantDeclarationDetails;
    }

    public Boolean getFilledApplicantOutletInformation() {
        return filledApplicantOutletInformation;
    }

    public void setFilledApplicantOutletInformation(Boolean filledApplicantOutletInformation) {
        this.filledApplicantOutletInformation = filledApplicantOutletInformation;
    }

    public Boolean getFilledApplicantOtherInformation() {
        return filledApplicantOtherInformation;
    }

    public void setFilledApplicantOtherInformation(Boolean filledApplicantOtherInformation) {
        this.filledApplicantOtherInformation = filledApplicantOtherInformation;
    }

    public Boolean getFilledApplicantContactDetails() {
        return filledApplicantContactDetails;
    }

    public void setFilledApplicantContactDetails(Boolean filledApplicantContactDetails) {
        this.filledApplicantContactDetails = filledApplicantContactDetails;
    }

    public Boolean getLslbAdminCommented() {
        return lslbAdminCommented;
    }

    public void setLslbAdminCommented(Boolean lslbAdminCommented) {
        this.lslbAdminCommented = lslbAdminCommented;
    }

    public String getLslbAdminName() {
        return lslbAdminName;
    }

    public void setLslbAdminName(String lslbAdminName) {
        this.lslbAdminName = lslbAdminName;
    }

    public String getLslbAdminComment() {
        return lslbAdminComment;
    }

    public void setLslbAdminComment(String lslbAdminComment) {
        this.lslbAdminComment = lslbAdminComment;
    }

    public String getRejectorId() {
        return rejectorId;
    }

    public String getRejectorName() {
        return rejectorName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public void setRejectorId(String rejectorId) {
    }

    public void setRejectorName(String fullName) {
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
