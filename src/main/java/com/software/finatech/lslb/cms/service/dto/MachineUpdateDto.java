package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class MachineUpdateDto {
    @NotEmpty(message = "please provide machine address")
    private String machineAddress;
    @NotNull(message = "please provide gaming machine id")
    private String id;

    public String getMachineAddress() {
        return machineAddress;
    }

    public void setMachineAddress(String machineAddress) {
        this.machineAddress = machineAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
