package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "CaseAndComplainCategory")
public class CaseAndComplainCategory extends EnumeratedFact {
    private int sortInt;

    public int getSortInt() {
        return sortInt;
    }

    public void setSortInt(int sortInt) {
        this.sortInt = sortInt;
    }
}
