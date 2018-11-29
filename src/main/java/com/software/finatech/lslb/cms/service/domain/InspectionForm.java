package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.InspectionFormCommentDto;
import com.software.finatech.lslb.cms.service.dto.InspectionFormDto;
import com.software.finatech.lslb.cms.service.referencedata.InspectionStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
@Document(collection = "InspectionForm")
public class InspectionForm extends AbstractFact {
    protected String institutionId;
    protected String gameTypeId;
    protected ArrayList<InspectionFormComments> inspectionFormComments;
    protected String agentBusinessAddress;
    protected String userId;
    protected LocalDate inspectionDate;
    protected String agentId;
    protected String gamingMachineId;
    protected String subject;
    protected String body;
    protected String status;
    protected String loggedCaseId;


    public String getLoggedCaseId() {
        return loggedCaseId;
    }

    public void setLoggedCaseId(String loggedCaseId) {
        this.loggedCaseId = loggedCaseId;
    }

    @Transient
    protected String ownerName;

    @Transient
    protected String reporter;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAgentBusinessAddress() {
        return agentBusinessAddress;
    }

    public void setAgentBusinessAddress(String agentBusinessAddress) {
        this.agentBusinessAddress = agentBusinessAddress;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
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


    public ArrayList<InspectionFormComments> getInspectionFormComments() {
        return inspectionFormComments;
    }

    public void setInspectionFormComments(ArrayList<InspectionFormComments> inspectionFormComments) {
        this.inspectionFormComments = inspectionFormComments;
    }

    public LocalDate getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDate inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public InspectionFormDto convertToDto(){
        InspectionFormDto inspectionFormDto = new InspectionFormDto();
        ArrayList<InspectionFormComments> inspectionFormComments= (ArrayList<InspectionFormComments>)mongoRepositoryReactive.findAll(new Query(Criteria.where("inspectionFormId").is(getId())), InspectionFormComments.class).toStream().collect(Collectors.toList()) ;
        ArrayList<InspectionFormCommentDto>inspectionFormCommentDtos= new ArrayList<>();
        inspectionFormComments.stream().forEach(inspectionFormComments1 -> {
            inspectionFormCommentDtos.add(inspectionFormComments1.convertToDto());
        });
        inspectionFormDto.setInspectionFormComments(inspectionFormCommentDtos);
        inspectionFormDto.setSubject(getSubject());
        inspectionFormDto.setOwnerName(getOwnerName());
        inspectionFormDto.setBody(getBody());
        InspectionStatus inspectionStatus= (InspectionStatus) mongoRepositoryReactive.findById(getStatus(), InspectionStatus.class).block();
        if(inspectionStatus!=null){
            inspectionFormDto.setStatus(inspectionStatus.getName());
        }
        inspectionFormDto.setAgentBusinessAddress(getAgentBusinessAddress()==null?null:getAgentBusinessAddress());
        Agent agent =(Agent) mongoRepositoryReactive.findById(getAgentId(), Agent.class).block();
        if(agent!=null){
            inspectionFormDto.setAgent(agent.convertToDto());
        }
        Institution institution =(Institution) mongoRepositoryReactive.findById(getInstitutionId(), Institution.class).block();
        if(institution!=null){
            inspectionFormDto.setInstitution(institution.convertToDto());
        }
        Machine gamingMachine =(Machine) mongoRepositoryReactive.findById(getGamingMachineId(), Machine.class).block();
        if(gamingMachine!=null){

            inspectionFormDto.setGamingMachine(gamingMachine.convertToDto());
        }if(!StringUtils.isEmpty(institutionId)){
            try{
                inspectionFormDto.setOwnerName(institution.getInstitutionName());

            }catch (Exception ex){

            }
          }if(!StringUtils.isEmpty(agentId)){
            try{
                inspectionFormDto.setOwnerName(agent.getFullName());

            }catch (Exception ex){

            }
          }
        if(!StringUtils.isEmpty(gamingMachineId)){
            try{
                inspectionFormDto.setOwnerName(gamingMachine.getSerialNumber());

            }catch (Exception ex){

            }
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
        AuthInfo authInfo =(AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
        if(authInfo!=null){
            inspectionFormDto.setReporter(authInfo.getFullName());
        }

        inspectionFormDto.setInspectionDate(getInspectionDate().toString("dd/MM/yyyy"));
        inspectionFormDto.setCreatedAt(getCreatedAt().toString("dd/MM/yyyy"));
        inspectionFormDto.setLoggedCaseId(getLoggedCaseId());

        return inspectionFormDto;

    }


    @Override
    public String getFactName() {
        return "RenewalForm";
    }
}
