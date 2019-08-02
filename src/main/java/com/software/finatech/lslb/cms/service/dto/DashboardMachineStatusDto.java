package com.software.finatech.lslb.cms.service.dto;

public class DashboardMachineStatusDto {

    protected String machineStatusId;
    protected long machineStatusCount;

    public String getMachineStatusId() {
        return machineStatusId;
    }

    public void setMachineStatusId(String machineStatusId) {
        this.machineStatusId = machineStatusId;
    }

    public long getMachineStatusCount() {
        return machineStatusCount;
    }

    public void setMachineStatusCount(long machineStatusCount) {
        this.machineStatusCount = machineStatusCount;
    }
}
