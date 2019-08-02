package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class MachineAgentAddDto {
    private String machineId;
    @NotEmpty(message = "Please provide agent id")
    private String agentId;
    private Set<String> machineIds = new HashSet<>();

    public Set<String> getMachineIds() {
        return machineIds;
    }

    public void setMachineIds(Set<String> machineIds) {
        this.machineIds = machineIds;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
