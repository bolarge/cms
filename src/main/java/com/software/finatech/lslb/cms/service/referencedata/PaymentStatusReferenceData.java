package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.PaymentStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class PaymentStatusReferenceData {

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        PaymentStatus paymentStatus1 = (PaymentStatus) mongoRepositoryReactive.findById("01", PaymentStatus.class).block();
        if (paymentStatus1 == null) {
            paymentStatus1 = new PaymentStatus();
            paymentStatus1.setId("01");
        }
        paymentStatus1.setName("CONFIRMED");


        PaymentStatus paymentStatus2 = (PaymentStatus) mongoRepositoryReactive.findById("02", PaymentStatus.class).block();
        if (paymentStatus2 == null) {
            paymentStatus2 = new PaymentStatus();
            paymentStatus2.setId("02");
        }
        paymentStatus2.setName("PENDING");

        PaymentStatus paymentStatus3 = (PaymentStatus) mongoRepositoryReactive.findById("03", PaymentStatus.class).block();
        if (paymentStatus3 == null) {
            paymentStatus3 = new PaymentStatus();
            paymentStatus3.setId("03");
        }
        paymentStatus3.setName("FAILED");

        mongoRepositoryReactive.saveOrUpdate(paymentStatus1);
        mongoRepositoryReactive.saveOrUpdate(paymentStatus2);
        mongoRepositoryReactive.saveOrUpdate(paymentStatus3);
    }
}
