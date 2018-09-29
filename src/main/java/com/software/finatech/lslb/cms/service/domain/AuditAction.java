package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "AuditActions")
public class AuditAction extends EnumeratedFact{

    @Override
    public String getFactName() {
        return "AuditAction";
    }

}
