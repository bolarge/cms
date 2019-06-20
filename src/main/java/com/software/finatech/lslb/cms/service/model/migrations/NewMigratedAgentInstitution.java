package com.software.finatech.lslb.cms.service.model.migrations;

import java.util.HashSet;
import java.util.Set;

/**
 * @author adeyi.adebolu
 * created on 27/05/2019
 */
public class NewMigratedAgentInstitution {
    private String institutionId;
    private Set<String> businessAddress = new HashSet<>();
    private Set<String> gameTypeIds = new HashSet<>();

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public Set<String> getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(Set<String> businessAddress) {
        this.businessAddress = businessAddress;
    }

    public Set<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(Set<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }
}
