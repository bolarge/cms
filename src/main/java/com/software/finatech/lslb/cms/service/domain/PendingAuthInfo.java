package com.software.finatech.lslb.cms.service.domain;


import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "PendingAuthInfo")
public class PendingAuthInfo extends AuthInfo {

    public String userApprovalRequestStatusId;

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
