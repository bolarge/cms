package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class AgentApprovalRequestOperationtDto {
    @NotEmpty(message = "please provide user id")
    private String userId;
    @NotEmpty(message = "please provide approval request id")
    private String agentApprovalRequestId;
    private String reason;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAgentApprovalRequestId() {
        return agentApprovalRequestId;
    }

    public void setAgentApprovalRequestId(String agentApprovalRequestId) {
        this.agentApprovalRequestId = agentApprovalRequestId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
