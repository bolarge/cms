package com.software.finatech.lslb.cms.service.dto;

public class AgentDashboardSummaryDto {
    protected String agentName;
    protected String agentId;
    protected String licenseNumber;
    protected long numberOfInstitutions;
    protected String licenseStatusId;
    protected String licenseStatus;
    protected String effectiveDate;
    protected String expirtyDate;
    protected String gameType;


    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public long getNumberOfInstitutions() {
        return numberOfInstitutions;
    }

    public void setNumberOfInstitutions(long numberOfInstitutions) {
        this.numberOfInstitutions = numberOfInstitutions;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }


    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }


    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }

    public String getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(String licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getExpirtyDate() {
        return expirtyDate;
    }

    public void setExpirtyDate(String expirtyDate) {
        this.expirtyDate = expirtyDate;
    }
}
