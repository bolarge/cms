package com.software.finatech.lslb.cms.service.dto;

public class DashboardMachineStatusCountDto {

    protected long activeCount;
    protected String activeStatusId;
    protected long inactiveCount;
    protected String inactiveStatusId;
    protected long faultyCount;
    protected String faultyStatusId;
    protected long stolenCount;
    protected String stolenStatusId;

    public String getActiveStatusId() {
        return activeStatusId;
    }

    public void setActiveStatusId(String activeStatusId) {
        this.activeStatusId = activeStatusId;
    }

    public String getInactiveStatusId() {
        return inactiveStatusId;
    }

    public void setInactiveStatusId(String inactiveStatusId) {
        this.inactiveStatusId = inactiveStatusId;
    }

    public String getFaultyStatusId() {
        return faultyStatusId;
    }

    public void setFaultyStatusId(String faultyStatusId) {
        this.faultyStatusId = faultyStatusId;
    }

    public String getStolenStatusId() {
        return stolenStatusId;
    }

    public void setStolenStatusId(String stolenStatusId) {
        this.stolenStatusId = stolenStatusId;
    }

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
