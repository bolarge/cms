package com.software.finatech.lslb.cms.service.dto;

public class DashboardSummaryDto {
    protected long institutionTotalCount;
    protected long agentTotalCount;
    protected long gamingMachineTotalCount;
    protected long casesTotalCount;
    protected long gamingTerminalTotalCount;


    public long getGamingTerminalTotalCount() {
        return gamingTerminalTotalCount;
    }

    public void setGamingTerminalTotalCount(long gamingTerminalTotalCount) {
        this.gamingTerminalTotalCount = gamingTerminalTotalCount;
    }

    public long getInstitutionTotalCount() {
        return institutionTotalCount;
    }

    public void setInstitutionTotalCount(long institutionTotalCount) {
        this.institutionTotalCount = institutionTotalCount;
    }

    public long getAgentTotalCount() {
        return agentTotalCount;
    }

    public void setAgentTotalCount(long agentTotalCount) {
        this.agentTotalCount = agentTotalCount;
    }

    public long getGamingMachineTotalCount() {
        return gamingMachineTotalCount;
    }

    public void setGamingMachineTotalCount(long gamingMachineTotalCount) {
        this.gamingMachineTotalCount = gamingMachineTotalCount;
    }

    public long getCasesTotalCount() {
        return casesTotalCount;
    }

    public void setCasesTotalCount(long casesTotalCount) {
        this.casesTotalCount = casesTotalCount;
    }
}
