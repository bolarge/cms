package com.software.finatech.lslb.cms.service.dto;

public class InstitutionDashboardSummaryDto {
    protected String institutionName;
    protected String institutionId;
    protected String licenseNumber;
    protected String licenseId;
    protected long numberOfAgents;
    protected long numberOfGamingMachines;
    protected long numberOfGamingTerminals;
    protected String licenseStatusId;
    protected String licenseStatus;
    protected String effectiveDate;
    protected String expiryDate;
    protected String gameType;
    protected boolean allowsGamingTerminal;
    protected boolean allowsGamingMachine;

    public boolean isAllowsGamingTerminal() {
        return allowsGamingTerminal;
    }

    public void setAllowsGamingTerminal(boolean allowsGamingTerminal) {
        this.allowsGamingTerminal = allowsGamingTerminal;
    }

    public boolean isAllowsGamingMachine() {
        return allowsGamingMachine;
    }

    public void setAllowsGamingMachine(boolean allowsGamingMachine) {
        this.allowsGamingMachine = allowsGamingMachine;
    }

    public long getNumberOfGamingTerminals() {
        return numberOfGamingTerminals;
    }

    public void setNumberOfGamingTerminals(long numberOfGamingTerminals) {
        this.numberOfGamingTerminals = numberOfGamingTerminals;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public long getNumberOfAgents() {
        return numberOfAgents;
    }

    public void setNumberOfAgents(long numberOfAgents) {
        this.numberOfAgents = numberOfAgents;
    }

    public long getNumberOfGamingMachines() {
        return numberOfGamingMachines;
    }

    public void setNumberOfGamingMachines(long numberOfGamingMachines) {
        this.numberOfGamingMachines = numberOfGamingMachines;
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

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
