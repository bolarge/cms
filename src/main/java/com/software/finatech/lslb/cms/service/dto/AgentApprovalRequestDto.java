package com.software.finatech.lslb.cms.service.dto;

import java.util.HashSet;
import java.util.Set;

public class AgentApprovalRequestDto extends AbstractApprovalRequestDto {
    private String agentName;
    private String agentId;
    private String gameTypeId;
    private String gameTypeName;
    private String creationDate;
    private AgentInstitutionDto pendingAgentInstitution;
    private AgentDto agent;
    private Set<String> businessAddressList = new HashSet<>();


    public AgentInstitutionDto getPendingAgentInstitution() {
        return pendingAgentInstitution;
    }

    public void setPendingAgentInstitution(AgentInstitutionDto pendingAgentInstitution) {
        this.pendingAgentInstitution = pendingAgentInstitution;
    }

    public AgentDto getAgent() {
        return agent;
    }

    public void setAgent(AgentDto agent) {
        this.agent = agent;
    }

    public Set<String> getBusinessAddressList() {
        return businessAddressList;
    }

    public void setBusinessAddressList(Set<String> businessAddressList) {
        this.businessAddressList = businessAddressList;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }
}
