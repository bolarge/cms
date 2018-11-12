package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.CaseAndComplainCategory;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class CaseAndComplainCategoryReferenceData {

    private static final String OTHERS_ID = "7";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdAndName("1", mongoRepositoryReactive, "Player – Operator");
        loadForIdAndName("2", mongoRepositoryReactive, "Operator – Operator");
        loadForIdAndName("3", mongoRepositoryReactive, "Operator - technical partner");
        loadForIdAndName("4", mongoRepositoryReactive, "Agent – operator");
        loadForIdAndName("5", mongoRepositoryReactive, "Operator – Agent");
        loadForIdAndName("6", mongoRepositoryReactive, "Agent – Agent");
        loadForIdAndName(OTHERS_ID, mongoRepositoryReactive, "Others");
    }

    private static void loadForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        CaseAndComplainCategory category = (CaseAndComplainCategory) mongoRepositoryReactive.findById(id, CaseAndComplainCategory.class).block();
        if (category == null) {
            category = new CaseAndComplainCategory();
            category.setId(id);
        }
        category.setName(name);
        mongoRepositoryReactive.saveOrUpdate(category);
    }
}
