package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.ScheduledMeetingStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class ScheduledMeetingStatusReferenceData {

    public static final String PENDING_STATUS_ID = "1";
    public static final String COMPLETED_STATUS_ID = "2";
    public static final String CANCELED_STATUS_ID = "3";
    public static final String UNATTENDED_STATUS_ID = "4";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        ScheduledMeetingStatus scheduledMeetingStatus1 = (ScheduledMeetingStatus) mongoRepositoryReactive.findById(PENDING_STATUS_ID, ScheduledMeetingStatus.class).block();
        if (scheduledMeetingStatus1 == null) {
            scheduledMeetingStatus1 = new ScheduledMeetingStatus();
            scheduledMeetingStatus1.setId(PENDING_STATUS_ID);
        }
        scheduledMeetingStatus1.setName("PENDING");

        ScheduledMeetingStatus scheduledMeetingStatus2 = (ScheduledMeetingStatus) mongoRepositoryReactive.findById(COMPLETED_STATUS_ID, ScheduledMeetingStatus.class).block();
        if (scheduledMeetingStatus2 == null) {
            scheduledMeetingStatus2 = new ScheduledMeetingStatus();
            scheduledMeetingStatus2.setId(COMPLETED_STATUS_ID);
        }
        scheduledMeetingStatus2.setName("COMPLETED");

        ScheduledMeetingStatus scheduledMeetingStatus3 = (ScheduledMeetingStatus) mongoRepositoryReactive.findById(CANCELED_STATUS_ID, ScheduledMeetingStatus.class).block();
        if (scheduledMeetingStatus3 == null) {
            scheduledMeetingStatus3 = new ScheduledMeetingStatus();
            scheduledMeetingStatus3.setId(CANCELED_STATUS_ID);
        }
        scheduledMeetingStatus3.setName("CANCELED");

        ScheduledMeetingStatus scheduledMeetingStatus4 = (ScheduledMeetingStatus) mongoRepositoryReactive.findById(UNATTENDED_STATUS_ID, ScheduledMeetingStatus.class).block();
        if (scheduledMeetingStatus4 == null) {
            scheduledMeetingStatus4 = new ScheduledMeetingStatus();
            scheduledMeetingStatus4.setId(UNATTENDED_STATUS_ID);
        }
        scheduledMeetingStatus3.setName("UNATTENDED");

        mongoRepositoryReactive.saveOrUpdate(scheduledMeetingStatus1);
        mongoRepositoryReactive.saveOrUpdate(scheduledMeetingStatus2);
        mongoRepositoryReactive.saveOrUpdate(scheduledMeetingStatus3);
        mongoRepositoryReactive.saveOrUpdate(scheduledMeetingStatus4);
    }
}
