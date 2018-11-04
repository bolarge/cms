package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LicenseTransferSetToInstitutionRequest {
    @NotEmpty(message = "please provide to institution id")
    private String toInstitutionId;
    @NotEmpty(message = "please provide licence transfer id")
    private String licenseTransferId;

    public String getToInstitutionId() {
        return toInstitutionId;
    }

    public void setToInstitutionId(String toInstitutionId) {
        this.toInstitutionId = toInstitutionId;
    }

    public String getLicenseTransferId() {
        return licenseTransferId;
    }

    public void setLicenseTransferId(String licenseTransferId) {
        this.licenseTransferId = licenseTransferId;
    }
}
