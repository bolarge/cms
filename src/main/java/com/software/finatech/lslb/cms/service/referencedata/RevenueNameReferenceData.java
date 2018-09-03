package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.RevenueName;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class RevenueNameReferenceData {
    public static final String INSTITUTION_REVENUE_CODE = "01";
    public static final String AGENT_REVENUE_CODE= "02";
    public static final String GAMING_MACHINE_CODE= "03";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        RevenueName revenueName = (RevenueName) mongoRepositoryReactive.findById(INSTITUTION_REVENUE_CODE, RevenueName.class).block();
        if (revenueName == null) {
            revenueName = new RevenueName();
            revenueName.setId(INSTITUTION_REVENUE_CODE);
        }
        revenueName.setName("INSTITUTION REVENUE CODE");

        RevenueName revenueName1 = (RevenueName) mongoRepositoryReactive.findById(AGENT_REVENUE_CODE, RevenueName.class).block();
        if (revenueName1 == null) {
            revenueName1 = new RevenueName();
            revenueName1.setId(AGENT_REVENUE_CODE);
        }
        revenueName1.setName("AGENT REVENUE CODE");

        RevenueName revenueName2 = (RevenueName) mongoRepositoryReactive.findById(GAMING_MACHINE_CODE, RevenueName.class).block();
        if (revenueName2 == null) {
            revenueName2 = new RevenueName();
            revenueName2.setId(GAMING_MACHINE_CODE);
        }
        revenueName2.setName("GAMING REVENUE CODE");

        mongoRepositoryReactive.saveOrUpdate(revenueName);
        mongoRepositoryReactive.saveOrUpdate(revenueName1);
        mongoRepositoryReactive.saveOrUpdate(revenueName2);
    }
}
