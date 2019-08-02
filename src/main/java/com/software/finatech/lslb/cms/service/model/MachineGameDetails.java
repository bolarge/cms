package com.software.finatech.lslb.cms.service.model;

import com.software.finatech.lslb.cms.service.dto.GameUpgrade;

import java.util.Collection;

public class MachineGameDetails {
    private String id;
    private String gameName;
    private String gameVersion;
    private boolean active;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public static MachineGameDetails fromGameNameAndVersion(String gameName, String gameVersion) {
        MachineGameDetails gameDetails = new MachineGameDetails();
        gameDetails.setGameName(gameName);
        gameDetails.setGameVersion(gameVersion);
        return gameDetails;
    }

    public static MachineGameDetails fromGameNameAndVersionAndState(String gameName, String gameVersion, Boolean active) {
        MachineGameDetails gameDetails = new MachineGameDetails();
        gameDetails.setGameName(gameName);
        gameDetails.setGameVersion(gameVersion);
        gameDetails.setActive(active);
        return gameDetails;
    }


    public static String machineGamesToString(Collection<MachineGameDetails> machineGameDetails) {
        StringBuilder builder = new StringBuilder();
        for (MachineGameDetails gameDetails : machineGameDetails) {
            builder.append(String.format("Game Name -> %s ,", gameDetails.getGameName()));
            builder.append(String.format("Game Version -> %s ", gameDetails.getGameVersion()));
            builder.append("\n");
        }
        return builder.toString();
    }

    public static  String machineGameUpgradeToString(Collection<GameUpgrade> gameUpgrades){
        StringBuilder builder = new StringBuilder();
        for (GameUpgrade gameUpgrade: gameUpgrades) {
            builder.append(String.format("Game Name -> %s ,", gameUpgrade.getGameName()));
            builder.append(String.format("Old Game Version -> %s ,", gameUpgrade.getOldGameVersion()));
            builder.append(String.format("New Game Version -> %s ", gameUpgrade.getNewGameVersion()));
            builder.append("\n");
        }
        return builder.toString();
    }
}
