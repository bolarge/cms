package com.software.finatech.lslb.cms.service.background_jobs;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.FeeService;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.FeeMailSenderAsync;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.joda.time.LocalDate;
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
    private final MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private final FeeService feeService;
    private final FeeMailSenderAsync feeMailSenderAsync;

    private static final int FIFTEEN_MIN = 15 * 60 * 1000;

    @Autowired
    public FeeUpdater(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                      FeeService feeService,
                      FeeMailSenderAsync feeMailSenderAsync) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.feeService = feeService;
        this.feeMailSenderAsync = feeMailSenderAsync;
    }


    @Scheduled(fixedRate = 15 * 60 * 1000, initialDelay = 600000)
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

    @Scheduled(fixedRate = 15 * 60 * 1000, initialDelay = 600000)
    @SchedulerLock(name = "Expire End Date Fees", lockAtMostFor = FIFTEEN_MIN, lockAtLeastFor = FIFTEEN_MIN)
    public void expireDueExpiryFees() {
        try {
            ArrayList<Fee> feeForExpiry = getFeesForExpiration();
            for (Fee fee : feeForExpiry) {
                try {
                    fee.setActive(false);
                    mongoRepositoryReactive.saveOrUpdate(fee);

                    Query query = new Query();
                    query.addCriteria(Criteria.where("feePaymentTypeId").is(fee.getFeePaymentTypeId()));
                    query.addCriteria(Criteria.where("licenseTypeId").is(fee.getFeePaymentTypeId()));
                    query.addCriteria(Criteria.where("gameTypeId").is(fee.getFeePaymentTypeId()));
                    query.addCriteria(Criteria.where("effectiveDate").is(LocalDate.now()));
                    query.addCriteria(Criteria.where("active").is(false));
                    Fee feeForActivation = (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
                    if (feeForActivation != null) {
                        feeForActivation.setActive(true);
                        mongoRepositoryReactive.saveOrUpdate(feeForActivation);
                    }
                } catch (Exception e) {
                    logger.error("An error occurred while updating fee ", e);
                }
            }
        } catch (Throwable e) {
            logger.error("Error occurred ", e);
        }
    }


    @Scheduled(fixedRate = 15 * 60 * 1000, initialDelay = 600000)
    @SchedulerLock(name = "Activate Fees For Today", lockAtMostFor = 60 * 1000, lockAtLeastFor = 60 * 1000)
    public void startNewFees() {
        try {
            ArrayList<Fee> feeForStarting = getFeesForActivation();
            for (Fee fee : feeForStarting) {
                try {
                    Fee feeForExpiry = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(fee.getLicenseTypeId(), fee.getGameTypeId(), fee.getFeePaymentTypeId());
                    if (feeForExpiry != null) {
                        feeForExpiry.setActive(false);
                        mongoRepositoryReactive.saveOrUpdate(feeForExpiry);
                    }

                    fee.setActive(true);
                    mongoRepositoryReactive.saveOrUpdate(fee);
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
        query.addCriteria(Criteria.where("effectiveDate").is(LocalDate.now()));
        //  query.addCriteria(Criteria.where("endDate").gte(LocalDate.now()));
        query.addCriteria(Criteria.where("active").is(false));
        return (ArrayList<Fee>) mongoRepositoryReactive.findAll(query, Fee.class).toStream().collect(Collectors.toList());
    }
}

