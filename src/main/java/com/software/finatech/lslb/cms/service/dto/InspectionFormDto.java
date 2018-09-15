package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.*;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;


public class InspectionFormDto {
    protected InstitutionDto institution;
    protected GameTypeDto gameType;
    protected String comment;
    protected AuthInfoDto user;
    protected AuthRoleDto userRole;
    protected String inspectionDate;
    protected AgentDto agent;
    protected GamingMachineDto gamingMachine;
    protected String id;

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


    public GamingMachineDto getGamingMachine() {
        return gamingMachine;
    }

    public void setGamingMachine(GamingMachineDto gamingMachine) {
        this.gamingMachine = gamingMachine;
    }

    public GameTypeDto getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeDto gameType) {
        this.gameType = gameType;
    }


    public AuthInfoDto getUser() {
        return user;
    }

    public void setUser(AuthInfoDto user) {
        this.user = user;
    }

    public AuthRoleDto getUserRole() {
        return userRole;
    }

    public void setUserRole(AuthRoleDto userRole) {
        this.userRole = userRole;
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