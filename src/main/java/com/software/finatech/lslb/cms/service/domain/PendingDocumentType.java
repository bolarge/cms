package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PendingDocumentTypes")
public class PendingDocumentType extends DocumentType {
    private String approvalRequestStatusIds = ApprovalRequestStatusReferenceData.PENDING_ID;

    public String getApprovalRequestStatusIds() {
        return approvalRequestStatusIds;
    }

    public void setApprovalRequestStatusIds(String approvalRequestStatusIds) {
        this.approvalRequestStatusIds = approvalRequestStatusIds;
    }
}
