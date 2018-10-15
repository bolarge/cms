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