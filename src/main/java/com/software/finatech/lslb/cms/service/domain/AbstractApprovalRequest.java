package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;

import java.util.Map;

public abstract class AbstractApprovalRequest extends AbstractFact {
    protected String approverId;
    protected String rejectorId;
    protected String approvalRequestStatusId = ApprovalRequestStatusReferenceData.PENDING_ID;
    protected String initiatorId;
    protected String initiatorAuthRoleId;
    protected LocalDateTime dateCreated = LocalDateTime.now();
    protected String rejectionReason;

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getInitiatorAuthRoleId() {
        return initiatorAuthRoleId;
    }

    public void setInitiatorAuthRoleId(String initiatorAuthRoleId) {
        this.initiatorAuthRoleId = initiatorAuthRoleId;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
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

    public String getApprovalRequestStatusId() {
        return approvalRequestStatusId;
    }

    public void setApprovalRequestStatusId(String approvalRequestStatusId) {
        this.approvalRequestStatusId = approvalRequestStatusId;
    }

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

    public ApprovalRequestStatus getApprovalRequestStatus() {
        if (StringUtils.isEmpty(this.approvalRequestStatusId)) {
            return null;
        }
        Map approvalRequestStatusMap = Mapstore.STORE.get("ApprovalRequestStatus");
        ApprovalRequestStatus approvalRequestStatus = null;
        if (approvalRequestStatusMap != null) {
            approvalRequestStatus = (ApprovalRequestStatus) approvalRequestStatusMap.get(this.approvalRequestStatusId);
        }
        if (approvalRequestStatus == null) {
            approvalRequestStatus = (ApprovalRequestStatus) mongoRepositoryReactive.findById(this.approvalRequestStatusId, ApprovalRequestStatus.class).block();
            if (approvalRequestStatus != null && approvalRequestStatusMap != null) {
                approvalRequestStatusMap.put(this.approvalRequestStatusId, approvalRequestStatus);
            }
        }
        return approvalRequestStatus;
    }

    public AuthInfo getAuthInfo(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
    }

    public AuthInfo getApprover() {
        return getAuthInfo(this.approverId);
    }

    public AuthInfo getRejector() {
        return getAuthInfo(this.rejectorId);
    }

    public AuthInfo getInitiator() {
        return getAuthInfo(this.initiatorId);
    }
}
