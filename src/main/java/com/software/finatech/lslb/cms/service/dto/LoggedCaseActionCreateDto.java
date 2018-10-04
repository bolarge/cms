package com.software.finatech.lslb.cms.service.dto;

public class LoggedCaseActionCreateDto {
    private String userId;
    private String caseStatusId;
    private String caseId;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCaseStatusId() {
        return caseStatusId;
    }

    public void setCaseStatusId(String caseStatusId) {
        this.caseStatusId = caseStatusId;
    }
}
