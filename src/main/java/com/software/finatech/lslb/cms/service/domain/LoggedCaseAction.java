package com.software.finatech.lslb.cms.service.domain;

import org.joda.time.LocalDateTime;

public class LoggedCaseAction {
    private LocalDateTime actionTime;
    private String userId;
    private String lslbCaseStatusId;
    private String lslbCaseOutcomeId;

    public String getLslbCaseOutcomeId() {
        return lslbCaseOutcomeId;
    }

    public void setLslbCaseOutcomeId(String lslbCaseOutcomeId) {
        this.lslbCaseOutcomeId = lslbCaseOutcomeId;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLslbCaseStatusId() {
        return lslbCaseStatusId;
    }

    public void setLslbCaseStatusId(String lslbCaseStatusId) {
        this.lslbCaseStatusId = lslbCaseStatusId;
    }
}
