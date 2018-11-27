package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class GameTypeCreateDto {
    protected int aipDurationMonths;
    protected int licenseDurationMonths;
    @NotEmpty(message = "Please enter GameType Name")
    protected String name;
    protected int agentLicenseDurationMonths;
    protected int gamingMachineLicenseDurationMonths;
    @NotNull(message = "Please enter if category allows gaming machine")
    protected Boolean allowsGamingMachine;
    @NotNull(message = "Please enter if category allows gaming terminal")
    protected Boolean allowsGamingTerminal;
    protected String shortCode;
    protected String description;
    protected int gamingTerminalLicenseDurationMonths;

    public Boolean getAllowsGamingMachine() {
        return allowsGamingMachine;
    }

    public Boolean getAllowsGamingTerminal() {
        return allowsGamingTerminal;
    }

    public void setAllowsGamingTerminal(Boolean allowsGamingTerminal) {
        this.allowsGamingTerminal = allowsGamingTerminal;
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

    public int getGamingMachineLicenseDurationMonths() {
        return gamingMachineLicenseDurationMonths;
    }

    public void setGamingMachineLicenseDurationMonths(int gamingMachineLicenseDurationMonths) {
        this.gamingMachineLicenseDurationMonths = gamingMachineLicenseDurationMonths;
    }

    public int getGamingTerminalLicenseDurationMonths() {
        return gamingTerminalLicenseDurationMonths;
    }

    public void setGamingTerminalLicenseDurationMonths(int gamingTerminalLicenseDurationMonths) {
        this.gamingTerminalLicenseDurationMonths = gamingTerminalLicenseDurationMonths;
    }
}
