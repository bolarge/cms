package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.MachineApprovalRequestType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class MachineApprovalRequestTypeReferenceData {

    public static final String CREATE_MACHINE_ID = "1";
    public static final String ADD_GAMES_TO_MACHINE_ID = "2";
    public static final String CHANGE_MACHINE_STATUS= "3";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        MachineApprovalRequestType approvalRequestType = (MachineApprovalRequestType) mongoRepositoryReactive.findById(CREATE_MACHINE_ID, MachineApprovalRequestType.class).block();
        if (approvalRequestType == null) {
            approvalRequestType = new MachineApprovalRequestType();
            approvalRequestType.setId(CREATE_MACHINE_ID);
        }
        approvalRequestType.setName("CREATE GAMING MACHINE");
        mongoRepositoryReactive.saveOrUpdate(approvalRequestType);

        approvalRequestType = (MachineApprovalRequestType) mongoRepositoryReactive.findById(ADD_GAMES_TO_MACHINE_ID, MachineApprovalRequestType.class).block();
        if (approvalRequestType == null) {
            approvalRequestType = new MachineApprovalRequestType();
            approvalRequestType.setId(ADD_GAMES_TO_MACHINE_ID);
        }
        approvalRequestType.setName("ADD GAMES TO GAMING MACHINE");
        mongoRepositoryReactive.saveOrUpdate(approvalRequestType);

        approvalRequestType = (MachineApprovalRequestType) mongoRepositoryReactive.findById(CHANGE_MACHINE_STATUS, MachineApprovalRequestType.class).block();
        if (approvalRequestType == null) {
            approvalRequestType = new MachineApprovalRequestType();
            approvalRequestType.setId(CHANGE_MACHINE_STATUS);
        }
        approvalRequestType.setName("CHANGE MACHINE STATUS");
        mongoRepositoryReactive.saveOrUpdate(approvalRequestType);
    }
}
