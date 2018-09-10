package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.RenewalFormDto;
import com.software.finatech.lslb.cms.service.dto.ReportFormDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "ReportForm")
public class ReportForm extends AbstractFact {
    protected String institutionId;
    protected String gameTypeId;
    protected String comment;
    protected String userId;
    protected LocalDate reportedDate;
    protected String userRoleId;
    protected String agentId;
    protected String gamingMachineId;

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


    public LocalDate getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(LocalDate reportedDate) {
        this.reportedDate = reportedDate;
    }

    public ReportFormDto convertToDto(){
        ReportFormDto reportFormDto = new ReportFormDto();
        reportFormDto.setComment(getComment());
        Agent agent =(Agent) mongoRepositoryReactive.findById(getAgentId(), Agent.class).block();
        if(agent!=null){
            reportFormDto.setAgent(agent.convertToDto());
        }
        Institution institution =(Institution) mongoRepositoryReactive.findById(getInstitutionId(), Institution.class).block();
        if(institution!=null){
            reportFormDto.setInstitution(institution.convertToDto());
        }
        GamingMachine gamingMachine =(GamingMachine) mongoRepositoryReactive.findById(getGamingMachineId(), GamingMachine.class).block();
        if(gamingMachine!=null){
            reportFormDto.setGamingMachine(gamingMachine.convertToDto());
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
            reportFormDto.setGameType(gameType.convertToDto());
        }
        reportFormDto.setId(getId());
        AuthInfo authInfo =(AuthInfo) mongoRepositoryReactive.findById(getUserId(), AuthInfo.class).block();
        if(authInfo!=null){
            reportFormDto.setUser(authInfo.convertToDto());
        }
        AuthRole authRole =(AuthRole) mongoRepositoryReactive.findById(getUserRoleId(), AuthRole.class).block();
        if(authRole!=null){
            reportFormDto.setUserRole(authRole.convertToDto());
        }
        reportFormDto.setReportedDate(getReportedDate().toString("dd/MM/yyyy"));
        return reportFormDto;

    }


    @Override
    public String getFactName() {
        return "RenewalForm";
    }
}
