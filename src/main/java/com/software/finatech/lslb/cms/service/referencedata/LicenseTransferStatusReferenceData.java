package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.LicenseTransferStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class LicenseTransferStatusReferenceData {

    public static final String PENDING_INITIAL_APPROVAL_ID = "1";
    public static final String PENDING_ADD_INSTITUTION_APPROVAL_ID = "5";
    public static final String PENDING_NEW_INSTITUTION_ADDITION_ID = "6";
    public static final String PENDING_FINAL_APPROVAL_ID = "2";
    public static final String APPROVED_ID = "3";
    public static final String REJECTED_ID = "4";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName(PENDING_INITIAL_APPROVAL_ID, mongoRepositoryReactive, "PENDING EXIT APPROVAL");
        loadForIdAndName(PENDING_FINAL_APPROVAL_ID, mongoRepositoryReactive, "PENDING FINAL APPROVAL");
        loadForIdAndName(APPROVED_ID, mongoRepositoryReactive, "APPROVED");
        loadForIdAndName(REJECTED_ID, mongoRepositoryReactive, "REJECTED");
        loadForIdAndName(PENDING_ADD_INSTITUTION_APPROVAL_ID, mongoRepositoryReactive, "PENDING CLAIM APPROVAL");
        loadForIdAndName(PENDING_NEW_INSTITUTION_ADDITION_ID, mongoRepositoryReactive, "PENDING TRANSFEREE CLAIM");
    }


    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        LicenseTransferStatus status = (LicenseTransferStatus) mongoRepositoryReactive.findById(id, LicenseTransferStatus.class).block();
        if (status == null) {
            status = new LicenseTransferStatus();
            status.setId(id);
        }
        status.setName(name);
        mongoRepositoryReactive.saveOrUpdate(status);
    }
}
