package com.software.finatech.lslb.cms.service.dto;

import java.util.HashSet;
import java.util.Set;

public class LoggedCaseDto {
    private String id;
    private String ticketId;
    private String reporterId;
    private String reporterName;
    private String agentId;
    private String institutionId;
    private String loggedCaseStatusId;
    private String loggedCaseStatusName;
    private String caseSubject;
    private String caseDetails;
    private String reportee;
    private String dateReported;
    private Set<LoggedCaseActionDto> caseActions = new HashSet<>();
    private Set<LoggedCaseCommentDto> caseComments = new HashSet<>();
    private String typeId;
    private String type;
    private String categoryId;
    private String category;
    private String machineSerialNumber;
    private String machineId;
    private String otherCategoryName;
    private String otherTypeName;
    private String licenseTypeId;
    private String licenseType;
    private String gameTypeId;
    private String gameTypeName;
    private String outcomeId;
    private String outcomeName;
    private String outcomeReason;
    private String caseOriginId;
    private String caseOriginName;
    private String customerComplaintId;
    private String loggedReportId;


    public String getGameTypeId() {
        return gameTypeId;
    }

    public String getCaseOriginId() {
        return caseOriginId;
    }

    public void setCaseOriginId(String caseOriginId) {
        this.caseOriginId = caseOriginId;
    }

    public String getCaseOriginName() {
        return caseOriginName;
    }

    public void setCaseOriginName(String caseOriginName) {
        this.caseOriginName = caseOriginName;
    }

    public String getCustomerComplaintId() {
        return customerComplaintId;
    }

    public void setCustomerComplaintId(String customerComplaintId) {
        this.customerComplaintId = customerComplaintId;
    }

    public String getLoggedReportId() {
        return loggedReportId;
    }

    public void setLoggedReportId(String loggedReportId) {
        this.loggedReportId = loggedReportId;
    }

    public String getOutcomeReason() {
        return outcomeReason;
    }

    public void setOutcomeReason(String outcomeReason) {
        this.outcomeReason = outcomeReason;
    }

    public String getOutcomeId() {
        return outcomeId;
    }

    public void setOutcomeId(String outcomeId) {
        this.outcomeId = outcomeId;
    }

    public String getOutcomeName() {
        return outcomeName;
    }

    public void setOutcomeName(String outcomeName) {
        this.outcomeName = outcomeName;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getLicenseTypeId() {
        return licenseTypeId;
    }

    public void setLicenseTypeId(String licenseTypeId) {
        this.licenseTypeId = licenseTypeId;
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


    public String getMachineSerialNumber() {
        return machineSerialNumber;
    }

    public void setMachineSerialNumber(String machineSerialNumber) {
        this.machineSerialNumber = machineSerialNumber;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getDateReported() {
        return dateReported;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
    }

    public String getReportee() {
        return reportee;
    }

    public void setReportee(String reportee) {
        this.reportee = reportee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getLoggedCaseStatusId() {
        return loggedCaseStatusId;
    }

    public void setLoggedCaseStatusId(String loggedCaseStatusId) {
        this.loggedCaseStatusId = loggedCaseStatusId;
    }

    public String getLoggedCaseStatusName() {
        return loggedCaseStatusName;
    }

    public void setLoggedCaseStatusName(String loggedCaseStatusName) {
        this.loggedCaseStatusName = loggedCaseStatusName;
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

    public Set<LoggedCaseActionDto> getCaseActions() {
        return caseActions;
    }

    public void setCaseActions(Set<LoggedCaseActionDto> caseActions) {
        this.caseActions = caseActions;
    }

    public Set<LoggedCaseCommentDto> getCaseComments() {
        return caseComments;
    }

    public void setCaseComments(Set<LoggedCaseCommentDto> caseComments) {
        this.caseComments = caseComments;
    }
}