package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.MachineApprovalRequestType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class MachineApprovalRequestTypeReferenceData {

    public static final String CREATE_GAMING_MACHINE_ID = "1";
    public static final String CREATE_GAMING_TERMINAL_ID = "2";
    public static final String ADD_GAMES_TO_GAMING_MACHINE_ID = "3";
    public static final String ADD_GAMES_TO_GAMING_TERMINAL_ID = "4";
    public static final String CHANGE_GAMING_MACHINE_STATUS = "5";
    public static final String CHANGE_GAMING_TERMINAL_STATUS = "6";
    public static final String ASSIGN_TERMINAL_TO_AGENT = "7";
    public static final String UPGRADE_GAMING_MACHINE_GAMES = "8";
    public static final String UPGRADE_GAMING_TERMINAL_GAMES = "9";
    public static final String ASSIGN_MULTIPLE_TERMINALS_TO_AGENT = "10";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForId(CREATE_GAMING_MACHINE_ID, mongoRepositoryReactive, "CREATE GAMING MACHINE");
        loadForId(CREATE_GAMING_TERMINAL_ID, mongoRepositoryReactive, "CREATE GAMING TERMINAL");
        loadForId(ADD_GAMES_TO_GAMING_MACHINE_ID, mongoRepositoryReactive, "ADD GAMES TO GAMING MACHINE");
        loadForId(ADD_GAMES_TO_GAMING_TERMINAL_ID, mongoRepositoryReactive, "ADD GAMES TO GAMING TERMINAL");
        loadForId(CHANGE_GAMING_MACHINE_STATUS, mongoRepositoryReactive, "CHANGE GAMING MACHINE STATUS");
        loadForId(CHANGE_GAMING_TERMINAL_STATUS, mongoRepositoryReactive, "CHANGE GAMING TERMINAL STATUS");
        loadForId(ASSIGN_TERMINAL_TO_AGENT, mongoRepositoryReactive, "ASSIGN TERMINAL TO AGENT");
        loadForId(UPGRADE_GAMING_MACHINE_GAMES, mongoRepositoryReactive, "UPGRADE GAMING MACHINE GAMES");
        loadForId(UPGRADE_GAMING_TERMINAL_GAMES, mongoRepositoryReactive, "UPGRADE GAMING TERMINAL GAMES");
        loadForId(ASSIGN_MULTIPLE_TERMINALS_TO_AGENT, mongoRepositoryReactive,"ASSIGN TERMINALS TO AGENT");
    }

    private static void loadForId(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        MachineApprovalRequestType approvalRequestType = (MachineApprovalRequestType) mongoRepositoryReactive.findById(id, MachineApprovalRequestType.class).block();
        if (approvalRequestType == null) {
            approvalRequestType = new MachineApprovalRequestType();
            approvalRequestType.setId(id);
        }
        approvalRequestType.setName(name);
        mongoRepositoryReactive.saveOrUpdate(approvalRequestType);
    }
}
