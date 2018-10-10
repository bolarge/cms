package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "RevenueName")
public class RevenueName extends EnumeratedFact {

    @Override
    public String getFactName() {
        return "RevenueName";
    }

    @Override
    public String toString() {
        return getName();
    }
}
