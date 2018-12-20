package com.software.finatech.lslb.cms.service.background_jobs;

import com.software.finatech.lslb.cms.service.domain.FailedEmailNotification;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.EmailService;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class EmailRetryJob {
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    private EmailService emailService;
    private static final int FIFTEEN_MIN = 15 * 60 * 1000;

    @Scheduled(fixedRate = 3 * 60 * 1000, initialDelay = 600000)
    @SchedulerLock(name = "Send Pending Emails", lockAtMostFor = FIFTEEN_MIN, lockAtLeastFor = FIFTEEN_MIN)
    public void sendPendingEmails() {
        ArrayList<FailedEmailNotification> failedEmailNotifications = getPendingFailedEmails();
        for (FailedEmailNotification failedEmailNotification : failedEmailNotifications) {
            emailService.sendFailedEmail(failedEmailNotification);
        }
    }

    private ArrayList<FailedEmailNotification> getPendingFailedEmails() {
        Query query = new Query();
        query.addCriteria(Criteria.where("sent").is(false));
        query.addCriteria(Criteria.where("processing").is(false));
        return (ArrayList<FailedEmailNotification>) mongoRepositoryReactive.findAll(query, FailedEmailNotification.class).toStream().collect(Collectors.toList());
    }
}
