package com.software.finatech.lslb.cms.service.background_jobs;


import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.CustomerComplain;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.CustomerComplainStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.CustomerComplaintEmailSenderAsync;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class CustomerComplainReminder {

    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    private AuthInfoService authInfoService;
    @Autowired
    private CustomerComplaintEmailSenderAsync customerComplaintEmailSenderAsync;

    private static final int FIFTEEN_MIN = 15 * 60 * 1000;
    private static final int MAX_DAYS_BEFORE_COMPLAIN_REMINDER = 7;

    @Scheduled(fixedRate = 5 * 50 * 1000, initialDelay = 600000)
    @SchedulerLock(name = "Remind Pending Customer Complains", lockAtMostFor = FIFTEEN_MIN, lockAtLeastFor = FIFTEEN_MIN)
    public void sendReminderEmails() {
        ArrayList<AuthInfo> validLslbUsersForNotification = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_CUSTOMER_COMPLAIN_ID);
        if (validLslbUsersForNotification == null || validLslbUsersForNotification.isEmpty()) {
            return;
        }
        ArrayList<CustomerComplain> validaCustomerComplainsForReminder = getAllValidCustomerComplainsForReminder();
        if (validaCustomerComplainsForReminder == null || validaCustomerComplainsForReminder.isEmpty()) {
            return;
        }
        for (CustomerComplain customerComplain : validaCustomerComplainsForReminder) {
            customerComplaintEmailSenderAsync.sendPendingCustomerComplaintToLSLBAdminsSync(customerComplain, validLslbUsersForNotification);
            customerComplain.setNextNotificationDateTime(LocalDateTime.now().plusDays(MAX_DAYS_BEFORE_COMPLAIN_REMINDER));
            mongoRepositoryReactive.saveOrUpdate(customerComplain);
        }
    }

    private ArrayList<CustomerComplain> getAllValidCustomerComplainsForReminder() {
        Query query = new Query();
        query.addCriteria(Criteria.where("customerComplainStatusId").in(Arrays.asList(CustomerComplainStatusReferenceData.IN_REVIEW_ID,
                CustomerComplainStatusReferenceData.PENDING_ID)));
        query.addCriteria(Criteria.where("nextNotificationDateTime").lte(LocalDateTime.now()));
        return (ArrayList<CustomerComplain>) mongoRepositoryReactive.findAll(query, CustomerComplain.class).toStream().collect(Collectors.toList());
    }
}
