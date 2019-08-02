package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LicenseTrannsferCreateRequest {
    @NotEmpty(message = "please provide institution id")
    private String fromInstitutionId;
    @NotEmpty(message = "please provide licence id")
    private String licenseId;

    public String getFromInstitutionId() {
        return fromInstitutionId;
    }

    public void setFromInstitutionId(String fromInstitutionId) {
        this.fromInstitutionId = fromInstitutionId;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }
}
