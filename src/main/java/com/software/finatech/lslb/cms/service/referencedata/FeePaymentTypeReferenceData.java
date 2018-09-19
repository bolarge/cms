package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class FeePaymentTypeReferenceData {
    public static final String APPLICATION_FEE_TYPE_ID = "01";
    public static final String LICENSE_FEE_TYPE_ID= "02";
    public static final String LICENSE_RENEWAL_FEE_TYPE_ID= "03";

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

        FeePaymentType feePaymentType3 = (FeePaymentType) mongoRepositoryReactive.findById(LICENSE_RENEWAL_FEE_TYPE_ID ,FeePaymentType.class).block();
        if (feePaymentType3 == null) {
            feePaymentType3 = new FeePaymentType();
            feePaymentType3.setId(LICENSE_RENEWAL_FEE_TYPE_ID);
        }
        feePaymentType3.setName("License Renewal Fees");

        mongoRepositoryReactive.saveOrUpdate(feePaymentType1);
        mongoRepositoryReactive.saveOrUpdate(feePaymentType2);
        mongoRepositoryReactive.saveOrUpdate(feePaymentType3);
    }
}
