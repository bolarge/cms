package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.PaymentStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class PaymentStatusReferenceData {
    public static final String COMPLETED_PAYMENT_STATUS_ID = "01";
    public static final String PENDING_PAYMENT_STATUS_ID = "02";
    public static final String FAILED_PAYMENT_STATUS_ID = "03";
    public static final String PARTIALLY_PAID_STATUS_ID = "04";
    public static final String UNPAID_STATUS_ID = "05";
    public static final String PENDING_VIGIPAY_CONFIRMATION_STATUS_ID = "06";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        PaymentStatus paymentStatus1 = (PaymentStatus) mongoRepositoryReactive.findById(COMPLETED_PAYMENT_STATUS_ID, PaymentStatus.class).block();
        if (paymentStatus1 == null) {
            paymentStatus1 = new PaymentStatus();
            paymentStatus1.setId(COMPLETED_PAYMENT_STATUS_ID);
        }
        paymentStatus1.setName("PAID");


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

        PaymentStatus paymentStatus4 = (PaymentStatus) mongoRepositoryReactive.findById(PARTIALLY_PAID_STATUS_ID, PaymentStatus.class).block();
        if (paymentStatus4 == null) {
            paymentStatus4 = new PaymentStatus();
            paymentStatus4.setId(PARTIALLY_PAID_STATUS_ID);
        }
        paymentStatus4.setName("PARTIALLY PAID");

        PaymentStatus paymentStatus5 = (PaymentStatus) mongoRepositoryReactive.findById(UNPAID_STATUS_ID, PaymentStatus.class).block();
        if (paymentStatus5 == null) {
            paymentStatus5 = new PaymentStatus();
            paymentStatus5.setId(UNPAID_STATUS_ID);
        }
        paymentStatus5.setName("UNPAID");


        PaymentStatus paymentStatus6 = (PaymentStatus) mongoRepositoryReactive.findById(PENDING_VIGIPAY_CONFIRMATION_STATUS_ID, PaymentStatus.class).block();
        if (paymentStatus6 == null) {
            paymentStatus6 = new PaymentStatus();
            paymentStatus6.setId(PENDING_VIGIPAY_CONFIRMATION_STATUS_ID);
        }
        paymentStatus6.setName("PENDING VIGI PAY CONFIRMATION");
        paymentStatus6.setDescription("Vigipay has sent a payment notification but requery to confirm the status from them has not been confirmed");

        mongoRepositoryReactive.saveOrUpdate(paymentStatus1);
        mongoRepositoryReactive.saveOrUpdate(paymentStatus2);
        mongoRepositoryReactive.saveOrUpdate(paymentStatus3);
        mongoRepositoryReactive.saveOrUpdate(paymentStatus4);
        mongoRepositoryReactive.saveOrUpdate(paymentStatus5);
        mongoRepositoryReactive.saveOrUpdate(paymentStatus6);
    }
}
