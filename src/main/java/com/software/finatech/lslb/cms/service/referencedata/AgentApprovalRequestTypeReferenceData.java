package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.AgentApprovalRequestType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class AgentApprovalRequestTypeReferenceData {

    public static final String CREATE_AGENT_ID = "1";
    public static final String ADD_INSTITUTION_TO_AGENT_ID = "2";
    public static final String BLACK_LIST_AGENT_ID = "3";
    public static final String WHITE_LIST_AGENT_ID = "4";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        loadForIdAndName(CREATE_AGENT_ID, mongoRepositoryReactive, "CREATE AGENT");
        loadForIdAndName(ADD_INSTITUTION_TO_AGENT_ID, mongoRepositoryReactive, "ADD OPERATOR");
        loadForIdAndName(BLACK_LIST_AGENT_ID, mongoRepositoryReactive, "BLACK LIST AGENT");
        loadForIdAndName(WHITE_LIST_AGENT_ID, mongoRepositoryReactive, "WHITE LIST AGENT");
    }

    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        AgentApprovalRequestType agentApprovalRequestType = (AgentApprovalRequestType) mongoRepositoryReactive.findById(id, AgentApprovalRequestType.class).block();
        if (agentApprovalRequestType == null) {
            agentApprovalRequestType = new AgentApprovalRequestType();
            agentApprovalRequestType.setId(id);
        }
        agentApprovalRequestType.setName(name);
        mongoRepositoryReactive.saveOrUpdate(agentApprovalRequestType);
    }
}
