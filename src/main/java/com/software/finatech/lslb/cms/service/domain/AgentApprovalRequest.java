package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.AgentApprovalRequestDto;
import com.software.finatech.lslb.cms.service.referencedata.AgentApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.adapters.AgentInstitutionAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")
@Document(collection = "AgentApprovalRequests")
public class AgentApprovalRequest extends AbstractFact {
    private String agentId;
    private String institutionId;
    private String agentApprovalRequestTypeId;
    private String gameTypeId;
    private List<String> businessAddressList = new ArrayList<>();
    private String approverId;
    private String approvalRequestStatusId;
    private String rejectorId;
    private String rejectionReason;

    public void setBusinessAddressList(List<String> businessAddressList) {
        this.businessAddressList = businessAddressList;
    }


    public List<String> getBusinessAddressList() {
        return businessAddressList;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectorId() {
        return rejectorId;
    }

    public void setRejectorId(String rejectorId) {
        this.rejectorId = rejectorId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
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

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getApprovalRequestStatusId() {
        return approvalRequestStatusId;
    }

    public void setApprovalRequestStatusId(String approvalRequestStatusId) {
        this.approvalRequestStatusId = approvalRequestStatusId;
    }

    public boolean isApprovedRequest() {
        return StringUtils.equals(ApprovalRequestStatusReferenceData.APPROVED_ID, this.approvalRequestStatusId);
    }

    public boolean isRejectedRequest() {
        return StringUtils.equals(ApprovalRequestStatusReferenceData.REJECTED_ID, this.approvalRequestStatusId);
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

    private ApprovalRequestStatus getApprovalRequestStatus() {
        if (StringUtils.isEmpty(this.approvalRequestStatusId)) {
            return null;
        }
        Map approvalRequestStatusMap = Mapstore.STORE.get("ApprovalRequestStatus");
        ApprovalRequestStatus approvalRequestStatus = null;
        if (approvalRequestStatusMap != null) {
            approvalRequestStatus = (ApprovalRequestStatus) approvalRequestStatusMap.get(this.approvalRequestStatusId);
        }
        if (approvalRequestStatus == null) {
            approvalRequestStatus = (ApprovalRequestStatus) mongoRepositoryReactive.findById(this.approvalRequestStatusId, ApprovalRequestStatus.class).block();
            if (approvalRequestStatus != null && approvalRequestStatusMap != null) {
                approvalRequestStatusMap.put(this.approvalRequestStatusId, approvalRequestStatus);
            }
        }
        return approvalRequestStatus;
    }

    public String getGameTypeName() {
        GameType gameType = getGameType();
        if (gameType == null) {
            return null;
        } else {
            return gameType.getName();
        }
    }

    public Institution getInstitution() {
        if (StringUtils.isEmpty(institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public Agent getAgent() {
        if (StringUtils.isEmpty(this.agentId)) {
            return null;
        }
        return (Agent) mongoRepositoryReactive.findById(this.agentId, Agent.class).block();
    }

    public String getInstitutionName() {
        Institution institution = getInstitution();
        if (institution != null) {
            return institution.getInstitutionName();
        }
        return "";
    }

    private AuthInfo getUser(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
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

        AuthInfo approver = getUser(this.approverId);
        if (approver != null) {
            agentApprovalRequestDto.setApproverId(getApproverId());
            agentApprovalRequestDto.setApproverName(approver.getFullName());
        }

        AuthInfo rejector = getUser(this.rejectorId);
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
