package com.software.finatech.lslb.cms.service.dto;

public class FeeApprovalRequestDto {
    private String initiatorName;
    private String dateCreated;
    private FeeDto pendingFee;
    private String newEndDate;
    private String initiatorId;
    private String rejectorName;
    private String rejectorId;
    private String approvalRequestStatus;
    private String approvalRequestStatusId;
    private String approvalRequestType;
    private String approvalRequestTypeId;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public FeeDto getPendingFee() {
        return pendingFee;
    }

    public void setPendingFee(FeeDto pendingFee) {
        this.pendingFee = pendingFee;
    }

    public String getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(String newEndDate) {
        this.newEndDate = newEndDate;
    }

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getRejectorName() {
        return rejectorName;
    }

    public void setRejectorName(String rejectorName) {
        this.rejectorName = rejectorName;
    }

    public String getRejectorId() {
        return rejectorId;
    }

    public void setRejectorId(String rejectorId) {
        this.rejectorId = rejectorId;
    }

    public String getApprovalRequestStatus() {
        return approvalRequestStatus;
    }

    public void setApprovalRequestStatus(String approvalRequestStatus) {
        this.approvalRequestStatus = approvalRequestStatus;
    }

    public String getApprovalRequestStatusId() {
        return approvalRequestStatusId;
    }

    public void setApprovalRequestStatusId(String approvalRequestStatusId) {
        this.approvalRequestStatusId = approvalRequestStatusId;
    }

    public String getApprovalRequestType() {
        return approvalRequestType;
    }

    public void setApprovalRequestType(String approvalRequestType) {
        this.approvalRequestType = approvalRequestType;
    }

    public String getApprovalRequestTypeId() {
        return approvalRequestTypeId;
    }

    public void setApprovalRequestTypeId(String approvalRequestTypeId) {
        this.approvalRequestTypeId = approvalRequestTypeId;
    }
}
