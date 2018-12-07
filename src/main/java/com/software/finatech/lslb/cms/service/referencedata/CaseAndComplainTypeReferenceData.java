package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.CaseAndComplainType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class CaseAndComplainTypeReferenceData {
    public static final String OTHERS_ID = "9";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadAndForIdAndName("1", mongoRepositoryReactive, "Deposit Issues", 4);
        loadAndForIdAndName("2", mongoRepositoryReactive, "Withdrawal issues",9);
        loadAndForIdAndName("3", mongoRepositoryReactive, "Non-payment of winnings",6);
        loadAndForIdAndName("4", mongoRepositoryReactive, "Result disputes",7);
        loadAndForIdAndName("5", mongoRepositoryReactive, "Technical issues", 8);
        loadAndForIdAndName("6", mongoRepositoryReactive, "Change of betting odds", 2);
        loadAndForIdAndName("7", mongoRepositoryReactive, "Change of terms and conditions", 3);
        loadAndForIdAndName("8", mongoRepositoryReactive, "Cancellation of bets", 1);
        loadAndForIdAndName("10", mongoRepositoryReactive, "Fraudulent Agent",5);
        loadAndForIdAndName(OTHERS_ID, mongoRepositoryReactive, "Others",20);
    }

    private static void loadAndForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name, int sortInt) {
        CaseAndComplainType type = (CaseAndComplainType) mongoRepositoryReactive.findById(id, CaseAndComplainType.class).block();
        if (type == null) {
            type = new CaseAndComplainType();
            type.setId(id);
        }
        type.setName(name);
        type.setSortInt(sortInt);
        mongoRepositoryReactive.saveOrUpdate(type);
    }
}