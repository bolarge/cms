package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.Gender;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class GenderReferenceData {

    public static final String MALE_ID = "1";
    public static final String FEMALE_ID = "2";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName(MALE_ID, mongoRepositoryReactive, "MALE");
        loadForIdAndName(FEMALE_ID, mongoRepositoryReactive, "FEMALE");
    }

    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        Gender gender = (Gender) mongoRepositoryReactive.findById(id, Gender.class).block();
        if (gender == null) {
            gender = new Gender();
            gender.setId(id);
        }
        gender.setName(name);
        mongoRepositoryReactive.saveOrUpdate(gender);
    }
}
