package com.software.finatech.lslb.cms.service.dto;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgentInstitutionDto {
    private String institutionName;
    private String institutionId;
    private List<EnumeratedFactDto> gameTypes = new ArrayList<>();
    private Set<String> businessAddressList = new HashSet<>();

    public Set<String> getBusinessAddressList() {
        return businessAddressList;
    }

    public void setBusinessAddressList(Set<String> businessAddressList) {
        this.businessAddressList = businessAddressList;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public List<EnumeratedFactDto> getGameTypes() {
        return gameTypes;
    }

    public void setGameTypes(List<EnumeratedFactDto> gameTypes) {
        this.gameTypes = gameTypes;
    }
}
