package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.MachineGameDetails;

import java.util.HashSet;
import java.util.Set;

public class MachineApprovalRequestDto extends AbstractApprovalRequestDto {
    private GamingMachineDto pendingGamingMachine;
    private Set<MachineGameDetails> newGameDetails = new HashSet<>();

    public GamingMachineDto getPendingGamingMachine() {
        return pendingGamingMachine;
    }

    public void setPendingGamingMachine(GamingMachineDto pendingGamingMachine) {
        this.pendingGamingMachine = pendingGamingMachine;
    }

    public Set<MachineGameDetails> getNewGameDetails() {
        return newGameDetails;
    }

    public void setNewGameDetails(Set<MachineGameDetails> newGameDetails) {
        this.newGameDetails = newGameDetails;
    }
}
