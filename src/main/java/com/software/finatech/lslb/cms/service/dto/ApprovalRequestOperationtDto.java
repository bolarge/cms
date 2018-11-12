package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class ApprovalRequestOperationtDto {
    private String approvalRequestId;
    private String reason;
    private Set<String> approvalRequestIds = new HashSet<>();

    public Set<String> getApprovalRequestIds() {
        return approvalRequestIds;
    }

    public void setApprovalRequestIds(Set<String> approvalRequestIds) {
        this.approvalRequestIds = approvalRequestIds;
    }

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
