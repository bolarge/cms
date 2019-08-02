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
    protected String institutionId;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

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

    public String getDateCreatedString() {
        LocalDateTime dateTime = getDateCreated();
        if (dateTime != null) {
            return dateTime.toString("dd-MM-yyyy hh:mm a");
        }
        return null;
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

    public boolean isApproved() {
        return StringUtils.equals(ApprovalRequestStatusReferenceData.APPROVED_ID, this.approvalRequestStatusId);
    }

    public boolean isApprovedRequest() {
        return StringUtils.equals(ApprovalRequestStatusReferenceData.APPROVED_ID, this.approvalRequestStatusId);
    }

    public boolean isRejectedRequest() {
        return StringUtils.equals(ApprovalRequestStatusReferenceData.REJECTED_ID, this.approvalRequestStatusId);
    }


    public void setAsApproved() {
        this.approvalRequestStatusId = ApprovalRequestStatusReferenceData.APPROVED_ID;
    }

    public void setAsApprovedByUser(String userId) {
        this.setAsApproved();
        setApproverId(userId);
    }

    public void setAsRejectedByUserWithReason(String userId, String reason) {
        this.setAsRejected();
        this.setRejectionReason(reason);
        setRejectorId(userId);
    }

    public void setAsRejected() {
        this.approvalRequestStatusId = ApprovalRequestStatusReferenceData.REJECTED_ID;
    }


    public boolean canBeApprovedByUser(String userId) {
        return !StringUtils.equals(this.initiatorId, userId);
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

    public String getAuthInfoName(String authInfoId) {
        AuthInfo authInfo = getAuthInfo(authInfoId);
        if (authInfo != null) {
            return authInfo.getFullName();
        }
        return null;
    }
}
