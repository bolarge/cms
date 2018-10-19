package com.software.finatech.lslb.cms.service.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "GamingMachineGames")
public class GamingMachineGame extends AbstractFact {
    private String gamingMachineId;
    private String gameName;
    private String gameVersion;
    private Boolean active;

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public GamingMachine getGamingMachine() {
        if (StringUtils.isEmpty(this.gamingMachineId)) {
            return null;
        }
        return (GamingMachine) mongoRepositoryReactive.findById(this.gamingMachineId, GamingMachine.class).block();
    }

    @Override
    public String getFactName() {
        return "GamingMachineGames";
    }
}
