package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.LoggedCaseActionDto;
import com.software.finatech.lslb.cms.service.dto.LoggedCaseCommentDto;
import com.software.finatech.lslb.cms.service.dto.LoggedCaseDto;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
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
    private String licenseTypeId;
    private String caseAndComplainCategoryId;
    private String caseAndComplainTypeId;
    private String gamingMachineId;
    private String gamingTerminalId;
    private String otherCategoryName;
    private String otherTypeName;
    private String gameTypeId;
    private String loggedCaseOutcomeId;

    public String getLoggedCaseOutcomeId() {
        return loggedCaseOutcomeId;
    }

    public void setLoggedCaseOutcomeId(String loggedCaseOutcomeId) {
        this.loggedCaseOutcomeId = loggedCaseOutcomeId;
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

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
    }

    public String getGamingTerminalId() {
        return gamingTerminalId;
    }

    public void setGamingTerminalId(String gamingTerminalId) {
        this.gamingTerminalId = gamingTerminalId;
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

    public String getLicenseTypeId() {
        return licenseTypeId;
    }

    public void setLicenseTypeId(String licenseTypeId) {
        this.licenseTypeId = licenseTypeId;
    }

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
        dto.setReportee(getReportedEntityName());
        dto.setAgentId(getAgentId());
        dto.setInstitutionId(getInstitutionId());
        LocalDateTime reportTime = getDateTimeReported();
        if (reportTime != null) {
            dto.setDateReported(reportTime.toString("dd-MM-yyyy HH:mm a"));
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
        CaseAndComplainType type = getCaseAndComplainType();
        if (type != null) {
            dto.setType(String.valueOf(type));
            dto.setTypeId(type.getId());
        }
        CaseAndComplainCategory category = getCaseAndComplainCategory();
        if (category != null) {
            dto.setCategory(String.valueOf(category));
            dto.setCategoryId(category.getId());
        }
        dto.setOtherCategoryName(getOtherCategoryName());
        dto.setOtherTypeName(getOtherTypeName());
        LicenseType licenseType = getLicenseType();
        if (licenseType != null) {
            dto.setLicenseType(licenseType.toString());
            dto.setLicenseTypeId(this.licenseTypeId);
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            dto.setGameTypeId(this.gameTypeId);
            dto.setGameTypeName(gameType.toString());
        }
        return dto;
    }

    public LoggedCaseDto convertToFullDto() {
        LoggedCaseDto dto = convertToDto();
        dto.setCaseActions(convertCaseActionsToDto(getCaseActions()));
        dto.setCaseComments(convertCaseCommentsToDto(getCaseComments()));
        dto.setCaseDetails(getCaseDetails());
        Machine machine = getMachine();
        if (machine != null) {
            dto.setMachineSerialNumber(machine.getSerialNumber());
        }
        LoggedCaseOutcome outcome = getLoggedCaseOutcome();
        if (outcome != null) {
            dto.setOutcomeId(this.loggedCaseOutcomeId);
            dto.setOutcomeName(outcome.getName());
        }
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
            caseCommentDto.setCommentTime(commentTime.toString("dd-MM-yyyy HH:mm a"));
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

    public LicenseType getLicenseType() {
        if (licenseTypeId == null) {
            return null;
        }
        Map licenseTypeMap = Mapstore.STORE.get("LicenseType");

        LicenseType licenseType = null;
        if (licenseTypeMap != null) {
            licenseType = (LicenseType) licenseTypeMap.get(licenseTypeId);
        }
        if (licenseType == null) {
            licenseType = (LicenseType) mongoRepositoryReactive.findById(licenseTypeId, LicenseType.class).block();
            if (licenseType != null && licenseTypeMap != null) {
                licenseTypeMap.put(licenseTypeId, licenseType);
            }
        }
        return licenseType;
    }


    public CaseAndComplainCategory getCaseAndComplainCategory() {
        if (StringUtils.isEmpty(this.caseAndComplainCategoryId)) {
            return null;
        }
        CaseAndComplainCategory category = null;
        Map<String, FactObject> categoryMap = Mapstore.STORE.get("CaseAndComplainCategory");
        if (categoryMap != null) {
            category = (CaseAndComplainCategory) categoryMap.get(this.caseAndComplainCategoryId);
        }
        if (category == null) {
            category = (CaseAndComplainCategory) mongoRepositoryReactive.findById(this.caseAndComplainCategoryId, CaseAndComplainCategory.class).block();
            if (category != null && categoryMap != null) {
                categoryMap.put(this.caseAndComplainCategoryId, category);
            }
        }
        return category;
    }

    public LoggedCaseOutcome getLoggedCaseOutcome() {
        if (StringUtils.isEmpty(this.loggedCaseOutcomeId)) {
            return null;
        }
        LoggedCaseOutcome outcome = null;
        Map<String, FactObject> outcomeMap = Mapstore.STORE.get("LoggedCaseOutcome");
        if (outcomeMap != null) {
            outcome = (LoggedCaseOutcome) outcomeMap.get(this.loggedCaseOutcomeId);
        }
        if (outcome == null) {
            outcome = (LoggedCaseOutcome) mongoRepositoryReactive.findById(this.loggedCaseOutcomeId, LoggedCaseOutcome.class).block();
            if (outcome != null && outcomeMap != null) {
                outcomeMap.put(this.loggedCaseOutcomeId, outcome);
            }
        }
        return outcome;
    }

    public CaseAndComplainType getCaseAndComplainType() {
        if (StringUtils.isEmpty(this.caseAndComplainTypeId)) {
            return null;
        }
        CaseAndComplainType type = null;
        Map<String, FactObject> typeMap = Mapstore.STORE.get("CaseAndComplainType");
        if (typeMap != null) {
            type = (CaseAndComplainType) typeMap.get(this.caseAndComplainTypeId);
        }
        if (type == null) {
            type = (CaseAndComplainType) mongoRepositoryReactive.findById(this.caseAndComplainTypeId, CaseAndComplainType.class).block();
            if (type != null && typeMap != null) {
                typeMap.put(this.caseAndComplainTypeId, type);
            }
        }
        return type;
    }

    public boolean isLoggedAgainstAgent() {
        return StringUtils.equals(LicenseTypeReferenceData.AGENT_ID, this.licenseTypeId);
    }

    public boolean isLoggedAgainstInstitution() {
        return StringUtils.equals(LicenseTypeReferenceData.INSTITUTION_ID, this.licenseTypeId);
    }

    public boolean isLoggedAgainstGamingMachine() {
        return StringUtils.equals(LicenseTypeReferenceData.GAMING_MACHINE_ID, this.licenseTypeId);
    }

    public boolean isLoggedAgainstGamingTerminal() {
        return StringUtils.equals(LicenseTypeReferenceData.GAMING_TERMINAL_ID, this.licenseTypeId);
    }

    public GameType getGameType() {
        if (StringUtils.isEmpty(this.gameTypeId)) {
            return null;
        }
        Map gameTypeMap = Mapstore.STORE.get("GameType");
        GameType gameType = null;
        if (gameTypeMap != null) {
            gameType = (GameType) gameTypeMap.get(gameTypeId);
        }
        if (gameType == null) {
            gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
            if (gameType != null && gameTypeMap != null) {
                gameTypeMap.put(gameTypeId, gameType);
            }
        }
        return gameType;
    }

    public Machine getMachine() {
        String machineId = null;
        if (isLoggedAgainstGamingMachine()) {
            machineId = this.gamingMachineId;
        }
        if (isLoggedAgainstGamingTerminal()) {
            machineId = this.gamingTerminalId;
        }
        if (!StringUtils.isEmpty(machineId)) {
            return (Machine) mongoRepositoryReactive.findById(machineId, Machine.class).block();
        }
        return null;
    }

    public boolean isClosed() {
        return StringUtils.equals(LoggedCaseStatusReferenceData.CLOSED_ID, this.loggedCaseStatusId);
    }
}
