package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "GameType")
public class GameType extends EnumeratedFact {


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

    public GameTypeDto convertToDto() {
        GameTypeDto gameType = new GameTypeDto();
        gameType.setName(getName());
        gameType.setId(getId());
        gameType.setDescription(getDescription());
        gameType.setAipDuration(getAipDuration());
        gameType.setLicenseDuration(getLicenseDuration());
        gameType.setAgentLicenseDuration(getAgentLicenseDuration());
        gameType.setGamingMachineLicenseDuration(getGamingMachineLicenseDuration());
        return gameType;
    }

    @Override
    public String getFactName() {
        return "GameType";
    }
}
