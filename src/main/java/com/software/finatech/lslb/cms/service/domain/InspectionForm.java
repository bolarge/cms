package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.InspectionFormDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "InspectionForm")
public class InspectionForm extends AbstractFact {
    protected String institutionId;
    protected String gameTypeId;
    protected String comment;
    protected String userId;
    protected LocalDate inspectionDate;
    protected String userRoleId;
    protected String agentId;
    protected String gamingMachineId;
    protected String subject;
    protected String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public LocalDate getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDate inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public InspectionFormDto convertToDto(){
        InspectionFormDto inspectionFormDto = new InspectionFormDto();
        inspectionFormDto.setComment(getComment());
        inspectionFormDto.setSubject(getSubject());
        Agent agent =(Agent) mongoRepositoryReactive.findById(getAgentId(), Agent.class).block();
        if(agent!=null){
            inspectionFormDto.setAgent(agent.convertToDto());
        }
        Institution institution =(Institution) mongoRepositoryReactive.findById(getInstitutionId(), Institution.class).block();
        if(institution!=null){
            inspectionFormDto.setInstitution(institution.convertToDto());
        }
        GamingMachine gamingMachine =(GamingMachine) mongoRepositoryReactive.findById(getGamingMachineId(), GamingMachine.class).block();
        if(gamingMachine!=null){
            inspectionFormDto.setGamingMachine(gamingMachine.convertToDto());
        }if(!StringUtils.isEmpty(institutionId)){
            this.owner=institution.getInstitutionName();

        }if(!StringUtils.isEmpty(agentId)){
            this.owner=agent.getFullName();
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
        if (gameType != null) {
            inspectionFormDto.setGameType(gameType.convertToDto());
        }
        inspectionFormDto.setId(getId());
        AuthInfo authInfo =(AuthInfo) mongoRepositoryReactive.findById(getUserId(), AuthInfo.class).block();
        if(authInfo!=null){
            inspectionFormDto.setUser(authInfo.convertToDto());
        }
        AuthRole authRole =(AuthRole) mongoRepositoryReactive.findById(getUserRoleId(), AuthRole.class).block();
        if(authRole!=null){
            inspectionFormDto.setUserRole(authRole.convertToDto());
        }
        inspectionFormDto.setInspectionDate(getInspectionDate().toString("dd/MM/yyyy"));
        return inspectionFormDto;

    }


    @Override
    public String getFactName() {
        return "RenewalForm";
    }
}
