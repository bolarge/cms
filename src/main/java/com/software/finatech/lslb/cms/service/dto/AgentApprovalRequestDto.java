package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

public class AgentApprovalRequestDto {
    private String agentName;
    private String agentId;
    private String institutionName;
    private String institutionId;
    private String gameTypeId;
    private String gameTypeName;
    private String requestTypeName;
    private String requestTypeId;
    private String requestStatusName;
    private String requestStatusId;
    private String creationDate;
    private String id;
    private String approverId;
    private String approverName;
    private String rejectorId;
    private String rejectorName;
    private AgentInstitutionDto pendingAgentInstitution;
    private AgentDto agent;
    private List<String> businessAddressList = new ArrayList<>();


    public AgentInstitutionDto getPendingAgentInstitution() {
        return pendingAgentInstitution;
    }

    public void setPendingAgentInstitution(AgentInstitutionDto pendingAgentInstitution) {
        this.pendingAgentInstitution = pendingAgentInstitution;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getRejectorId() {
        return rejectorId;
    }

    public void setRejectorId(String rejectorId) {
        this.rejectorId = rejectorId;
    }

    public String getRejectorName() {
        return rejectorName;
    }

    public void setRejectorName(String rejectorName) {
        this.rejectorName = rejectorName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AgentDto getAgent() {
        return agent;
    }

    public void setAgent(AgentDto agent) {
        this.agent = agent;
    }

    public List<String> getBusinessAddressList() {
        return businessAddressList;
    }

    public void setBusinessAddressList(List<String> businessAddressList) {
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

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
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

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getRequestTypeName() {
        return requestTypeName;
    }

    public void setRequestTypeName(String requestTypeName) {
        this.requestTypeName = requestTypeName;
    }

    public String getRequestTypeId() {
        return requestTypeId;
    }

    public void setRequestTypeId(String requestTypeId) {
        this.requestTypeId = requestTypeId;
    }

    public String getRequestStatusName() {
        return requestStatusName;
    }

    public void setRequestStatusName(String requestStatusName) {
        this.requestStatusName = requestStatusName;
    }

    public String getRequestStatusId() {
        return requestStatusId;
    }

    public void setRequestStatusId(String requestStatusId) {
        this.requestStatusId = requestStatusId;
    }
}
