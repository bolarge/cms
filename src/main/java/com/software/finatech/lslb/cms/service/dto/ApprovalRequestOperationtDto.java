package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class ApprovalRequestOperationtDto {
    @NotEmpty(message = "please provide approval request id")
    private String approvalRequestId;
    private String reason;

    public String getApprovalRequestId() {
        return approvalRequestId;
    }

    public void setApprovalRequestId(String approvalRequestId) {
        this.approvalRequestId = approvalRequestId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
