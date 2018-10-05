package com.software.finatech.lslb.cms.service.dto;

public class InstitutionsLicenseSummaryDto {
    protected String institutionId;
    protected String institutionName;
    protected String licenseStatus;
    protected String licenseStatusId;
    protected String agentCount;
    protected String gamingMachineCount;
    protected String staffCount;
    protected String LicenseExpiryDate;
    protected String licenseStatusCount;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getAgentCount() {
        return agentCount;
    }

    public void setAgentCount(String agentCount) {
        this.agentCount = agentCount;
    }

    public String getGamingMachineCount() {
        return gamingMachineCount;
    }

    public void setGamingMachineCount(String gamingMachineCount) {
        this.gamingMachineCount = gamingMachineCount;
    }

    public String getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(String staffCount) {
        this.staffCount = staffCount;
    }

    public String getLicenseExpiryDate() {
        return LicenseExpiryDate;
    }

    public void setLicenseExpiryDate(String licenseExpiryDate) {
        LicenseExpiryDate = licenseExpiryDate;
    }

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
