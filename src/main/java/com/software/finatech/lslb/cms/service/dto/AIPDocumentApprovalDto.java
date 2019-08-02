package com.software.finatech.lslb.cms.service.dto;

public class AIPDocumentApprovalDto {
    protected String approverId;
    protected String rejectorId;
    protected String approverName;
    protected String rejectorName;
    protected String rejectionReason;
    private Boolean lslbAdminCommented;
    private String lslbAdminName;
    private String lslbAdminComment;
    private String aipFormId;
    protected Boolean readyForApproval;
    protected String statusName;
    protected String statusId;
    protected String institutionId;
    private String gameTypeId;
    private String gameTypeName;
    private String institutionName;
    private String licenseId;
    private Boolean readyForFinalApproval;

    public Boolean getReadyForFinalApproval() {
        return readyForFinalApproval;
    }

    public void setReadyForFinalApproval(Boolean readyForFinalApproval) {
        this.readyForFinalApproval = readyForFinalApproval;
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

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public Boolean getReadyForApproval() {
        return readyForApproval;
    }

    public void setReadyForApproval(Boolean readyForApproval) {
        this.readyForApproval = readyForApproval;
    }
    public String getAipFormId() {
        return aipFormId;
    }

    public void setAipFormId(String aipFormId) {
        this.aipFormId = aipFormId;
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

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getRejectorId() {
        return rejectorId;
    }

    public void setRejectorId(String rejectorId) {
        this.rejectorId = rejectorId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getRejectorName() {
        return rejectorName;
    }

    public void setRejectorName(String rejectorName) {
        this.rejectorName = rejectorName;
    }


}
