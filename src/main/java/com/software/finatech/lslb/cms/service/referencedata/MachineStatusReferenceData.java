package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.MachineStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class MachineStatusReferenceData {
    public static final String ACTIVE_ID = "1";
    public static final String IN_ACTIVE_ID = "2";
    public static final String STOLEN_ID = "3";
    public static final String FAULTY_ID = "4";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        MachineStatus status = (MachineStatus) mongoRepositoryReactive.findById(ACTIVE_ID, MachineStatus.class).block();
        if (status == null) {
            status = new MachineStatus();
            status.setId(ACTIVE_ID);
        }
        status.setName("ACTIVE");
        mongoRepositoryReactive.saveOrUpdate(status);

        status = (MachineStatus) mongoRepositoryReactive.findById(IN_ACTIVE_ID, MachineStatus.class).block();
        if (status == null) {
            status = new MachineStatus();
            status.setId(IN_ACTIVE_ID);
        }
        status.setName("IN ACTIVE");
        mongoRepositoryReactive.saveOrUpdate(status);

        status = (MachineStatus) mongoRepositoryReactive.findById(STOLEN_ID, MachineStatus.class).block();
        if (status == null) {
            status = new MachineStatus();
            status.setId(STOLEN_ID);
        }
        status.setName("STOLEN");
        mongoRepositoryReactive.saveOrUpdate(status);

        status = (MachineStatus) mongoRepositoryReactive.findById(FAULTY_ID, MachineStatus.class).block();
        if (status == null) {
            status = new MachineStatus();
            status.setId(FAULTY_ID);
        }
        status.setName("FAULTY");
        mongoRepositoryReactive.saveOrUpdate(status);
    }
}
