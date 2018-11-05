package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.ScheduledMeetingPurpose;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class ScheduledMeetingPurposeReferenceData {
    public static final String APPLICANT_ID = "1";
    public static final String TRANSFEROR_ID = "2";
    public static final String TRANSFEREE_ID = "3";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName(APPLICANT_ID, mongoRepositoryReactive, "LICENCE APPLICANT");
        loadForIdAndName(TRANSFEREE_ID, mongoRepositoryReactive, "LICENCE TRANSFEREE");
        loadForIdAndName(TRANSFEROR_ID, mongoRepositoryReactive, "LICENCE TRANSFEROR");
    }

    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        ScheduledMeetingPurpose purpose = (ScheduledMeetingPurpose) mongoRepositoryReactive.findById(id, ScheduledMeetingPurpose.class).block();
        if (purpose == null) {
            purpose = new ScheduledMeetingPurpose();
            purpose.setId(id);
        }
        purpose.setName(name);
        purpose.setDescription(name);
        mongoRepositoryReactive.saveOrUpdate(purpose);
    }
}
