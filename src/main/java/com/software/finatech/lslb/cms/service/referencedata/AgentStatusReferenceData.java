package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AgentStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AgentStatusReferenceData {
    public static final String ACTIVE_ID = "1";
    public static final String IN_ACTIVE_ID = "2";
    public static final String BLACK_LISTED_ID = "3";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName(ACTIVE_ID, mongoRepositoryReactive, "ACTIVE");
        loadForIdAndName(IN_ACTIVE_ID, mongoRepositoryReactive, "IN ACTIVE");
        loadForIdAndName(BLACK_LISTED_ID, mongoRepositoryReactive, "BLACK LISTED");
        updateAgents(mongoRepositoryReactive);
    }

    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        AgentStatus status = (AgentStatus) mongoRepositoryReactive.findById(id, AgentStatus.class).block();
        if (status == null) {
            status = new AgentStatus();
            status.setId(id);
        }
        status.setName(name);
        mongoRepositoryReactive.saveOrUpdate(status);
    }

    //TODO:: Remove this agent updater
    private static void updateAgents(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("agentStatusId").is(null));
//        ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
//        for (Agent agent : agents) {
//            if (agent.isEnabled()) {
//                agent.setAgentStatusId(ACTIVE_ID);
//            } else {
//                agent.setAgentStatusId(IN_ACTIVE_ID);
//            }
//            mongoRepositoryReactive.saveOrUpdate(agent);
//        }

//        query = new Query();
//        query.addCriteria(Criteria.where("createdAt").gte(LocalDate.now()));
//        agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
//        for (Agent agent : agents) {
//            mongoRepositoryReactive.delete(agent);
//        }
    }
}
