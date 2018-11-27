package com.software.finatech.lslb.cms.service.dto;

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
    @NotEmpty(message = "please provide license type")
    private String licenseTypeId;
    @NotEmpty(message = "please provide case/complain category")
    private String caseAndComplainCategoryId;
    @NotEmpty(message = "please provide case/complain type")
    private String caseAndComplainTypeId;
    private String gamingTerminalId;
    private String gamingMachineId;
    private String otherCategoryName;
    private String otherTypeName;
    private String gameTypeId;
    private String caseOriginId;
    private String loggedReportId;
    private String customerComplaintId;


    public String getLoggedReportId() {
        return loggedReportId;
    }

    public void setLoggedReportId(String loggedReportId) {
        this.loggedReportId = loggedReportId;
    }

    public String getCustomerComplaintId() {
        return customerComplaintId;
    }

    public void setCustomerComplaintId(String customerComplaintId) {
        this.customerComplaintId = customerComplaintId;
    }

    public String getCaseOriginId() {
        return caseOriginId;
    }

    public void setCaseOriginId(String caseOriginId) {
        this.caseOriginId = caseOriginId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getOtherCategoryName() {
        return otherCategoryName;
    }

    public void setOtherCategoryName(String otherCategoryName) {
        this.otherCategoryName = otherCategoryName;
    }

    public String getOtherTypeName() {
        return otherTypeName;
    }

    public void setOtherTypeName(String otherTypeName) {
        this.otherTypeName = otherTypeName;
    }

    public String getGamingTerminalId() {
        return gamingTerminalId;
    }

    public void setGamingTerminalId(String gamingTerminalId) {
        this.gamingTerminalId = gamingTerminalId;
    }

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
    }

    public String getLicenseTypeId() {
        return licenseTypeId;
    }

    public void setLicenseTypeId(String licenseTypeId) {
        this.licenseTypeId = licenseTypeId;
    }

    public String getCaseAndComplainCategoryId() {
        return caseAndComplainCategoryId;
    }

    public void setCaseAndComplainCategoryId(String caseAndComplainCategoryId) {
        this.caseAndComplainCategoryId = caseAndComplainCategoryId;
    }

    public String getCaseAndComplainTypeId() {
        return caseAndComplainTypeId;
    }

    public void setCaseAndComplainTypeId(String caseAndComplainTypeId) {
        this.caseAndComplainTypeId = caseAndComplainTypeId;
    }

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
}
