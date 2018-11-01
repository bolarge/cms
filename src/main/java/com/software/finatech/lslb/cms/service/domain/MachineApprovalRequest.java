package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.MachineApprovalRequestDto;
import com.software.finatech.lslb.cms.service.model.MachineGameDetails;
import com.software.finatech.lslb.cms.service.referencedata.MachineApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "MachineApprovalRequests")
public class MachineApprovalRequest extends AbstractApprovalRequest {
    private String machineId;
    private String pendingMachineId;
    private Set<MachineGameDetails> newMachineGames = new HashSet<>();
    private Set<MachineGameDetails> removedMachineGames = new HashSet<>();
    private String machineApprovalRequestTypeId;
    private String agentId;
    private String machineTypeId;
    private boolean initiatedByInstitution;
    private String newMachineStatusId;

    public String getNewMachineStatusId() {
        return newMachineStatusId;
    }

    public void setNewMachineStatusId(String newMachineStatusId) {
        this.newMachineStatusId = newMachineStatusId;
    }

    public boolean isInitiatedByInstitution() {
        return initiatedByInstitution;
    }

    public void setInitiatedByInstitution(boolean initiatedByInstitution) {
        this.initiatedByInstitution = initiatedByInstitution;
    }

    public String getMachineApprovalRequestTypeId() {
        return machineApprovalRequestTypeId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getMachineTypeId() {
        return machineTypeId;
    }

    public void setMachineTypeId(String machineTypeId) {
        this.machineTypeId = machineTypeId;
    }

    public void setMachineApprovalRequestTypeId(String machineApprovalRequestTypeId) {
        this.machineApprovalRequestTypeId = machineApprovalRequestTypeId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getPendingMachineId() {
        return pendingMachineId;
    }

    public void setPendingMachineId(String pendingMachineId) {
        this.pendingMachineId = pendingMachineId;
    }

    public Set<MachineGameDetails> getNewMachineGames() {
        return newMachineGames;
    }

    public void setNewMachineGames(Set<MachineGameDetails> newMachineGames) {
        this.newMachineGames = newMachineGames;
    }

    public Set<MachineGameDetails> getRemovedMachineGames() {
        return removedMachineGames;
    }

    public void setRemovedMachineGames(Set<MachineGameDetails> removedMachineGames) {
        this.removedMachineGames = removedMachineGames;
    }

    public MachineApprovalRequestType getMachineApprovalRequestType() {
        MachineApprovalRequestType machineApprovalRequestType = null;
        Map machineApprovalRequestTypeMap = Mapstore.STORE.get("MachineApprovalRequestType");
        if (machineApprovalRequestType != null) {
            machineApprovalRequestType = (MachineApprovalRequestType) machineApprovalRequestTypeMap.get(this.machineApprovalRequestTypeId);
        }
        if (machineApprovalRequestType == null) {
            machineApprovalRequestType = (MachineApprovalRequestType) mongoRepositoryReactive.findById(this.machineApprovalRequestTypeId, MachineApprovalRequestType.class).block();
            if (machineApprovalRequestType != null && machineApprovalRequestTypeMap != null) {
                machineApprovalRequestTypeMap.put(this.machineApprovalRequestTypeId, machineApprovalRequestType);
            }
        }
        return machineApprovalRequestType;
    }

    public MachineApprovalRequestDto convertToDto() {
        MachineApprovalRequestDto dto = new MachineApprovalRequestDto();
        dto.setId(getId());
        if (isInitiatedByInstitution()) {
            Institution institution = getInstitution();
            if (institution != null) {
                dto.setInitiatorId(this.institutionId);
                dto.setInitiatorName(institution.getInstitutionName());
            }
        } else {
            AuthInfo initiator = getInitiator();
            if (initiator != null) {
                dto.setInitiatorName(initiator.getFullName());
            }
        }
        MachineApprovalRequestType approvalRequestType = getMachineApprovalRequestType();
        if (approvalRequestType != null) {
            dto.setRequestTypeName(approvalRequestType.getName());
            dto.setRequestTypeId(this.machineApprovalRequestTypeId);
        }
        ApprovalRequestStatus approvalRequestStatus = getApprovalRequestStatus();
        if (approvalRequestStatus != null) {
            dto.setRequestStatusId(this.approvalRequestStatusId);
            dto.setRequestStatusName(approvalRequestStatus.toString());
        }
        LocalDateTime dateCreated = getDateCreated();
        if (dateCreated != null) {
            dto.setDateCreated(dateCreated.toString("dd-MM-yyyy HH:mm:ss a"));
        }
        return dto;
    }

    public MachineApprovalRequestDto convertToFullDto() {
        MachineApprovalRequestDto dto = convertToDto();
        PendingMachine pendingGamingMachine = getPendingMachine();
        if (pendingGamingMachine != null) {
            dto.setPendingMachine(pendingGamingMachine.convertToPendingDto());
        }
        Machine gamingMachine = getMachine();
        if (gamingMachine != null) {
            dto.setPendingMachine(gamingMachine.convertToFullDto());
        }
        dto.setNewGameDetails(getNewMachineGames());
        AuthInfo approver = getAuthInfo(this.approverId);
        if (approver != null) {
            dto.setApproverId(this.approverId);
            dto.setApproverName(approver.getFullName());
        }

        AuthInfo rejector = getAuthInfo(this.rejectorId);
        if (rejector != null) {
            dto.setRejectorId(this.rejectorId);
            dto.setRejectorName(rejector.getFullName());
        }
        dto.setNewMachineStatusName(getNewMachineStatusName());
        return dto;
    }

    public boolean isCreateGamingMachine() {
        return StringUtils.equals(MachineApprovalRequestTypeReferenceData.CREATE_GAMING_MACHINE_ID, this.machineApprovalRequestTypeId);
    }

    public boolean isCreateGamingTerminal() {
        return StringUtils.equals(MachineApprovalRequestTypeReferenceData.CREATE_GAMING_TERMINAL_ID, this.machineApprovalRequestTypeId);
    }

    public boolean isAddGamesToGamingMachine() {
        return StringUtils.equals(MachineApprovalRequestTypeReferenceData.ADD_GAMES_TO_GAMING_MACHINE_ID, this.machineApprovalRequestTypeId);
    }

    public boolean isAddGamesToGamingTerminal() {
        return StringUtils.equals(MachineApprovalRequestTypeReferenceData.ADD_GAMES_TO_GAMING_TERMINAL_ID, this.machineApprovalRequestTypeId);
    }

    public boolean isChangeGamingMachineStatus() {
        return StringUtils.equals(MachineApprovalRequestTypeReferenceData.CHANGE_GAMING_MACHINE_STATUS, this.machineApprovalRequestTypeId);
    }

    public boolean isChangeGamingTerminalStatus() {
        return StringUtils.equals(MachineApprovalRequestTypeReferenceData.CHANGE_GAMING_TERMINAL_STATUS, this.machineApprovalRequestTypeId);
    }

    public boolean isAssignTerminalToAgent() {
        return StringUtils.equals(MachineApprovalRequestTypeReferenceData.ASSIGN_TERMINAL_TO_AGENT, this.machineApprovalRequestTypeId);
    }


    public PendingMachine getPendingMachine() {
        if (StringUtils.isEmpty(this.pendingMachineId)) {
            return null;
        }
        return (PendingMachine) mongoRepositoryReactive.findById(this.pendingMachineId, PendingMachine.class).block();
    }

    public Machine getMachine() {
        if (StringUtils.isEmpty(this.machineId)) {
            return null;
        }
        return (Machine) mongoRepositoryReactive.findById(this.machineId, Machine.class).block();
    }

    @Override
    public String getFactName() {
        return "MachineApprovalRequests";
    }

    public MachineStatus getNewMachineStatus() {
        if (StringUtils.isEmpty(this.newMachineStatusId)) {
            return null;
        }
        Map machineStatusMap = Mapstore.STORE.get("MachineStatus");
        MachineStatus machineStatus = null;
        if (machineStatus != null) {
            machineStatus = (MachineStatus) machineStatusMap.get(this.newMachineStatusId);
        }
        if (machineStatus == null) {
            machineStatus = (MachineStatus) mongoRepositoryReactive.findById(this.newMachineStatusId, MachineStatus.class).block();
            if (machineStatus != null && machineStatusMap != null) {
                machineStatusMap.put(this.machineTypeId, machineStatus);
            }
        }
        return machineStatus;
    }

    private String getNewMachineStatusName() {
        MachineStatus newStatus = getNewMachineStatus();
        if (newStatus != null) {
            return newStatus.toString();
        }
        return null;
    }

    public String getRequestInitiatorName() {
        if (isInitiatedByInstitution()) {
            Institution institution = getInstitution();
            if (institution != null) {
                return institution.getInstitutionName();
            }
        } else {
            AuthInfo initiator = getInitiator();
            if (initiator != null) {
                return initiator.getFullName();
            }
        }
        return null;
    }

    public String getMachineRequestSerialNumber() {
        PendingMachine pendingMachine = getPendingMachine();
        if (pendingMachine != null) {
            return pendingMachine.getSerialNumber();
        }
        Machine machine = getMachine();
        if (machine != null) {
            return machine.getSerialNumber();
        }
        return null;
    }

}
