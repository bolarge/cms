package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgentInstitutionCreateDto {
    @Email
    @NotEmpty(message = "Please provide agent email address")
    private String agentEmailAddress;
    @NotEmpty(message = "Please provide institution id")
    private String institutionId;
    @NotEmpty(message = "Please provide game type id")
    private String gameTypeId;
    @NotEmpty(message = "Please provide business address list")
    private Set<String> businessAddressList = new HashSet<>();

    public String getAgentEmailAddress() {
        return agentEmailAddress;
    }

    public void setAgentEmailAddress(String agentEmailAddress) {
        this.agentEmailAddress = agentEmailAddress;
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

    public Set<String> getBusinessAddressList() {
        return businessAddressList;
    }

    public void setBusinessAddressList(Set<String> businessAddressList) {
        this.businessAddressList = businessAddressList;
    }
}
