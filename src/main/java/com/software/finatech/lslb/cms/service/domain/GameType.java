package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "GameType")
public class GameType extends EnumeratedFact {

    protected int aipDurationMonths;
    protected int institutionLicenseDurationMonths;
    protected int agentLicenseDurationMonths;
    protected int gamingMachineLicenseDurationMonths;
    protected String shortCode;
    protected Boolean allowsGamingTerminal;
    protected Boolean allowsGamingMachine;

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

    public int getAipDurationMonths() {
        return aipDurationMonths;
    }

    public void setAipDurationMonths(int aipDurationMonths) {
        this.aipDurationMonths = aipDurationMonths;
    }

    public int getInstitutionLicenseDurationMonths() {
        return institutionLicenseDurationMonths;
    }

    public void setInstitutionLicenseDurationMonths(int institutionLicenseDurationMonths) {
        this.institutionLicenseDurationMonths = institutionLicenseDurationMonths;
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

    public GameTypeDto convertToDto() {
        GameTypeDto gameType = new GameTypeDto();
        gameType.setName(getName());
        gameType.setId(getId());
        gameType.setDescription(getDescription());
        gameType.setAipDuration(String.valueOf(getAipDurationMonths()));
        gameType.setLicenseDuration(String.valueOf(getInstitutionLicenseDurationMonths()));
        gameType.setAgentLicenseDuration(String.valueOf(getAgentLicenseDurationMonths()));
        gameType.setGamingMachineLicenseDuration(String.valueOf(getGamingMachineLicenseDurationMonths()));
        gameType.setShortCode(getShortCode());
        gameType.setAllowsGamingMachine(getAllowsGamingMachine());
        gameType.setAllowsGamingTerminal(getAllowsGamingTerminal());
        return gameType;
    }

    @Override
    public String getFactName() {
        return "GameType";
    }
}
