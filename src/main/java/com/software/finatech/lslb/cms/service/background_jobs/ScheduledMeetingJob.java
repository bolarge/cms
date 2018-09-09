package com.software.finatech.lslb.cms.service.background_jobs;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.domain.ScheduledMeeting;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.ScheduledMeetingStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import com.software.finatech.lslb.cms.service.service.contracts.ScheduledMeetingService;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class ScheduledMeetingJob {

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private ScheduledMeetingService scheduledMeetingService;
    private AuthInfoService authInfoService;
    private InstitutionService institutionService;

    @Autowired
    public ScheduledMeetingJob(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                               ScheduledMeetingService scheduledMeetingService,
                               AuthInfoService authInfoService,
                               InstitutionService institutionService) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.scheduledMeetingService = scheduledMeetingService;
        this.authInfoService = authInfoService;
        this.institutionService = institutionService;
    }


    @Scheduled(fixedRate = 30000)
    public void remindPendingMeetingInvites() {
        ArrayList<ScheduledMeeting> pendingScheduledMeetings = getAllPendingScheduledMeetings();
        for (ScheduledMeeting scheduledMeeting : pendingScheduledMeetings) {
            handleScheduledMeetingNotification(scheduledMeeting);
        }
    }

    public ArrayList<ScheduledMeeting> getAllPendingScheduledMeetings() {
        Query queryForPendingScheduled = new Query();
        String pendingStatusId = ScheduledMeetingStatusReferenceData.PENDING_STATUS_ID;
        queryForPendingScheduled.addCriteria(Criteria.where("scheduledMeetingStatusId").is(pendingStatusId));
        return (ArrayList<ScheduledMeeting>) mongoRepositoryReactive.findAll(queryForPendingScheduled, ScheduledMeeting.class).toStream().collect(Collectors.toList());
    }

    private void handleScheduledMeetingNotification(ScheduledMeeting scheduledMeeting) {
        DateTime meetingDate = scheduledMeeting.getMeetingDate();
        DateTime presentTime = new DateTime();

        Duration duration = new Duration(presentTime, meetingDate);
        long differenceInHours = duration.getStandardHours();

        if (differenceInHours > 23 && differenceInHours < 25 && !scheduledMeeting.isFirstReminderMailSent()) {
            sendReminderEmailsToMeetingAttenders(scheduledMeeting);
            scheduledMeeting.setFirstReminderMailSent(true);
            mongoRepositoryReactive.saveOrUpdate(scheduledMeeting);
            return;
        }

        if (differenceInHours > 0 && differenceInHours < 2 && !scheduledMeeting.isSecondReminderMailSent()) {
            sendReminderEmailsToMeetingAttenders(scheduledMeeting);
            scheduledMeeting.setSecondReminderMailSent(true);
            mongoRepositoryReactive.saveOrUpdate(scheduledMeeting);
            return;
        }

        if (differenceInHours > -13 && differenceInHours < -9 && !scheduledMeeting.isExpirationEmailSent()) {
            sendExpirationEmail(scheduledMeeting);
            return;
        }

        if (scheduledMeeting.isExpirationEmailSent() && differenceInHours > -25 && differenceInHours < -23) {
            changeMeetingToUnattended(scheduledMeeting);
            return;
        }
    }

    private void changeMeetingToUnattended(ScheduledMeeting scheduledMeeting) {
        String unattendedStatusId = ScheduledMeetingStatusReferenceData.UNATTENDED_STATUS_ID;
        scheduledMeeting.setScheduledMeetingStatusId(unattendedStatusId);
        mongoRepositoryReactive.save(scheduledMeeting);
    }

    private void sendExpirationEmail(ScheduledMeeting scheduledMeeting) {
        Institution institution = institutionService.findById(scheduledMeeting.getInstitutionId());
        String creatorMailSubject = String.format("POST REMINDER : Scheduled meeting with %s", institution.getInstitutionName());
        scheduledMeetingService.sendMeetingNotificationEmailToMeetingCreator(creatorMailSubject, "ScheduledMeetingExpiryNotificationForLslbAdmin", scheduledMeeting);
        scheduledMeeting.setExpirationEmailSent(true);
        mongoRepositoryReactive.saveOrUpdate(scheduledMeeting);
    }

    private void sendReminderEmailsToMeetingAttenders(ScheduledMeeting scheduledMeeting) {
        Institution institution = institutionService.findById(scheduledMeeting.getInstitutionId());
        String creatorMailSubject = String.format("REMINDER : Scheduled meeting with %s", institution.getInstitutionName());
        scheduledMeetingService.sendMeetingNotificationEmailToMeetingCreator(creatorMailSubject, "ScheduledMeetingReminderNotificationForLslbAdmin", scheduledMeeting);
        ArrayList<AuthInfo> gamingOperatorAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(scheduledMeeting.getInstitutionId());
        for (AuthInfo gamingOperatorAdmin : gamingOperatorAdmins) {
            scheduledMeetingService.sendMeetingNotificationEmailToAttendee("REMINDER: Scheduled meeting with Lagos State Lotteries Board", "ScheduledMeetingReminderNotificationForGamingOperator",
                    gamingOperatorAdmin, scheduledMeeting);
        }
    }
}
