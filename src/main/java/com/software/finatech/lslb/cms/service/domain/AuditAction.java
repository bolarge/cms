package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.AuditActionDto;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "AuditActions")
public class AuditAction extends EnumeratedFact{

    public AuditActionDto convertToDto() {
        AuditActionDto enumeratedFactDto = new AuditActionDto();
        enumeratedFactDto.setCode(getCode());
        enumeratedFactDto.setDescription(getDescription());
        enumeratedFactDto.setName(getName());
        enumeratedFactDto.setId(getId());

        return enumeratedFactDto;
    }

    @Override
    public String getFactName() {
        return "AuditAction";
    }

}
