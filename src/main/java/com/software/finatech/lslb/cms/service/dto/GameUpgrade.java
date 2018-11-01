package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class GameUpgrade {
    @NotEmpty(message = "Please provide game id")
    private String gameId;
    @NotEmpty(message = "Please provide new game version")
    private String newGameVersion;
    private String gameName;
    private String oldGameVersion;


    public String getOldGameVersion() {
        return oldGameVersion;
    }

    public void setOldGameVersion(String oldGameVersion) {
        this.oldGameVersion = oldGameVersion;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getNewGameVersion() {
        return newGameVersion;
    }

    public void setNewGameVersion(String newGameVersion) {
        this.newGameVersion = newGameVersion;
    }
}
