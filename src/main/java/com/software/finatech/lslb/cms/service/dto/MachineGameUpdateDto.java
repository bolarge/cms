package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.MachineGameDetails;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class MachineGameUpdateDto {
    @NotEmpty(message = "Please provide machine id")
    private String machineId;
    private Set<MachineGameDetails> machineGameDetails = new HashSet<>();
    private Set<String> removedGameIds = new HashSet<>();

    public Set<String> getRemovedGameIds() {
        return removedGameIds;
    }

    public void setRemovedGameIds(Set<String> removedGameIds) {
        this.removedGameIds = removedGameIds;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public Set<MachineGameDetails> getMachineGameDetails() {
        return machineGameDetails;
    }

    public void setMachineGameDetails(Set<MachineGameDetails> machineGameDetails) {
        this.machineGameDetails = machineGameDetails;
    }
}
