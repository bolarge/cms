package com.software.finatech.lslb.cms.service.dto;

public class LicenseStatusSummaryDto {
    protected String licenseStatus;
    protected String licenseStatusId;
    protected String licenseStatusCount;

    public String getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(String licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }

    public String getLicenseStatusCount() {
        return licenseStatusCount;
    }

    public void setLicenseStatusCount(String licenseStatusCount) {
        this.licenseStatusCount = licenseStatusCount;
    }
}
