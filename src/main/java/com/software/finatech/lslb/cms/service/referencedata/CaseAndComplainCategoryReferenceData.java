package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.CaseAndComplainCategory;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class CaseAndComplainCategoryReferenceData {

    public static final String OTHERS_ID = "7";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName("1", mongoRepositoryReactive, "Player – Operator", 6);
        loadForIdAndName("2", mongoRepositoryReactive, "Operator – Operator",4);
        loadForIdAndName("3", mongoRepositoryReactive, "Operator - technical partner",5);
        loadForIdAndName("4", mongoRepositoryReactive, "Agent – operator",2);
        loadForIdAndName("5", mongoRepositoryReactive, "Operator – Agent",3);
        loadForIdAndName("6", mongoRepositoryReactive, "Agent – Agent",1);
        loadForIdAndName(OTHERS_ID, mongoRepositoryReactive, "Others",600);
    }

    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name, int sortInt) {
        CaseAndComplainCategory category = (CaseAndComplainCategory) mongoRepositoryReactive.findById(id, CaseAndComplainCategory.class).block();
        if (category == null) {
            category = new CaseAndComplainCategory();
            category.setId(id);
        }
        category.setName(name);
        category.setSortInt(sortInt);
        mongoRepositoryReactive.saveOrUpdate(category);
    }
}
