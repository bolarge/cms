package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.LoggedCaseOutcome;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LoggedCaseOutcomeReferenceData {
    public static final String LICENSE_TERMINATED_ID = "1";
    public static final String LICENSE_REVOKED_ID = "2";
    public static final String LICENSE_SUSPENDED_ID = "3";
    public static final String PENALTY_ID = "4";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName(LICENSE_TERMINATED_ID, mongoRepositoryReactive, "LICENCE TERMINATED");
        loadForIdAndName(LICENSE_REVOKED_ID, mongoRepositoryReactive, "LICENCE REVOKED");
        loadForIdAndName(LICENSE_SUSPENDED_ID, mongoRepositoryReactive, "LICENCE SUSPENDED");
        loadForIdAndName(PENALTY_ID, mongoRepositoryReactive, "PENALTY");
        deleteAgents(mongoRepositoryReactive);
    }

    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        LoggedCaseOutcome outcome = (LoggedCaseOutcome) mongoRepositoryReactive.findById(id, LoggedCaseOutcome.class).block();
        if (outcome == null) {
            outcome = new LoggedCaseOutcome();
            outcome.setId(id);
        }
        outcome.setName(name);
        mongoRepositoryReactive.saveOrUpdate(outcome);
    }

    private static void deleteAgents(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(Arrays.asList("1", "2", "3", "4", "5")));
        ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
        for (Agent agent : agents) {
            mongoRepositoryReactive.delete(agent);
        }
    }
}
