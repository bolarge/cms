package com.software.finatech.lslb.cms.service.dto;

public class DashboardLoggedCaseStatusDto {

    protected String loggedCaseStatusId;
    protected long loggedStatusCount;

    public String getLoggedCaseStatusId() {
        return loggedCaseStatusId;
    }

    public void setLoggedCaseStatusId(String loggedCaseStatusId) {
        this.loggedCaseStatusId = loggedCaseStatusId;
    }

    public long getLoggedStatusCount() {
        return loggedStatusCount;
    }

    public void setLoggedStatusCount(long loggedStatusCount) {
        this.loggedStatusCount = loggedStatusCount;
    }
}
