package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class CaseOutcomeRequest {
    @NotEmpty
    private String caseOutcomeId;
    @NotEmpty
    private String loggedCaseId;
    @NotEmpty
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCaseOutcomeId() {
        return caseOutcomeId;
    }

    public void setCaseOutcomeId(String caseOutcomeId) {
        this.caseOutcomeId = caseOutcomeId;
    }

    public String getLoggedCaseId() {
        return loggedCaseId;
    }

    public void setLoggedCaseId(String loggedCaseId) {
        this.loggedCaseId = loggedCaseId;
    }
}
