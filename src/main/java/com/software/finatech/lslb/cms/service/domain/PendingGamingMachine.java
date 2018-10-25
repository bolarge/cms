package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.GamingMachineDto;
import com.software.finatech.lslb.cms.service.model.MachineGameDetails;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "GamingMachines")
public class PendingGamingMachine extends GamingMachine {
    private String approvalRequestStatusId = ApprovalRequestStatusReferenceData.PENDING_ID;
    private Set<MachineGameDetails> gameDetailsList = new HashSet<>();

    public Set<MachineGameDetails> getGameDetailsList() {
        return gameDetailsList;
    }

    public void setGameDetailsList(Set<MachineGameDetails> gameDetailsList) {
        this.gameDetailsList = gameDetailsList;
    }

    public String getApprovalRequestStatusId() {
        return approvalRequestStatusId;
    }

    public void setApprovalRequestStatusId(String approvalRequestStatusId) {
        this.approvalRequestStatusId = approvalRequestStatusId;
    }

    public GamingMachineDto convertToPendingDto() {
        GamingMachineDto dto = convertToDto();
        dto.setMachineGames(new ArrayList<>(getGameDetailsList()));
        return dto;
    }
}
