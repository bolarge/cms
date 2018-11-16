package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class FeePaymentTypeReferenceData {
    public static final String APPLICATION_FEE_TYPE_ID = "01";
    public static final String LICENSE_FEE_TYPE_ID = "02";
    public static final String LICENSE_RENEWAL_FEE_TYPE_ID = "03";
    public static final String TAX_FEE_TYPE_ID = "04";
    public static final String TAX_RENEWAL_FEE_TYPE_ID = "05";
    public static final String LICENSE_TRANSFER_FEE_TYPE_ID = "06";

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

        FeePaymentType feePaymentType3 = (FeePaymentType) mongoRepositoryReactive.findById(LICENSE_RENEWAL_FEE_TYPE_ID, FeePaymentType.class).block();
        if (feePaymentType3 == null) {
            feePaymentType3 = new FeePaymentType();
            feePaymentType3.setId(LICENSE_RENEWAL_FEE_TYPE_ID);
        }
        feePaymentType3.setName("License Renewal Fees");
        FeePaymentType feePaymentType4 = (FeePaymentType) mongoRepositoryReactive.findById(TAX_FEE_TYPE_ID, FeePaymentType.class).block();
        if (feePaymentType4 == null) {
            feePaymentType4 = new FeePaymentType();
            feePaymentType4.setId(TAX_FEE_TYPE_ID);
        }
        feePaymentType4.setName("Tax Fees");

        FeePaymentType feePaymentType5 = (FeePaymentType) mongoRepositoryReactive.findById(TAX_RENEWAL_FEE_TYPE_ID, FeePaymentType.class).block();
        if (feePaymentType5 == null) {
            feePaymentType5 = new FeePaymentType();
            feePaymentType5.setId(TAX_RENEWAL_FEE_TYPE_ID);
        }
        feePaymentType5.setName("Tax Renewal Fees");
        mongoRepositoryReactive.delete(feePaymentType5);

        FeePaymentType feePaymentType6 = (FeePaymentType) mongoRepositoryReactive.findById(LICENSE_TRANSFER_FEE_TYPE_ID, FeePaymentType.class).block();
        if (feePaymentType6 == null) {
            feePaymentType6 = new FeePaymentType();
            feePaymentType6.setId(LICENSE_TRANSFER_FEE_TYPE_ID);
        }
        feePaymentType6.setName("License Transfer Fee");

        mongoRepositoryReactive.saveOrUpdate(feePaymentType1);
        mongoRepositoryReactive.saveOrUpdate(feePaymentType2);
        mongoRepositoryReactive.saveOrUpdate(feePaymentType3);
        mongoRepositoryReactive.saveOrUpdate(feePaymentType4);
        mongoRepositoryReactive.delete(feePaymentType5);
        mongoRepositoryReactive.saveOrUpdate(feePaymentType6);
    }
}
