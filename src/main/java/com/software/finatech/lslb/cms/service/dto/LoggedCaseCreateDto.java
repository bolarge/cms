package com.software.finatech.lslb.cms.service.dto;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;

public class LoggedCaseCreateDto {
    private String agentId;
    private String institutionId;
    @NotEmpty(message = "Please provide user id")
    private String userId;
    @NotEmpty(message = "Please provide case subject")
    private String caseSubject;
    @NotEmpty(message = "please provide case details")
    private String caseDetails;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCaseSubject() {
        return caseSubject;
    }

    public void setCaseSubject(String caseSubject) {
        this.caseSubject = caseSubject;
    }

    public String getCaseDetails() {
        return caseDetails;
    }

    public void setCaseDetails(String caseDetails) {
        this.caseDetails = caseDetails;
    }

    public boolean isValid() {
        return (StringUtils.isEmpty(this.agentId) && !StringUtils.isEmpty(this.institutionId)) ||
                (StringUtils.isEmpty(this.institutionId) && !StringUtils.isEmpty(this.agentId));
    }
}
