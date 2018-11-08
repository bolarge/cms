package com.software.finatech.lslb.cms.service.dto;

public class DashboardMachineStatusCountDto {
    protected long activeCount;
    protected long inactiveCount;
    protected long faultyCount;
    protected long stolenCount;

    public long getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(long activeCount) {
        this.activeCount = activeCount;
    }

    public long getInactiveCount() {
        return inactiveCount;
    }

    public void setInactiveCount(long inactiveCount) {
        this.inactiveCount = inactiveCount;
    }

    public long getFaultyCount() {
        return faultyCount;
    }

    public void setFaultyCount(long faultyCount) {
        this.faultyCount = faultyCount;
    }

    public long getStolenCount() {
        return stolenCount;
    }

    public void setStolenCount(long stolenCount) {
        this.stolenCount = stolenCount;
    }
}
