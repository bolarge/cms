package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.CustomerComplainStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class CustomerComplainStatusReferenceData {
    public static final String IN_REVIEW_ID = "1";
    public static final String CLOSED_ID = "2";
    public static final String PENDING_ID = "3";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForId(IN_REVIEW_ID, mongoRepositoryReactive, "IN REVIEW");
        loadForId(CLOSED_ID, mongoRepositoryReactive, "CLOSED");
        loadForId(PENDING_ID, mongoRepositoryReactive, "PENDING");
    }

    private static void loadForId(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        CustomerComplainStatus status = (CustomerComplainStatus) mongoRepositoryReactive.findById(id, CustomerComplainStatus.class).block();
        if (status == null) {
            status = new CustomerComplainStatus();
            status.setId(id);
        }
        status.setName(name);
        status.setDescription(name);
        mongoRepositoryReactive.saveOrUpdate(status);
    }
}
