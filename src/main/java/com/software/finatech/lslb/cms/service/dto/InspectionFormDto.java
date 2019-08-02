package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.InspectionFormComments;

import java.util.ArrayList;
import java.util.List;

public class InspectionFormDto {
    protected InstitutionDto institution;
    protected GameTypeDto gameType;
    protected ArrayList<InspectionFormCommentDto> inspectionFormComments;
    protected String reporter;
    //protected AuthRoleDto userRole;
    protected String inspectionDate;
    protected String createdAt;
    protected AgentDto agent;
    protected String body;
    protected MachineDto gamingMachine;
    protected String id;
    protected String subject;
    protected String ownerName;
    protected String agentBusinessAddress;
    protected String status;
    private String loggedCaseId;

    public String getLoggedCaseId() {
        return loggedCaseId;
    }

    public void setLoggedCaseId(String loggedCaseId) {
        this.loggedCaseId = loggedCaseId;
    }

    public String getAgentBusinessAddress() {
        return agentBusinessAddress;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAgentBusinessAddress(String agentBusinessAddress) {
        this.agentBusinessAddress = agentBusinessAddress;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public AgentDto getAgent() {
        return agent;
    }

    public void setAgent(AgentDto agent) {
        this.agent = agent;
    }

    public InstitutionDto getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDto institution) {
        this.institution = institution;
    }


    public MachineDto getGamingMachine() {
        return gamingMachine;
    }

    public void setGamingMachine(MachineDto gamingMachine) {
        this.gamingMachine = gamingMachine;
    }

    public GameTypeDto getGameType() {
        return gameType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setGameType(GameTypeDto gameType) {
        this.gameType = gameType;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public ArrayList<InspectionFormCommentDto> getInspectionFormComments() {
        return inspectionFormComments;
    }

    public void setInspectionFormComments(ArrayList<InspectionFormCommentDto> inspectionFormComments) {
        this.inspectionFormComments = inspectionFormComments;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
