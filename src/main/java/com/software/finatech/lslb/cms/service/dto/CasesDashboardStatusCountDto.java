package com.software.finatech.lslb.cms.service.dto;

public class CasesDashboardStatusCountDto {
    protected long openedCount;
    protected String openedStatusId;
    protected long closedCount;
    protected String closedStatusId;
    protected long pendingCount;
    protected String pendingStatusId;
    protected long totalCount;

    public String getOpenedStatusId() {
        return openedStatusId;
    }

    public void setOpenedStatusId(String openedStatusId) {
        this.openedStatusId = openedStatusId;
    }

    public String getClosedStatusId() {
        return closedStatusId;
    }

    public void setClosedStatusId(String closedStatusId) {
        this.closedStatusId = closedStatusId;
    }

    public String getPendingStatusId() {
        return pendingStatusId;
    }

    public void setPendingStatusId(String pendingStatusId) {
        this.pendingStatusId = pendingStatusId;
    }

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
