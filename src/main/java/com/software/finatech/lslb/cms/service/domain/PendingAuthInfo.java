package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "PendingAuthInfo")
public class PendingAuthInfo extends AuthInfo {

    private String userApprovalRequestStatusId = ApprovalRequestStatusReferenceData.PENDING_ID;

    public String getUserApprovalRequestStatusId() {
        return userApprovalRequestStatusId;
    }

    public void setUserApprovalRequestStatusId(String userApprovalRequestStatusId) {
        this.userApprovalRequestStatusId = userApprovalRequestStatusId;
    }

    @Override
    public String getFactName() {
        return "PendingAuthInfo";
    }
}
