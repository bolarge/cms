package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.MachineApprovalRequestType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class MachineApprovalRequestTypeReferenceData {

    public static final String CREATE_GAMING_MACHINE_ID = "1";
    public static final String ADD_GAMES_TO_GAMING_MACHINE_ID = "2";
    public static final String CREATE_GAMING_TERMINAL_ID = "3";
    public static final String ADD_GAMES_TO_GAMING_TERMINAL_ID = "4";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        MachineApprovalRequestType approvalRequestType = (MachineApprovalRequestType) mongoRepositoryReactive.findById(CREATE_GAMING_MACHINE_ID, MachineApprovalRequestType.class).block();
        if (approvalRequestType == null) {
            approvalRequestType = new MachineApprovalRequestType();
            approvalRequestType.setId(CREATE_GAMING_MACHINE_ID);
        }
        approvalRequestType.setName("CREATE GAMING MACHINE");
        mongoRepositoryReactive.saveOrUpdate(approvalRequestType);

        approvalRequestType = (MachineApprovalRequestType) mongoRepositoryReactive.findById(ADD_GAMES_TO_GAMING_MACHINE_ID, MachineApprovalRequestType.class).block();
        if (approvalRequestType == null) {
            approvalRequestType = new MachineApprovalRequestType();
            approvalRequestType.setId(ADD_GAMES_TO_GAMING_MACHINE_ID);
        }
        approvalRequestType.setName("ADD GAMES TO GAMING MACHINE");
        mongoRepositoryReactive.saveOrUpdate(approvalRequestType);

        approvalRequestType = (MachineApprovalRequestType) mongoRepositoryReactive.findById(CREATE_GAMING_TERMINAL_ID, MachineApprovalRequestType.class).block();
        if (approvalRequestType == null) {
            approvalRequestType = new MachineApprovalRequestType();
            approvalRequestType.setId(CREATE_GAMING_TERMINAL_ID);
        }
        approvalRequestType.setName("CREATE GAMING TERMINAL");
        mongoRepositoryReactive.saveOrUpdate(approvalRequestType);

        approvalRequestType = (MachineApprovalRequestType) mongoRepositoryReactive.findById(ADD_GAMES_TO_GAMING_MACHINE_ID, MachineApprovalRequestType.class).block();
        if (approvalRequestType == null) {
            approvalRequestType = new MachineApprovalRequestType();
            approvalRequestType.setId(ADD_GAMES_TO_GAMING_TERMINAL_ID);
        }
        approvalRequestType.setName("ADD GAMES TO GAMING TERMINAL");
        mongoRepositoryReactive.saveOrUpdate(approvalRequestType);
    }
}
