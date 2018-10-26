package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.MachineGameDetails;

import java.util.HashSet;
import java.util.Set;

public class MachineApprovalRequestDto extends AbstractApprovalRequestDto {
    private MachineDto pendingMachine;
    private Set<MachineGameDetails> newGameDetails = new HashSet<>();

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
