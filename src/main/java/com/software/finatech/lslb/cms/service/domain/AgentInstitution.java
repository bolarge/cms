package com.software.finatech.lslb.cms.service.domain;

import java.util.HashSet;
import java.util.Set;

public class AgentInstitution {
    private String institutionId;
    private Set<String> gameTypeIds = new HashSet<>();
    private Set<String> businessAddressList = new HashSet<>();


    public Set<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(Set<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }

    public Set<String> getBusinessAddressList() {
        return businessAddressList;
    }

    public void setBusinessAddressList(Set<String> businessAddressList) {
        this.businessAddressList = businessAddressList;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }
}
