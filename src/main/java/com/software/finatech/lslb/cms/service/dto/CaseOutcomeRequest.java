package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.referencedata.LoggedCaseOutcomeReferenceData;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;

public class CaseOutcomeRequest {
    @NotEmpty
    private String caseOutcomeId;
    @NotEmpty
    private String loggedCaseId;
    @NotEmpty
    private String reason;

    private CasePenaltyParams casePenaltyParams;

    public CasePenaltyParams getCasePenaltyParams() {
        return casePenaltyParams;
    }

    public void setCasePenaltyParams(CasePenaltyParams casePenaltyParams) {
        this.casePenaltyParams = casePenaltyParams;
    }

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

    public boolean isOutcomeLicenseRevoked() {
        return StringUtils.equals(LoggedCaseOutcomeReferenceData.LICENSE_REVOKED_ID, this.caseOutcomeId);
    }

    public boolean isOutcomeLicenseSuspended() {
        return StringUtils.equals(LoggedCaseOutcomeReferenceData.LICENSE_SUSPENDED_ID, this.caseOutcomeId);
    }

    public boolean isOutcomeLicenseTerminated() {
        return StringUtils.equals(LoggedCaseOutcomeReferenceData.LICENSE_TERMINATED_ID, this.caseOutcomeId);
    }

    public boolean isOutcomePenalty() {
        return StringUtils.equals(LoggedCaseOutcomeReferenceData.PENALTY_ID, this.caseOutcomeId);
    }
}
