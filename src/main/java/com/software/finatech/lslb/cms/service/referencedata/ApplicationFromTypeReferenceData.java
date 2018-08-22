package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.ApplicationFormType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class ApplicationFromTypeReferenceData {

    private static final String ONE = "1";
    private static final String TWO = "2";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        ApplicationFormType applicationFormType1 = (ApplicationFormType) mongoRepositoryReactive.findById(ONE, ApplicationFormType.class).block();
        if (applicationFormType1 == null) {
            applicationFormType1 = new ApplicationFormType();
            applicationFormType1.setId(ONE);
        }
        applicationFormType1.setName("Registration");
        applicationFormType1.setName("Registration of a new license");

        ApplicationFormType applicationFormType2 = (ApplicationFormType) mongoRepositoryReactive.findById(TWO, ApplicationFormType.class).block();
        if (applicationFormType2 == null) {
            applicationFormType2 = new ApplicationFormType();
            applicationFormType2.setId(TWO);
        }
        applicationFormType2.setName("Renewal");
        applicationFormType2.setDescription("Renewal of license");

        mongoRepositoryReactive.saveOrUpdate(applicationFormType1);
        mongoRepositoryReactive.saveOrUpdate(applicationFormType2);
    }
}
