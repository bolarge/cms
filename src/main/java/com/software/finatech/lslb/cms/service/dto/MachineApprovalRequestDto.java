package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.MachineGameDetails;

import java.util.HashSet;
import java.util.Set;

public class MachineApprovalRequestDto extends AbstractApprovalRequestDto {
    private MachineDto pendingMachine;
    private Set<MachineGameDetails> newGameDetails = new HashSet<>();
    private String newMachineStatusName;
    private Set<GameUpgrade> gameUpgrades = new HashSet<>();
    private String agentFullName;
    private String agentId;
    private Set<MachineDto> pendingMachines = new HashSet<>();

    public Set<MachineDto> getPendingMachines() {
        return pendingMachines;
    }

    public void setPendingMachines(Set<MachineDto> pendingMachines) {
        this.pendingMachines = pendingMachines;
    }

    public String getAgentFullName() {
        return agentFullName;
    }

    public void setAgentFullName(String agentFullName) {
        this.agentFullName = agentFullName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Set<GameUpgrade> getGameUpgrades() {
        return gameUpgrades;
    }

    public void setGameUpgrades(Set<GameUpgrade> gameUpgrades) {
        this.gameUpgrades = gameUpgrades;
    }

    public String getNewMachineStatusName() {
        return newMachineStatusName;
    }

    public void setNewMachineStatusName(String newMachineStatusName) {
        this.newMachineStatusName = newMachineStatusName;
    }

    public MachineDto getPendingMachine() {
        return pendingMachine;
    }

    public void setPendingMachine(MachineDto pendingMachine) {
        this.pendingMachine = pendingMachine;
    }

    public Set<MachineGameDetails> getNewGameDetails() {
        return newGameDetails;
    }

    public void setNewGameDetails(Set<MachineGameDetails> newGameDetails) {
        this.newGameDetails = newGameDetails;
    }
}
