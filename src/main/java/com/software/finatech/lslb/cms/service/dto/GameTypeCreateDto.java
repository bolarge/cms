package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class GameTypeCreateDto {
    @Min(value = 1, message = "AIP licence duration should be at least one month")
    protected int aipDurationMonths;
    @Min(value = 1, message = "Institution licence duration should be at least one month")
    protected int licenseDurationMonths;
    @NotEmpty(message = "Please enter GameType Name")
    protected String name;
    @Min(value = 1, message = "Agent license duration should be at least one month")
    protected int agentLicenseDurationMonths;
    protected Integer gamingMachineLicenseDurationMonths;
    @NotNull(message = "Please enter if category allows gaming machine")
    protected Boolean allowsGamingMachine;
    protected String shortCode;
    protected String description;

    public Boolean getAllowsGamingMachine() {
        return allowsGamingMachine;
    }

    public void setAllowsGamingMachine(Boolean allowsGamingMachine) {
        this.allowsGamingMachine = allowsGamingMachine;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAipDurationMonths() {
        return aipDurationMonths;
    }

    public void setAipDurationMonths(int aipDurationMonths) {
        this.aipDurationMonths = aipDurationMonths;
    }

    public int getLicenseDurationMonths() {
        return licenseDurationMonths;
    }

    public void setLicenseDurationMonths(int licenseDurationMonths) {
        this.licenseDurationMonths = licenseDurationMonths;
    }

    public int getAgentLicenseDurationMonths() {
        return agentLicenseDurationMonths;
    }

    public void setAgentLicenseDurationMonths(int agentLicenseDurationMonths) {
        this.agentLicenseDurationMonths = agentLicenseDurationMonths;
    }

    public Integer getGamingMachineLicenseDurationMonths() {
        return gamingMachineLicenseDurationMonths;
    }

    public void setGamingMachineLicenseDurationMonths(Integer gamingMachineLicenseDurationMonths) {
        this.gamingMachineLicenseDurationMonths = gamingMachineLicenseDurationMonths;
    }
}
