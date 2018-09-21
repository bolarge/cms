package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.AgentApprovalRequestType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class AgentApprovalRequestTypeReferenceData {

    public static final String CREATE_AGENT_ID = "1";
    public static final String ADD_INSTITUTION_TO_AGENT_ID = "2";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        AgentApprovalRequestType agentApprovalRequestType1 = (AgentApprovalRequestType) mongoRepositoryReactive.findById(CREATE_AGENT_ID, AgentApprovalRequestType.class).block();
        if (agentApprovalRequestType1 == null) {
            agentApprovalRequestType1 = new AgentApprovalRequestType();
            agentApprovalRequestType1.setId(CREATE_AGENT_ID);
        }
        agentApprovalRequestType1.setName("CREATE AGENT");

        AgentApprovalRequestType agentApprovalRequestType2 = (AgentApprovalRequestType) mongoRepositoryReactive.findById(ADD_INSTITUTION_TO_AGENT_ID, AgentApprovalRequestType.class).block();
        if (agentApprovalRequestType2 == null) {
            agentApprovalRequestType2 = new AgentApprovalRequestType();
            agentApprovalRequestType2.setId(ADD_INSTITUTION_TO_AGENT_ID);
        }
        agentApprovalRequestType2.setName("ADD OPERATOR FOR AGENT");

        mongoRepositoryReactive.saveOrUpdate(agentApprovalRequestType1);
        mongoRepositoryReactive.saveOrUpdate(agentApprovalRequestType2);
    }
}
