package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.ScheduledMeetingStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class ScheduledMeetingStatusReferenceData {

    private static final String ONE = "1";
    private static final String TWO = "2";
    private static final String THREE = "3";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        ScheduledMeetingStatus scheduledMeetingStatus1 = (ScheduledMeetingStatus) mongoRepositoryReactive.findById(ONE, ScheduledMeetingStatus.class).block();
        if (scheduledMeetingStatus1 == null) {
            scheduledMeetingStatus1 = new ScheduledMeetingStatus();
            scheduledMeetingStatus1.setId(ONE);
        }
        scheduledMeetingStatus1.setName("PENDING");

        ScheduledMeetingStatus scheduledMeetingStatus2 = (ScheduledMeetingStatus) mongoRepositoryReactive.findById(TWO, ScheduledMeetingStatus.class).block();
        if (scheduledMeetingStatus2 == null) {
            scheduledMeetingStatus2 = new ScheduledMeetingStatus();
            scheduledMeetingStatus2.setId(TWO);
        }
        scheduledMeetingStatus2.setName("COMPLETED");

        ScheduledMeetingStatus scheduledMeetingStatus3 = (ScheduledMeetingStatus) mongoRepositoryReactive.findById(THREE, ScheduledMeetingStatus.class).block();
        if (scheduledMeetingStatus3 == null) {
            scheduledMeetingStatus3 = new ScheduledMeetingStatus();
            scheduledMeetingStatus3.setId(THREE);
        }
        scheduledMeetingStatus3.setName("CANCELED");

        mongoRepositoryReactive.saveOrUpdate(scheduledMeetingStatus1);
        mongoRepositoryReactive.saveOrUpdate(scheduledMeetingStatus2);
        mongoRepositoryReactive.saveOrUpdate(scheduledMeetingStatus3);
    }
}
