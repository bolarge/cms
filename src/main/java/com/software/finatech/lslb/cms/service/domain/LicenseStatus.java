package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.LicenseStatusDto;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "LicenseStatus")
public class LicenseStatus extends EnumeratedFact {

    public LicenseStatusDto convertToDto() {
        LicenseStatusDto licenseStatusDto = new LicenseStatusDto();
        licenseStatusDto.setName(getName());
        licenseStatusDto.setId(getId());
        licenseStatusDto.setDescription(getDescription());
        return licenseStatusDto;
    }

    @Override
    public String getFactName() {
        return "LicenseStatus";
    }
}
