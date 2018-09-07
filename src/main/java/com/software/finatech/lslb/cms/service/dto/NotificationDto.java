package com.software.finatech.lslb.cms.service.dto;

public class NotificationDto {

    protected String institutionId;
    protected String institutionName;
    protected String gameType;
    protected String endDate;
    protected int daysToExpiration;
    protected String institutionEmail;
    protected String agentId;
    protected String gamingMachineId;
    protected String agentFullName;
    protected String agentEmailAddress;
    protected String machineNumber;
    protected String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAgentEmailAddress() {
        return agentEmailAddress;
    }

    public void setAgentEmailAddress(String agentEmailAddress) {
        this.agentEmailAddress = agentEmailAddress;
    }

    public String getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }

    public String getAgentFullName() {
        return agentFullName;
    }

    public void setAgentFullName(String agentFullName) {
        this.agentFullName = agentFullName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
    }

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

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getDaysToExpiration() {
        return daysToExpiration;
    }

    public void setDaysToExpiration(int daysToExpiration) {
        this.daysToExpiration = daysToExpiration;
    }

    public String getInstitutionEmail() {
        return institutionEmail;
    }

    public void setInstitutionEmail(String institutionEmail) {
        this.institutionEmail = institutionEmail;
    }
}
