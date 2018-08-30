package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class GameTypeCreateDto  {
    @NotEmpty(message = "Please enter AIP Duration")
    protected String aipDuration;
    @NotEmpty(message = "Please enter License Duration")
    protected String licenseDuration;
    @NotEmpty(message = "Please enter GameType Name")
    protected String name;
    @NotEmpty(message = "Please enter Agent License Duration")
    protected String agentLicenseDuration;
    @NotEmpty(message = "Please enter Gaming Machine License Duration")
    protected String gamingMachineLicenseDuration;

    public String getAgentLicenseDuration() {
        return agentLicenseDuration;
    }

    public void setAgentLicenseDuration(String agentLicenseDuration) {
        this.agentLicenseDuration = agentLicenseDuration;
    }

    public String getGamingMachineLicenseDuration() {
        return gamingMachineLicenseDuration;
    }

    public void setGamingMachineLicenseDuration(String gamingMachineLicenseDuration) {
        this.gamingMachineLicenseDuration = gamingMachineLicenseDuration;
    }


    protected String description;

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

    public String getAipDuration() {
        return aipDuration;
    }

    public void setAipDuration(String aipDuration) {
        this.aipDuration = aipDuration;
    }

    public String getLicenseDuration() {
        return licenseDuration;
    }

    public void setLicenseDuration(String licenseDuration) {
        this.licenseDuration = licenseDuration;
    }
}
