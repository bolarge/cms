package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PaymentRecordUpdater {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordUpdater.class);

    public static void updatePaymentRecords(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        logger.info("Started updating payment records");
        Query query = new Query();
        ArrayList<PaymentRecord> paymentRecords = (ArrayList<PaymentRecord>) mongoRepositoryReactive.findAll(query, PaymentRecord.class).toStream().collect(Collectors.toList());
        for (PaymentRecord paymentRecord : paymentRecords) {
            if (paymentRecord.getAmount() <= 0) {
                Fee fee = paymentRecord.getFee();
                if (fee != null) {
                    paymentRecord.setAmount(fee.getAmount());
                    logger.info("Updating payment record -> {}", paymentRecord.getId());
                    mongoRepositoryReactive.saveOrUpdate(paymentRecord);
                }
            }
        }
        logger.info("Finished updating payment records");
    }
}
