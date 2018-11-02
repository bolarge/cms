package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class MachineGameUpgradeRequest {
    @NotEmpty(message = "please provide machine id")
    private String machineId;
    @NotEmpty(message = "game upgrade list should not be empty")
    private List<GameUpgrade> gameUpgradeList = new ArrayList<>();

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public List<GameUpgrade> getGameUpgradeList() {
        return gameUpgradeList;
    }

    public void setGameUpgradeList(List<GameUpgrade> gameUpgradeList) {
        this.gameUpgradeList = gameUpgradeList;
    }
}
