package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.EnumeratedFact;
import org.springframework.data.mongodb.core.mapping.Document;

public class RevenueNameDto extends EnumeratedFact {

    @Override
    public String getFactName() {
        return "RevenueNameDto";
    }
}
