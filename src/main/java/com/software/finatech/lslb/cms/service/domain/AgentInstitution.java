package com.software.finatech.lslb.cms.service.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgentInstitution {
    private String institutionId;
    private List<String> gameTypeIds;
    private List<String> businessAddressList = new ArrayList<>();

    public List<String> getBusinessAddressList() {
        return businessAddressList;
    }

    public void setBusinessAddressList(List<String> businessAddressList) {
        this.businessAddressList = businessAddressList;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public List<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(List<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }
}
