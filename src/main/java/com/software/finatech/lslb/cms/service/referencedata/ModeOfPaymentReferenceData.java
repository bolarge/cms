package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.ModeOfPayment;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class ModeOfPaymentReferenceData {
    public static final String WEB_PAYMENT_ID = "1";
    public static final String IN_BRANCH_ID = "2";
    public static final String LSLB_OFFLINE_ID = "3";
    public static final String OFFLINE_CONFIRMATION_ID = "4";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName(WEB_PAYMENT_ID, mongoRepositoryReactive, "WEB PAYMENT");
        loadForIdAndName(IN_BRANCH_ID, mongoRepositoryReactive, "IN BRANCH");
        loadForIdAndName(LSLB_OFFLINE_ID, mongoRepositoryReactive, "LSLB OFFLINE");
        loadForIdAndName(OFFLINE_CONFIRMATION_ID, mongoRepositoryReactive, "OFFLINE CONFIRMATION");
    }

    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        ModeOfPayment modeOfPayment = (ModeOfPayment) mongoRepositoryReactive.findById(id, ModeOfPayment.class).block();
        if (modeOfPayment == null) {
            modeOfPayment = new ModeOfPayment();
            modeOfPayment.setId(id);
        }
        modeOfPayment.setName(name);
        mongoRepositoryReactive.saveOrUpdate(modeOfPayment);
    }
}
