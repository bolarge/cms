package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.EnumeratedFact;

public class FeePaymentTypeDto extends EnumeratedFact {

    @Override
    public String getFactName() {
        return "FeePaymentType";
    }
}
