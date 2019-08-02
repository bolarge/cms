package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.DocumentPurposeDto;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "DocumentPurposes")
public class DocumentPurpose extends EnumeratedFact{
    @Override
    public String getFactName() {
        return "DocumentPurpose";
    }

    public DocumentPurposeDto convertToDto() {
        DocumentPurposeDto dto = new DocumentPurposeDto();
        dto.setName(getName());
        dto.setId(getId());
        dto.setDescription(getDescription());
        return dto;
    }
}
