package com.software.finatech.lslb.cms.service.background_jobs;

import com.software.finatech.lslb.cms.service.domain.ScheduledMeeting;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.ScheduledMeetingStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ScheduledMeetingMailSenderAsync;
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
public class ScheduledMeetingJob {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledMeetingJob.class);

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private ScheduledMeetingMailSenderAsync scheduledMeetingMailSenderAsync;

    private static final int MAX_NUMBER_OF_METING_REMINDERS = 2;
    private static final int POST_MEETING_REMINDER_DAYS = 7;
    private static final int FIFTEEN_MIN = 15 * 60 * 1000;

    @Autowired
    public ScheduledMeetingJob(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                               ScheduledMeetingMailSenderAsync scheduledMeetingMailSenderAsync) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.scheduledMeetingMailSenderAsync = scheduledMeetingMailSenderAsync;
    }


    @Scheduled(fixedRate = 5 * 50 * 1000, initialDelay = 600000)
    @SchedulerLock(name = "Remind Meeting Invites After Meeting", lockAtMostFor = FIFTEEN_MIN, lockAtLeastFor = FIFTEEN_MIN)
    public void postMeetingReminderForMeetingInvites() {
        ArrayList<ScheduledMeeting> pendingScheduledMeetings = getAllScheduledMeetingsForMeetingPostMeetingReminder();
        if (pendingScheduledMeetings == null || pendingScheduledMeetings.isEmpty()) {
            return;
        }
        for (ScheduledMeeting scheduledMeeting : pendingScheduledMeetings) {
            scheduledMeetingMailSenderAsync.sendEmailToMeetingCreator("scheduled-meetings/ScheduledMeetingPostMeetingReminderNotificationForLslbAdmin", String.format("Post Meeting Reminder : Meeting with %s", scheduledMeeting.getInstitutionName()), scheduledMeeting);
            scheduledMeeting.setNextPostMeetingReminderDate(scheduledMeeting.getNextPostMeetingReminderDate().plusDays(POST_MEETING_REMINDER_DAYS));
            scheduledMeeting.setReminderNotificationCount(scheduledMeeting.getReminderNotificationCount() + 1);
            mongoRepositoryReactive.saveOrUpdate(scheduledMeeting);
        }
    }

    @Scheduled(fixedRate = 5 * 50 * 1000, initialDelay = 600000)
    @SchedulerLock(name = "Remind Meeting Invites", lockAtMostFor = FIFTEEN_MIN, lockAtLeastFor = FIFTEEN_MIN)
    public void remindMeetingForMeetingInvitees() {
        ArrayList<ScheduledMeeting> pendingScheduledMeetings = getAllScheduledMeetingsForMeetingReminder();
        if (pendingScheduledMeetings == null || pendingScheduledMeetings.isEmpty()) {
            return;
        }
        for (ScheduledMeeting scheduledMeeting : pendingScheduledMeetings) {
            scheduledMeetingMailSenderAsync.sendEmailToMeetingCreator("scheduled-meetings/ScheduledMeetingReminderNotificationForLslbAdmin", String.format("Reminder: Meeting with %s", scheduledMeeting.getInstitutionName()), scheduledMeeting);
            scheduledMeetingMailSenderAsync.sendEmailToMeetingInvitedOperators("scheduled-meetings/ScheduledMeetingReminderNotificationForGamingOperator", "Reminder: Meeting With Lagos State Lotteries Board", scheduledMeeting);
            scheduledMeetingMailSenderAsync.sendEmailToMeetingRecipients("scheduled-meetings/ScheduledMeetingReminderNotificationForLslbAdmin", String.format("Reminder: Meeting with %s", scheduledMeeting.getInstitutionName()), scheduledMeeting, scheduledMeeting.getRecipients());
            scheduledMeeting.setReminderSent(true);
            mongoRepositoryReactive.saveOrUpdate(scheduledMeeting);
        }
    }

    @Scheduled(fixedRate = 5 * 50 * 1000, initialDelay = 600000)
    @SchedulerLock(name = "Expire unattended meetings", lockAtMostFor = FIFTEEN_MIN, lockAtLeastFor = FIFTEEN_MIN)
    public void expireUnattendedMeetings() {
        ArrayList<ScheduledMeeting> scheduleMeetingForExpiration = getAllScheduledMeetingsForMeetingExpiration();
        for (ScheduledMeeting scheduledMeeting : scheduleMeetingForExpiration) {
            changeMeetingToUnattended(scheduledMeeting);
        }
    }

    private ArrayList<ScheduledMeeting> getAllScheduledMeetingsForMeetingReminder() {
        Query query = new Query();
        query.addCriteria(Criteria.where("meetingReminderDate").lte(LocalDateTime.now()));
        query.addCriteria(Criteria.where("scheduledMeetingStatusId").is(ScheduledMeetingStatusReferenceData.PENDING_STATUS_ID));
        query.addCriteria(Criteria.where("reminderSent").is(false));
        return (ArrayList<ScheduledMeeting>) mongoRepositoryReactive.findAll(query, ScheduledMeeting.class).toStream().collect(Collectors.toList());
    }


    private ArrayList<ScheduledMeeting> getAllScheduledMeetingsForMeetingPostMeetingReminder() {
        Query query = new Query();
        query.addCriteria(Criteria.where("nextPostMeetingReminderDate").lte(LocalDateTime.now()));
        query.addCriteria(Criteria.where("scheduledMeetingStatusId").is(ScheduledMeetingStatusReferenceData.PENDING_STATUS_ID));
        return (ArrayList<ScheduledMeeting>) mongoRepositoryReactive.findAll(query, ScheduledMeeting.class).toStream().collect(Collectors.toList());
    }


    private ArrayList<ScheduledMeeting> getAllScheduledMeetingsForMeetingExpiration() {
        Query query = new Query();
        query.addCriteria(Criteria.where("reminderNotificationCount").gte(MAX_NUMBER_OF_METING_REMINDERS));
        query.addCriteria(Criteria.where("scheduledMeetingStatusId").is(ScheduledMeetingStatusReferenceData.PENDING_STATUS_ID));
        query.addCriteria(Criteria.where("reminderSent").is(true));
        return (ArrayList<ScheduledMeeting>) mongoRepositoryReactive.findAll(query, ScheduledMeeting.class).toStream().collect(Collectors.toList());
    }

    private void changeMeetingToUnattended(ScheduledMeeting scheduledMeeting) {
        logger.info("Trying to expire scheduled meeting with id {}", scheduledMeeting.getId());
        scheduledMeeting.setScheduledMeetingStatusId(ScheduledMeetingStatusReferenceData.UNATTENDED_STATUS_ID);
        scheduledMeeting.setNextPostMeetingReminderDate(null);
        scheduledMeeting.setMeetingReminderDate(null);
        mongoRepositoryReactive.saveOrUpdate(scheduledMeeting);
    }
}
