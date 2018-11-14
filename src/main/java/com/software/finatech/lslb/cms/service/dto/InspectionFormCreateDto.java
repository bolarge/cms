package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;


public class InspectionFormCreateDto {
    protected String institutionId;
    protected String gameTypeId;
    @NotEmpty(message = "Provide User ID")
    protected String userId;
    @NotEmpty(message = "Provide Body Message")
    protected String body;
    @NotEmpty(message = "Provide reportDate")
    protected String inspectionDate;
    @NotEmpty(message = "Provide User role")
    protected String userRoleId;
    protected String agentId;
    protected String gamingMachineId;
    protected String agentBusinessAddress;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @NotEmpty(message = "Provide User role")


    protected String subject;

    public String getAgentBusinessAddress() {
        return agentBusinessAddress;
    }

    public void setAgentBusinessAddress(String agentBusinessAddress) {
        this.agentBusinessAddress = agentBusinessAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }


    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }


}
