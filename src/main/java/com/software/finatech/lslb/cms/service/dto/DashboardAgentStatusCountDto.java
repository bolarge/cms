package com.software.finatech.lslb.cms.service.dto;

public class DashboardAgentStatusCountDto {
    protected long activeCount;
    protected long inactiveCount;
    protected long blackListCount;

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
