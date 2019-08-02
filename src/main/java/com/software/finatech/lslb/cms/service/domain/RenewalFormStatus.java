package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.LicenseStatusDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormStatusDto;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "RenewalFormStatus")
public class RenewalFormStatus extends EnumeratedFact {

    public RenewalFormStatusDto convertToDto() {
        RenewalFormStatusDto renewalFormStatusDto = new RenewalFormStatusDto();
        renewalFormStatusDto.setName(getName());
        renewalFormStatusDto.setId(getId());
        renewalFormStatusDto.setDescription(getDescription());
        return renewalFormStatusDto;
    }

    @Override
    public String getFactName() {
        return "RenewalFormStatus";
    }
}
