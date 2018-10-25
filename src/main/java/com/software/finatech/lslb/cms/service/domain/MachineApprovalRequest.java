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

    public String getMachineApprovalRequestTypeId() {
        return machineApprovalRequestTypeId;
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
        return dto;
    }

    public boolean isCreateMachine() {
        return StringUtils.equals(MachineApprovalRequestTypeReferenceData.CREATE_MACHINE_ID, this.machineApprovalRequestTypeId);
    }

    public boolean isAddGamesToMachine() {
        return StringUtils.equals(MachineApprovalRequestTypeReferenceData.ADD_GAMES_TO_MACHINE_ID, this.machineApprovalRequestTypeId);
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
}
