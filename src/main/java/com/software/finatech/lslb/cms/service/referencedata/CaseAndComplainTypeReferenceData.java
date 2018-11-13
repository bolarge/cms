package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.CaseAndComplainType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class CaseAndComplainTypeReferenceData {
    public static final String OTHERS_ID = "9";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadAndForIdAndName("1", mongoRepositoryReactive, "Deposit Issues");
        loadAndForIdAndName("2", mongoRepositoryReactive, "Withdrawal issues");
        loadAndForIdAndName("3", mongoRepositoryReactive, "Non-payment of winnings");
        loadAndForIdAndName("4", mongoRepositoryReactive, "Result disputes");
        loadAndForIdAndName("5", mongoRepositoryReactive, "Technical issues");
        loadAndForIdAndName("6", mongoRepositoryReactive, "Change of betting odds");
        loadAndForIdAndName("7", mongoRepositoryReactive, "Change of terms and conditions");
        loadAndForIdAndName("8", mongoRepositoryReactive, "Cancellation of bets");
        loadAndForIdAndName(OTHERS_ID, mongoRepositoryReactive, "Others");
    }

    private static void loadAndForIdAndName(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name) {
        CaseAndComplainType type = (CaseAndComplainType) mongoRepositoryReactive.findById(id, CaseAndComplainType.class).block();
        if (type == null) {
            type = new CaseAndComplainType();
            type.setId(id);
        }
        type.setName(name);
        mongoRepositoryReactive.saveOrUpdate(type);
    }
}