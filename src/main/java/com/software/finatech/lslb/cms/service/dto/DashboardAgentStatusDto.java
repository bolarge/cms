package com.software.finatech.lslb.cms.service.dto;

public class DashboardAgentStatusDto {

    protected String agentStatusId;
    protected long agentStatusCount;

    public String getAgentStatusId() {
        return agentStatusId;
    }

    public void setAgentStatusId(String agentStatusId) {
        this.agentStatusId = agentStatusId;
    }

    public long getAgentStatusCount() {
        return agentStatusCount;
    }

    public void setAgentStatusCount(long agentStatusCount) {
        this.agentStatusCount = agentStatusCount;
    }
}
