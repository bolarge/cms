package com.software.finatech.lslb.cms.service.background_jobs;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.FeeService;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.FeeMailSenderAsync;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class FeeUpdater {
    private static final Logger logger = LoggerFactory.getLogger(FeeUpdater.class);
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    private FeeService feeService;
    @Autowired
    private FeeMailSenderAsync feeMailSenderAsync;

    private static final int FIFTEEN_MIN = 15 * 60 * 1000;


    @Scheduled(fixedRate = 1440 * 60 * 1000)
    @SchedulerLock(name = "Send Fee Expiry Notifications", lockAtMostFor = FIFTEEN_MIN, lockAtLeastFor = FIFTEEN_MIN)
    public void sendFeeExpiryNotifications() {
        try {
            ArrayList<Fee> feeForExpiryNotification = getPendingFeesForNotification();
            for (Fee fee : feeForExpiryNotification) {
                feeMailSenderAsync.sendFeeExpiryNotificationForFeeSync(fee);
                fee.setNextNotificationDate(fee.getNextNotificationDate().plusDays(1));
                mongoRepositoryReactive.saveOrUpdate(fee);
            }
        } catch (Throwable e) {
            logger.error("Error occurred ", e);
        }
    }

    @Scheduled(fixedRate = 1440 * 60 * 1000)
    @SchedulerLock(name = "Expire End Date Fees", lockAtMostFor = FIFTEEN_MIN, lockAtLeastFor = FIFTEEN_MIN)
    public void expireDueExpiryFees() {
        try {
            ArrayList<Fee> feeForExpiry = getFeesForExpiration();
            for (Fee fee : feeForExpiry) {
                try {
                    fee.setActive(false);
                    mongoRepositoryReactive.saveOrUpdate(fee);
                } catch (Exception e) {
                    logger.error("An error occurred while updating fee ", e);
                }
            }
        } catch (Throwable e) {
            logger.error("Error occurred ", e);
        }
    }


    @Scheduled(fixedRate = 15 * 60 * 1000)
    @SchedulerLock(name = "Activate Fees For Today", lockAtMostFor = FIFTEEN_MIN, lockAtLeastFor = FIFTEEN_MIN)
    public void startNewFees() {
        try {
            ArrayList<Fee> feeForStarting = getFeesForActivation();
            for (Fee fee : feeForStarting) {
                try {
                    fee.setActive(true);
                    mongoRepositoryReactive.saveOrUpdate(fee);

                    Fee feeForExpiry = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(fee.getLicenseTypeId(), fee.getGameTypeId(), fee.getFeePaymentTypeId());
                    if (feeForExpiry != null) {
                        feeForExpiry.setActive(false);
                        mongoRepositoryReactive.saveOrUpdate(feeForExpiry);
                    }
                } catch (Exception e) {
                    logger.error("An error occurred while updating fee ", e);
                }
            }
        } catch (Throwable e) {
            logger.error("Error occurred ", e);
        }
    }

    private ArrayList<Fee> getPendingFeesForNotification() {
        Query query = new Query();
        query.addCriteria(Criteria.where("nextNotificationDate").lte(LocalDateTime.now()));
        query.addCriteria(Criteria.where("active").is(true));
        return (ArrayList<Fee>) mongoRepositoryReactive.findAll(query, Fee.class).toStream().collect(Collectors.toList());
    }

    private ArrayList<Fee> getFeesForExpiration() {
        Query query = new Query();
        query.addCriteria(Criteria.where("endDate").lte(LocalDateTime.now()));
        query.addCriteria(Criteria.where("active").is(true));
        return (ArrayList<Fee>) mongoRepositoryReactive.findAll(query, Fee.class).toStream().collect(Collectors.toList());
    }

    private ArrayList<Fee> getFeesForActivation() {
        Query query = new Query();
        query.addCriteria(Criteria.where("effectiveDate").lte(LocalDateTime.now()));
        query.addCriteria(Criteria.where("active").is(false));
        return (ArrayList<Fee>) mongoRepositoryReactive.findAll(query, Fee.class).toStream().collect(Collectors.toList());
    }
}

