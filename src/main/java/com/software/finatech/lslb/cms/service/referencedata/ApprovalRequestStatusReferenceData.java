package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.ApprovalRequestStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class ApprovalRequestStatusReferenceData {
    public static final String APPROVED_ID = "1";
    public static final String PENDING_ID = "2";
    public static final String REJECTED_ID = "3";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        ApprovalRequestStatus approvalRequestStatus1 = (ApprovalRequestStatus) mongoRepositoryReactive.findById(APPROVED_ID, ApprovalRequestStatus.class).block();
        if (approvalRequestStatus1 == null) {
            approvalRequestStatus1 = new ApprovalRequestStatus();
            approvalRequestStatus1.setId(APPROVED_ID);
        }
        approvalRequestStatus1.setName("APPROVED");

        ApprovalRequestStatus approvalRequestStatus2 = (ApprovalRequestStatus) mongoRepositoryReactive.findById(PENDING_ID, ApprovalRequestStatus.class).block();
        if (approvalRequestStatus2 == null) {
            approvalRequestStatus2 = new ApprovalRequestStatus();
            approvalRequestStatus2.setId(PENDING_ID);
        }
        approvalRequestStatus2.setName("PENDING");

        ApprovalRequestStatus approvalRequestStatus3 = (ApprovalRequestStatus) mongoRepositoryReactive.findById(REJECTED_ID, ApprovalRequestStatus.class).block();
        if (approvalRequestStatus3 == null) {
            approvalRequestStatus3 = new ApprovalRequestStatus();
            approvalRequestStatus3.setId(REJECTED_ID);
        }
        approvalRequestStatus3.setName("REJECTED");

        mongoRepositoryReactive.saveOrUpdate(approvalRequestStatus1);
        mongoRepositoryReactive.saveOrUpdate(approvalRequestStatus2);
        mongoRepositoryReactive.saveOrUpdate(approvalRequestStatus3);
    }
}
