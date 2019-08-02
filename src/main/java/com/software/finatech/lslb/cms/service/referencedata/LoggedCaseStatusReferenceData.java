package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.LoggedCaseStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

import java.util.ArrayList;
import java.util.List;

public class LoggedCaseStatusReferenceData {

    public static final String OPEN_ID = "1";
    public static final String CLOSED_ID = "2";
    public static final String PENDING_ID = "3";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        LoggedCaseStatus caseStatus1 = (LoggedCaseStatus) mongoRepositoryReactive.findById(OPEN_ID, LoggedCaseStatus.class).block();
        if (caseStatus1 == null) {
            caseStatus1 = new LoggedCaseStatus();
            caseStatus1.setId(OPEN_ID);
        }
        caseStatus1.setName("OPEN");

        LoggedCaseStatus caseStatus2 = (LoggedCaseStatus) mongoRepositoryReactive.findById(CLOSED_ID, LoggedCaseStatus.class).block();
        if (caseStatus2 == null) {
            caseStatus2 = new LoggedCaseStatus();
            caseStatus2.setId(CLOSED_ID);
        }
        caseStatus2.setName("CLOSED");

        LoggedCaseStatus caseStatus3 = (LoggedCaseStatus) mongoRepositoryReactive.findById(PENDING_ID, LoggedCaseStatus.class).block();
        if (caseStatus3 == null) {
            caseStatus3 = new LoggedCaseStatus();
            caseStatus3.setId(PENDING_ID);
        }
        caseStatus3.setName("PENDING");
        mongoRepositoryReactive.saveOrUpdate(caseStatus1);
        mongoRepositoryReactive.saveOrUpdate(caseStatus2);
        mongoRepositoryReactive.saveOrUpdate(caseStatus3);
    }

    public static List<String> getCaseStatusIds() {
        List<String> caseStatusIds = new ArrayList<>();
        caseStatusIds.add(OPEN_ID);
        caseStatusIds.add(CLOSED_ID);
        caseStatusIds.add(PENDING_ID);
        return caseStatusIds;
    }
}
