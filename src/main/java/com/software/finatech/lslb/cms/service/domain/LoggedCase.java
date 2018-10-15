package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.LoggedCaseActionDto;
import com.software.finatech.lslb.cms.service.dto.LoggedCaseCommentDto;
import com.software.finatech.lslb.cms.service.dto.LoggedCaseDto;
import com.software.finatech.lslb.cms.service.referencedata.LoggedCaseStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "LoggedCases")
public class LoggedCase extends AbstractFact {
    private String institutionId;
    private String agentId;
    private String caseSubject;
    private String caseDetails;
    private String reporterId;
    private String loggedCaseStatusId;
    private String ticketId;
    private LocalDateTime dateTimeReported;
    private Set<LoggedCaseComment> caseComments = new HashSet<>();
    private Set<LoggedCaseAction> caseActions = new HashSet<>();


    public LocalDateTime getDateTimeReported() {
        return dateTimeReported;
    }

    public void setDateTimeReported(LocalDateTime dateTimeReported) {
        this.dateTimeReported = dateTimeReported;
    }

    public Set<LoggedCaseAction> getCaseActions() {
        return caseActions;
    }

    public void setCaseActions(Set<LoggedCaseAction> caseActions) {
        this.caseActions = caseActions;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Set<LoggedCaseComment> getCaseComments() {
        return caseComments;
    }

    public void setCaseComments(Set<LoggedCaseComment> caseComments) {
        this.caseComments = caseComments;
    }

    public String getLoggedCaseStatusId() {
        return loggedCaseStatusId;
    }

    public void setLoggedCaseStatusId(String loggedCaseStatusId) {
        this.loggedCaseStatusId = loggedCaseStatusId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }


    private AuthInfo getUser(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
    }

    public LoggedCaseStatus getCaseStatus(String caseStatusId) {
        if (StringUtils.isEmpty(this.loggedCaseStatusId)) {
            return null;
        }
        Map caseStatusMap = Mapstore.STORE.get("LoggedCaseStatus");
        LoggedCaseStatus caseStatus = null;
        if (caseStatusMap != null) {
            caseStatus = (LoggedCaseStatus) caseStatusMap.get(caseStatusId);
        }
        if (caseStatus == null) {
            caseStatus = (LoggedCaseStatus) mongoRepositoryReactive.findById(caseStatusId, LoggedCaseStatus.class).block();
            if (caseStatus != null && caseStatusMap != null) {
                caseStatusMap.put(caseStatus, caseStatusMap);
            }
        }
        return caseStatus;
    }

    private Agent getAgent() {
        if (StringUtils.isEmpty(this.agentId)) {
            return null;
        }
        return (Agent) mongoRepositoryReactive.findById(this.agentId, Agent.class).block();
    }

    private Institution getInstitution() {
        if (StringUtils.isEmpty(this.institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(this.institutionId, Institution.class).block();
    }

    public LoggedCaseDto convertToDto() {
        LoggedCaseDto dto = new LoggedCaseDto();
        dto.setId(getId());
        dto.setRepotee(getReportedEntityName());
        dto.setAgentId(getAgentId());
        dto.setInstitutionId(getInstitutionId());
        LocalDateTime reportTime = getDateTimeReported();
        if (reportTime != null) {
            dto.setDateReported(reportTime.toString("dd-MM-yyyy HH:mm:ss"));
        }
        dto.setCaseSubject(getCaseSubject());
        AuthInfo reporter = getUser(this.reporterId);
        if (reporter != null) {
            dto.setReporterId(getReporterId());
            dto.setReporterName(reporter.getFullName());
        }
        LoggedCaseStatus caseStatus = getCaseStatus(this.loggedCaseStatusId);
        if (caseStatus != null) {
            dto.setLoggedCaseStatusId(this.loggedCaseStatusId);
            dto.setLoggedCaseStatusName(caseStatus.getName());
        }
        dto.setTicketId(getTicketId());
        return dto;
    }

    public LoggedCaseDto convertToFullDto() {
        LoggedCaseDto dto = convertToDto();
        dto.setCaseActions(convertCaseActionsToDto(getCaseActions()));
        dto.setCaseComments(convertCaseCommentsToDto(getCaseComments()));
        dto.setCaseDetails(getCaseDetails());
        return dto;
    }


    private Set<LoggedCaseCommentDto> convertCaseCommentsToDto(Set<LoggedCaseComment> caseComments) {
        Set<LoggedCaseCommentDto> caseCommentDtos = new HashSet<>();
        for (LoggedCaseComment caseComment : caseComments) {
            caseCommentDtos.add(covertCaseCommentToDto(caseComment));
        }
        return caseCommentDtos;
    }

    private Set<LoggedCaseActionDto> convertCaseActionsToDto(Set<LoggedCaseAction> caseActions) {
        Set<LoggedCaseActionDto> caseActionDtos = new HashSet<>();
        for (LoggedCaseAction caseAction : caseActions) {
            caseActionDtos.add(convertCaseActionToDto(caseAction));
        }
        return caseActionDtos;
    }

    private LoggedCaseCommentDto covertCaseCommentToDto(LoggedCaseComment caseComment) {
        LoggedCaseCommentDto caseCommentDto = new LoggedCaseCommentDto();
        AuthInfo user = getUser(caseComment.getUserId());
        if (user != null) {
            caseCommentDto.setUserFulName(user.getFullName());
        }
        caseCommentDto.setComment(caseComment.getComment());
        LocalDateTime commentTime = caseComment.getCommentTime();
        if (commentTime != null) {
            caseCommentDto.setCommentTime(commentTime.toString("dd-MM-yyyy HH:mm:ss"));
        }
        return caseCommentDto;
    }

    private LoggedCaseActionDto convertCaseActionToDto(LoggedCaseAction caseAction) {
        LoggedCaseActionDto caseActionDto = new LoggedCaseActionDto();
        LoggedCaseStatus caseStatus = getCaseStatus(caseAction.getLslbCaseStatusId());
        AuthInfo user = getUser(caseAction.getUserId());
        LocalDateTime actionTime = caseAction.getActionTime();
        if (caseStatus != null && user != null && actionTime != null) {
            caseActionDto.setCaseStatusName(caseStatus.getName());
            caseActionDto.setUserName(user.getFullName());
            caseActionDto.setActionTime(actionTime.toString("dd-MM-yyyy HH:mm:ss"));
            String actionString = String.format("%s moved this to %s at %s", user.getFullName(),
                    caseStatus.getName(), actionTime.toString("dd-MM-yyyy HH:mm:ss"));
            caseActionDto.setActionString(actionString);
        }
        return caseActionDto;
    }


    @Override
    public String getFactName() {
        return "LSLBCase";
    }

    public String getReportedEntityName() {
        if (isLoggedAgainstAgent()) {
            return getAgentFullName();
        }
        if (isLoggedAgainstInstitution()) {
            return getInstitutionName();
        }
        return null;
    }

    public String getAgentFullName() {
        Agent agent = getAgent();
        if (agent != null) {
            return agent.getFullName();
        }
        return null;
    }

    public String getInstitutionName() {
        Institution institution = getInstitution();
        if (institution != null) {
            return institution.getInstitutionName();
        }
        return null;
    }

    public String getReporterName() {
        AuthInfo reporter = getUser(this.reporterId);
        if (reporter != null) {
            return reporter.getFullName();
        }
        return null;
    }

    public boolean isLoggedAgainstAgent() {
        return !StringUtils.isEmpty(this.agentId) && StringUtils.isEmpty(this.institutionId);
    }

    public boolean isLoggedAgainstInstitution() {
        return StringUtils.isEmpty(this.agentId) && !StringUtils.isEmpty(this.institutionId);
    }

    public boolean isClosed() {
        return StringUtils.equals(LoggedCaseStatusReferenceData.CLOSED_ID, this.loggedCaseStatusId);
    }
}
