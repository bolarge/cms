package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.ApprovalRequestStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class ApprovalRequestStatusReferenceData {
    public static final String APPROVED_ID = "1";
    public static final String PENDING_ID = "2";
    public static final String REJECTED_ID = "3";
    public static final String PENDING_OPERATOR_APPROVAL_ID = "4";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForId(APPROVED_ID, mongoRepositoryReactive, "APPROVED");
        loadForId(PENDING_ID, mongoRepositoryReactive, "PENDING APPROVAL");
        loadForId(REJECTED_ID, mongoRepositoryReactive, "REJECTED");
        loadForId(PENDING_OPERATOR_APPROVAL_ID, mongoRepositoryReactive, "PENDING OPERATOR APPROVAL");
    }

    private static void loadForId(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        ApprovalRequestStatus status = (ApprovalRequestStatus) mongoRepositoryReactive.findById(id, ApprovalRequestStatus.class).block();
        if (status == null) {
            status = new ApprovalRequestStatus();
            status.setId(id);
        }
        status.setName(name);
        mongoRepositoryReactive.saveOrUpdate(status);
    }
}
