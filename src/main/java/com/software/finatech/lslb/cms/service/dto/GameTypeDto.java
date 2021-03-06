package com.software.finatech.lslb.cms.service.dto;

public class GameTypeDto extends EnumeratedFactDto {

    protected String aipDuration;
    protected String licenseDuration;
    protected String agentLicenseDuration;
    protected String gamingMachineLicenseDuration;
    protected String shortCode;
    protected Boolean allowsGamingMachine;
    protected Boolean allowsGamingTerminal;
    private String gamingTerminalLicenseDuration;
    private Boolean allowsAgents;


    public Boolean getAllowsAgents() {
        return allowsAgents;
    }

    public void setAllowsAgents(Boolean allowsAgents) {
        this.allowsAgents = allowsAgents;
    }

    public String getGamingTerminalLicenseDuration() {
        return gamingTerminalLicenseDuration;
    }

    public void setGamingTerminalLicenseDuration(String gamingTerminalLicenseDuration) {
        this.gamingTerminalLicenseDuration = gamingTerminalLicenseDuration;
    }

    public Boolean getAllowsGamingTerminal() {
        return allowsGamingTerminal;
    }

    public void setAllowsGamingTerminal(Boolean allowsGamingTerminal) {
        this.allowsGamingTerminal = allowsGamingTerminal;
    }

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
