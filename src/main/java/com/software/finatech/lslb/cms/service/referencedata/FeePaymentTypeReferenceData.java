package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class FeePaymentTypeReferenceData {
    public static final String APPLICATION_FEE_TYPE_ID = "01";
    public static final String LICENSE_FEE_TYPE_ID= "02";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        FeePaymentType feePaymentType1 = (FeePaymentType) mongoRepositoryReactive.findById(APPLICATION_FEE_TYPE_ID, FeePaymentType.class).block();
        if (feePaymentType1 == null) {
            feePaymentType1 = new FeePaymentType();
            feePaymentType1.setId(APPLICATION_FEE_TYPE_ID);
        }
        feePaymentType1.setName("Application Fees");

        FeePaymentType feePaymentType2 = (FeePaymentType) mongoRepositoryReactive.findById(LICENSE_FEE_TYPE_ID, FeePaymentType.class).block();
        if (feePaymentType2 == null) {
            feePaymentType2 = new FeePaymentType();
            feePaymentType2.setId(LICENSE_FEE_TYPE_ID);
        }
        feePaymentType2.setName("Licensing Fees");

        mongoRepositoryReactive.saveOrUpdate(feePaymentType1);
        mongoRepositoryReactive.saveOrUpdate(feePaymentType2);
    }
}
