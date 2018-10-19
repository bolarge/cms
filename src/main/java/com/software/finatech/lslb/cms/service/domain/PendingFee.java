package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "PendingFees")
public class PendingFee extends Fee {
    private String approvalRequestStatusId = ApprovalRequestStatusReferenceData.PENDING_ID;

    public String getApprovalRequestStatusId() {
        return approvalRequestStatusId;
    }

    public void setApprovalRequestStatusId(String approvalRequestStatusId) {
        this.approvalRequestStatusId = approvalRequestStatusId;
    }

    @Override
    public String getFactName() {
        return "PendingFee";
    }
}
