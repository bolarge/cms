package com.software.finatech.lslb.cms.service.dto;

public class CasesDashboardStatusCountDto {
    protected long openedCount;
    protected long closedCount;
    protected long pendingCount;
    protected long totalCount;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getOpenedCount() {
        return openedCount;
    }

    public void setOpenedCount(long openedCount) {
        this.openedCount = openedCount;
    }

    public long getClosedCount() {
        return closedCount;
    }

    public void setClosedCount(long closedCount) {
        this.closedCount = closedCount;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }
}
