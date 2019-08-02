package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.InspectionStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class InspectionStatusReferenceData {
    public static final String NEW = "1";
    public static final String IN_PROGRESS = "2";
    public static final String CLOSED = "3";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForId(NEW, mongoRepositoryReactive, "NEW");
        loadForId(IN_PROGRESS, mongoRepositoryReactive, "IN PROGRESS");
        loadForId(CLOSED, mongoRepositoryReactive, "CLOSED");
     }

    private static void loadForId(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        InspectionStatus status = (InspectionStatus) mongoRepositoryReactive.findById(id, InspectionStatus.class).block();
        if (status == null) {
            status = new InspectionStatus();
            status.setId(id);
        }
        status.setName(name);
        mongoRepositoryReactive.saveOrUpdate(status);
    }
}
