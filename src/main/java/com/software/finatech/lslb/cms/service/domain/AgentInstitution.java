package com.software.finatech.lslb.cms.service.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgentInstitution {
    private String institutionId;
    private String gameTypeId;
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

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }
}
