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
    private String gamingMachineId;
    private String pendingGamingMachineId;
    private Set<MachineGameDetails> newMachineGames = new HashSet<>();
    private Set<MachineGameDetails> removedMachineGames = new HashSet<>();
    private String machineApprovalRequestTypeId;

    public String getMachineApprovalRequestTypeId() {
        return machineApprovalRequestTypeId;
    }

    public void setMachineApprovalRequestTypeId(String machineApprovalRequestTypeId) {
        this.machineApprovalRequestTypeId = machineApprovalRequestTypeId;
    }

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
    }

    public String getPendingGamingMachineId() {
        return pendingGamingMachineId;
    }

    public void setPendingGamingMachineId(String pendingGamingMachineId) {
        this.pendingGamingMachineId = pendingGamingMachineId;
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
        Institution institution = getInstitution();
        if (institution != null) {
            dto.setInstitutionId(this.institutionId);
            dto.setInstitutionName(institution.getInstitutionName());
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
        PendingGamingMachine pendingGamingMachine = getPendingGamingMachine();
        if (pendingGamingMachine != null) {
            dto.setPendingGamingMachine(pendingGamingMachine.convertToPendingDto());
        }
        GamingMachine gamingMachine = getGamingMachine();
        if (gamingMachine != null) {
            dto.setPendingGamingMachine(gamingMachine.convertToFullDto());
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

    public PendingGamingMachine getPendingGamingMachine() {
        if (StringUtils.isEmpty(this.pendingGamingMachineId)) {
            return null;
        }
        return (PendingGamingMachine) mongoRepositoryReactive.findById(this.pendingGamingMachineId, PendingGamingMachine.class).block();
    }

    public GamingMachine getGamingMachine() {
        if (StringUtils.isEmpty(this.gamingMachineId)) {
            return null;
        }
        return (GamingMachine) mongoRepositoryReactive.findById(this.gamingMachineId, GamingMachine.class).block();
    }

    @Override
    public String getFactName() {
        return "MachineApprovalRequests";
    }
}
