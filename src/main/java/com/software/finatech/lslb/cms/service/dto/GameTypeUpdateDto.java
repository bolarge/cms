package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class GameTypeUpdateDto  {

    @NotEmpty(message = "Please enter AIP Duration in months")
    protected int aipDurationMonths;
    @NotEmpty(message = "Please enter License Duration in months")
    protected int licenseDuration;
    @NotEmpty(message = "Please enter GameType Id")
    protected String id;
    @NotEmpty(message = "Please enter GameType Name")
    protected String name;
    protected String description;
    @NotEmpty(message = "Please enter Agent License Duration in months")
    protected int  agentLicenseDurationMonths;
    @NotEmpty(message = "Please enter Gaming Machine License Duration in months")
    protected int gamingMachineLicenseDurationMonths;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAipDurationMonths() {
        return aipDurationMonths;
    }

    public void setAipDurationMonths(int aipDurationMonths) {
        this.aipDurationMonths = aipDurationMonths;
    }

    public int getLicenseDuration() {
        return licenseDuration;
    }

    public void setLicenseDuration(int licenseDuration) {
        this.licenseDuration = licenseDuration;
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
