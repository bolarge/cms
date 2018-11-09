package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.AgentApprovalRequestDto;
import com.software.finatech.lslb.cms.service.referencedata.AgentApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.adapters.AgentInstitutionAdapter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")
@Document(collection = "AgentApprovalRequests")
public class AgentApprovalRequest extends AbstractApprovalRequest {
    private String agentId;
    private String agentApprovalRequestTypeId;
    private String gameTypeId;
    private List<String> businessAddressList = new ArrayList<>();
    private String pendingAgentId;

    public String getPendingAgentId() {
        return pendingAgentId;
    }

    public void setPendingAgentId(String pendingAgentId) {
        this.pendingAgentId = pendingAgentId;
    }

    public void setBusinessAddressList(List<String> businessAddressList) {
        this.businessAddressList = businessAddressList;
    }

    public List<String> getBusinessAddressList() {
        return businessAddressList;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentApprovalRequestTypeId() {
        return agentApprovalRequestTypeId;
    }

    public void setAgentApprovalRequestTypeId(String agentApprovalRequestTypeId) {
        this.agentApprovalRequestTypeId = agentApprovalRequestTypeId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    private GameType getGameType() {
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


    private AgentApprovalRequestType getAgentApprovalRequestType() {
        if (StringUtils.isEmpty(this.agentApprovalRequestTypeId)) {
            return null;
        }
        Map agentApprovalRequestTypeMap = Mapstore.STORE.get("AgentApprovalRequestType");
        AgentApprovalRequestType agentApprovalRequestType = null;
        if (agentApprovalRequestTypeMap != null) {
            agentApprovalRequestType = (AgentApprovalRequestType) agentApprovalRequestTypeMap.get(this.agentApprovalRequestTypeId);
        }
        if (agentApprovalRequestType == null) {
            agentApprovalRequestType = (AgentApprovalRequestType) mongoRepositoryReactive.findById(this.agentApprovalRequestTypeId, AgentApprovalRequestType.class).block();
            if (agentApprovalRequestType != null && agentApprovalRequestTypeMap != null) {
                agentApprovalRequestTypeMap.put(this.agentApprovalRequestTypeId, agentApprovalRequestType);
            }
        }
        return agentApprovalRequestType;
    }

    public String getAgentApprovalRequestTypeName() {
        AgentApprovalRequestType agentApprovalRequestType = getAgentApprovalRequestType();
        if (agentApprovalRequestType == null) {
            return null;
        }
        return agentApprovalRequestType.getName();
    }

    public String getGameTypeName() {
        GameType gameType = getGameType();
        if (gameType == null) {
            return null;
        } else {
            return gameType.getName();
        }
    }
    public Agent getAgent() {
        if (StringUtils.isEmpty(this.agentId)) {
            return null;
        }
        return (Agent) mongoRepositoryReactive.findById(this.agentId, Agent.class).block();
    }
    private PendingAgent getPendingAgent() {
        if (StringUtils.isEmpty(this.pendingAgentId)) {
            return null;
        }
        return (PendingAgent) mongoRepositoryReactive.findById(this.pendingAgentId, PendingAgent.class).block();
    }

    public boolean isAgentCreationRequest() {
        return StringUtils.equals(AgentApprovalRequestTypeReferenceData.CREATE_AGENT_ID, this.agentApprovalRequestTypeId);
    }

    public boolean isInstitutionAgentAdditionRequest() {
        return StringUtils.equals(AgentApprovalRequestTypeReferenceData.ADD_INSTITUTION_TO_AGENT_ID, this.agentApprovalRequestTypeId);
    }


    public AgentApprovalRequestDto convertToDto() {
        AgentApprovalRequestDto agentApprovalRequestDto = new AgentApprovalRequestDto();
        agentApprovalRequestDto.setId(getId());
        Institution institution = getInstitution();
        if (institution != null) {
            agentApprovalRequestDto.setInstitutionId(getInstitutionId());
            agentApprovalRequestDto.setInstitutionName(institution.getInstitutionName());
        }
        Agent agent = getAgent();
        if (agent != null) {
            agentApprovalRequestDto.setAgentId(getAgentId());
            agentApprovalRequestDto.setAgentName(agent.getFullName());
        }
        PendingAgent pendingAgent = getPendingAgent();
        if (pendingAgent != null){
            agentApprovalRequestDto.setAgentName(pendingAgent.getFullName());
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            agentApprovalRequestDto.setGameTypeId(getGameTypeId());
            agentApprovalRequestDto.setGameTypeName(gameType.getName());
        }
        ApprovalRequestStatus approvalRequestStatus = getApprovalRequestStatus();
        if (approvalRequestStatus != null) {
            agentApprovalRequestDto.setRequestStatusId(getApprovalRequestStatusId());
            agentApprovalRequestDto.setRequestStatusName(approvalRequestStatus.getName());
        }

        AgentApprovalRequestType agentApprovalRequestType = getAgentApprovalRequestType();
        if (agentApprovalRequestType != null) {
            agentApprovalRequestDto.setRequestTypeId(getAgentApprovalRequestTypeId());
            agentApprovalRequestDto.setRequestTypeName(agentApprovalRequestType.getName());
        }
        LocalDateTime dateCreated = getDateCreated();
        if (dateCreated != null) {
            agentApprovalRequestDto.setDateCreated(dateCreated.toString("dd-MM-yyyy HH:mm:ss a"));
        }
        return agentApprovalRequestDto;
    }


    public AgentApprovalRequestDto convertToDtoFullDetail() {
        AgentApprovalRequestDto agentApprovalRequestDto = new AgentApprovalRequestDto();
        agentApprovalRequestDto.setId(getId());
        Institution institution = getInstitution();
        if (institution != null) {
            agentApprovalRequestDto.setInstitutionId(getInstitutionId());
            agentApprovalRequestDto.setInstitutionName(institution.getInstitutionName());
        }
        Agent agent = getAgent();
        if (agent != null) {
            agentApprovalRequestDto.setAgentId(getAgentId());
            agentApprovalRequestDto.setAgentName(agent.getFullName());
            agentApprovalRequestDto.setAgent(agent.convertToFullDetailDto());
        }
        PendingAgent pendingAgent = getPendingAgent();
        if (pendingAgent != null){
            agentApprovalRequestDto.setAgent(pendingAgent.convertToFullDetailDto());
            agentApprovalRequestDto.setAgentName(pendingAgent.getFullName());
        }

        GameType gameType = getGameType();
        if (gameType != null) {
            agentApprovalRequestDto.setGameTypeId(getGameTypeId());
            agentApprovalRequestDto.setGameTypeName(gameType.getName());
        }
        ApprovalRequestStatus approvalRequestStatus = getApprovalRequestStatus();
        if (approvalRequestStatus != null) {
            agentApprovalRequestDto.setRequestStatusId(getApprovalRequestStatusId());
            agentApprovalRequestDto.setRequestStatusName(approvalRequestStatus.getName());
        }

        AgentApprovalRequestType agentApprovalRequestType = getAgentApprovalRequestType();
        if (agentApprovalRequestType != null) {
            agentApprovalRequestDto.setRequestTypeId(getAgentApprovalRequestTypeId());
            agentApprovalRequestDto.setRequestTypeName(agentApprovalRequestType.getName());
        }
        agentApprovalRequestDto.setCreationDate(getCreated() != null ? getCreated().toString() : "");
        agentApprovalRequestDto.setBusinessAddressList(getBusinessAddressList());

        AuthInfo approver = getAuthInfo(this.approverId);
        if (approver != null) {
            agentApprovalRequestDto.setApproverId(getApproverId());
            agentApprovalRequestDto.setApproverName(approver.getFullName());
        }

        AuthInfo rejector = getAuthInfo(this.rejectorId);
        if (rejector != null) {
            agentApprovalRequestDto.setRejectorId(getRejectorId());
            agentApprovalRequestDto.setRejectorName(rejector.getFullName());
        }

        if (isInstitutionAgentAdditionRequest()) {
            AgentInstitution agentInstitution = new AgentInstitution();
            agentInstitution.setInstitutionId(getInstitutionId());
            agentInstitution.setGameTypeId(getGameTypeId());
            agentInstitution.setBusinessAddressList(getBusinessAddressList());
            agentApprovalRequestDto.setPendingAgentInstitution(AgentInstitutionAdapter.convertAgentInstitutionToDto(agentInstitution, mongoRepositoryReactive));
        }
        return agentApprovalRequestDto;
    }

    @Override
    public String getFactName() {
        return "AgentApprovalRequest";
    }
}
