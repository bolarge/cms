package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class MachineStatusUpdateDto {
    @NotEmpty(message = "Please provide new machine status id")
    private String machineStatusId;
    @NotEmpty(message = "Please provide machine id")
    private String machineId;

    public String getMachineStatusId() {
        return machineStatusId;
    }

    public void setMachineStatusId(String machineStatusId) {
        this.machineStatusId = machineStatusId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
}
