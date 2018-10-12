package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LoggedCaseActionCreateDto {

    @NotEmpty(message = "Please provide logged case status")
    private String caseStatusId;
    @NotEmpty(message = "please provide loggged case id")
    private String caseId;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCaseStatusId() {
        return caseStatusId;
    }

    public void setCaseStatusId(String caseStatusId) {
        this.caseStatusId = caseStatusId;
    }
}
