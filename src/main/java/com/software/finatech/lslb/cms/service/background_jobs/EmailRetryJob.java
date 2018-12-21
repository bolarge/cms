package com.software.finatech.lslb.cms.service.background_jobs;

import com.software.finatech.lslb.cms.service.domain.FailedEmailNotification;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.EmailService;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class EmailRetryJob {

    private static final Logger logger = LoggerFactory.getLogger(EmailRetryJob.class);
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    private EmailService emailService;
    private static final int FIFTEEN_MIN = 15 * 60 * 1000;
    private static final int THREE_MIN = 3 * 60 * 1000;

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 6000)
    @SchedulerLock(name = "Send Pending Emails", lockAtMostFor = 60 * 1000, lockAtLeastFor = 60 * 1000)
    public void sendPendingEmails() {
        try {
            ArrayList<FailedEmailNotification> failedEmailNotifications = getPendingFailedEmails();
            for (FailedEmailNotification failedEmailNotification : failedEmailNotifications) {
                emailService.sendFailedEmail(failedEmailNotification);
            }
        } catch (Throwable e) {
            logger.info("An error occurred while sending pending emails", e);
        }
    }

    private ArrayList<FailedEmailNotification> getPendingFailedEmails() {
        Query query = new Query();
        query.addCriteria(Criteria.where("sent").is(false));
        query.addCriteria(Criteria.where("processing").is(false));
        Sort sort = new Sort(Sort.Direction.DESC, "createdAt");
        query.with(PageRequest.of(0, 100, sort));
        query.with(sort);
        return (ArrayList<FailedEmailNotification>) mongoRepositoryReactive.findAll(query, FailedEmailNotification.class).toStream().collect(Collectors.toList());
    }
}
