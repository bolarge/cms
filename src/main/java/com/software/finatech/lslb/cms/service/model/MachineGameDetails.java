package com.software.finatech.lslb.cms.service.model;

import java.util.Collection;

public class MachineGameDetails {
    private String gameName;
    private String gameVersion;
    private boolean active;

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
}
