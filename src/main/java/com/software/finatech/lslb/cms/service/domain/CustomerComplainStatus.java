package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "CustomerComplainStatus")
public class CustomerComplainStatus extends EnumeratedFact {
    @Override
    public String getFactName() {
        return "CustomerComplainStatus";
    }
}
