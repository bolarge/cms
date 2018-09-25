package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class GameTypeCreateDto {
    @Min(value = 0, message = "Please enter AIP Duration")
    protected int aipDurationMonths;
    @Min(value = 0, message = "Please enter License Duration in months")
    protected int licenseDurationMonths;
    @NotEmpty(message = "Please enter GameType Name")
    protected String name;
    @Min(value = 0, message = "Please enter Agent License Duration in months")
    protected int agentLicenseDurationMonths;
    @Min(value = 0, message = "Please enter Gaming Machine License Duration in  months")
    protected int gamingMachineLicenseDurationMonths;
    protected String shortCode;
    protected String description;

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

    public int getGamingMachineLicenseDurationMonths() {
        return gamingMachineLicenseDurationMonths;
    }

    public void setGamingMachineLicenseDurationMonths(int gamingMachineLicenseDurationMonths) {
        this.gamingMachineLicenseDurationMonths = gamingMachineLicenseDurationMonths;
    }
}
