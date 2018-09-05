package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.ModeOfPayment;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class ModeOfPaymentReferenceData {
    public static final String WEB_PAYMENT_ID = "1";
    public static final String IN_BRANCH_ID = "2";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        ModeOfPayment modeOfPayment1 = (ModeOfPayment) mongoRepositoryReactive.findById(WEB_PAYMENT_ID, ModeOfPayment.class).block();
        if (modeOfPayment1 == null) {
            modeOfPayment1 = new ModeOfPayment();
            modeOfPayment1.setId(WEB_PAYMENT_ID);
        }
        modeOfPayment1.setName("WEB PAYMENT");

        ModeOfPayment modeOfPayment2 = (ModeOfPayment) mongoRepositoryReactive.findById(IN_BRANCH_ID, ModeOfPayment.class).block();
        if (modeOfPayment2 == null) {
            modeOfPayment2 = new ModeOfPayment();
            modeOfPayment2.setId(IN_BRANCH_ID);
        }
        modeOfPayment2.setName("IN BRANCH");

        mongoRepositoryReactive.saveOrUpdate(modeOfPayment1);
        mongoRepositoryReactive.saveOrUpdate(modeOfPayment2);
    }
}
