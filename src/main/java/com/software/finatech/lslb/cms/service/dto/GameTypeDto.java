package com.software.finatech.lslb.cms.service.dto;

public class GameTypeDto extends EnumeratedFactDto {

    protected String aipDuration;
    protected String licenseDuration;
    protected String agentLicenseDuration;
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
