package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class FeePaymentTypeReferenceData {
    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        FeePaymentType feePaymentType1 = (FeePaymentType) mongoRepositoryReactive.findById("01", FeePaymentType.class).block();
        if (feePaymentType1 == null) {
            feePaymentType1 = new FeePaymentType();
            feePaymentType1.setId("01");
        }
        feePaymentType1.setName("Application Fees");

        FeePaymentType feePaymentType2 = (FeePaymentType) mongoRepositoryReactive.findById("02", FeePaymentType.class).block();
        if (feePaymentType2 == null) {
            feePaymentType2 = new FeePaymentType();
            feePaymentType2.setId("02");
        }
        feePaymentType2.setName("Licensing Fees");

        mongoRepositoryReactive.saveOrUpdate(feePaymentType1);
        mongoRepositoryReactive.saveOrUpdate(feePaymentType2);
    }
}
