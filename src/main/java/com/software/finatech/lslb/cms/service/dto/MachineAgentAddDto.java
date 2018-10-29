package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class MachineAgentAddDto {
    @NotEmpty(message = "Please provide machine id")
    private String machineId;
    @NotEmpty(message = "Please provide agent id")
    private String agentId;

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
