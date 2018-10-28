package com.software.finatech.lslb.cms.service.dto;

public class InspectionFormDto {
    protected InstitutionDto institution;
    protected GameTypeDto gameType;
    protected String comment;
    protected String reporter;
    //protected AuthRoleDto userRole;
    protected String inspectionDate;
    protected AgentDto agent;
    protected MachineDto gamingMachine;
    protected String id;
    protected String subject;
    protected String ownerName;

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

    public void setGameType(GameTypeDto gameType) {
        this.gameType = gameType;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }
}
