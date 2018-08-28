package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.PaymentStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class PaymentStatusReferenceData {
    public static final String CONFIRMED_PAYMENT_STATUS_ID = "01";
    public static final String PENDING_PAYMENT_STATUS_ID = "02";
    public static final String FAILED_PAYMENT_STATUS_ID = "03";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        PaymentStatus paymentStatus1 = (PaymentStatus) mongoRepositoryReactive.findById(CONFIRMED_PAYMENT_STATUS_ID, PaymentStatus.class).block();
        if (paymentStatus1 == null) {
            paymentStatus1 = new PaymentStatus();
            paymentStatus1.setId(CONFIRMED_PAYMENT_STATUS_ID);
        }
        paymentStatus1.setName("CONFIRMED");


        PaymentStatus paymentStatus2 = (PaymentStatus) mongoRepositoryReactive.findById(PENDING_PAYMENT_STATUS_ID, PaymentStatus.class).block();
        if (paymentStatus2 == null) {
            paymentStatus2 = new PaymentStatus();
            paymentStatus2.setId(PENDING_PAYMENT_STATUS_ID);
        }
        paymentStatus2.setName("PENDING");

        PaymentStatus paymentStatus3 = (PaymentStatus) mongoRepositoryReactive.findById(FAILED_PAYMENT_STATUS_ID, PaymentStatus.class).block();
        if (paymentStatus3 == null) {
            paymentStatus3 = new PaymentStatus();
            paymentStatus3.setId(FAILED_PAYMENT_STATUS_ID);
        }
        paymentStatus3.setName("FAILED");

        mongoRepositoryReactive.saveOrUpdate(paymentStatus1);
        mongoRepositoryReactive.saveOrUpdate(paymentStatus2);
        mongoRepositoryReactive.saveOrUpdate(paymentStatus3);
    }
}
