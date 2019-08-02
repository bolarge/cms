package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.MachineType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class MachineTypeReferenceData {

    public static final String GAMING_MACHINE_ID = "1";
    public static final String GAMING_TERMINAL_ID = "2";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        MachineType machineType = (MachineType) mongoRepositoryReactive.findById(GAMING_MACHINE_ID, MachineType.class).block();
        if (machineType == null) {
            machineType = new MachineType();
            machineType.setId(GAMING_MACHINE_ID);
        }
        machineType.setName("GAMING MACHINE");
        mongoRepositoryReactive.saveOrUpdate(machineType);

        machineType = (MachineType) mongoRepositoryReactive.findById(GAMING_TERMINAL_ID, MachineType.class).block();
        if (machineType == null) {
            machineType = new MachineType();
            machineType.setId(GAMING_TERMINAL_ID);
        }
        machineType.setName("GAMING TERMINAL");
        mongoRepositoryReactive.saveOrUpdate(machineType);
    }
}
