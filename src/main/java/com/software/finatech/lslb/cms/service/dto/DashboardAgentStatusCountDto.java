package com.software.finatech.lslb.cms.service.dto;

public class DashboardAgentStatusCountDto {
    protected long activeCount;
    protected String activeStatusId;
    protected long inactiveCount;
    protected String inactiveStatusId;
    protected long blackListCount;
    protected String blackListStatusId;

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

    public String getBlackListStatusId() {
        return blackListStatusId;
    }

    public void setBlackListStatusId(String blackListStatusId) {
        this.blackListStatusId = blackListStatusId;
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

    public long getBlackListCount() {
        return blackListCount;
    }

    public void setBlackListCount(long blackListCount) {
        this.blackListCount = blackListCount;
    }
}
