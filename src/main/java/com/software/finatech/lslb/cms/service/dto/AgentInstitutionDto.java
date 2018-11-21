package com.software.finatech.lslb.cms.service.dto;


import java.util.ArrayList;
import java.util.List;

public class AgentInstitutionDto {
    private String institutionName;
    private String institutionId;
    private List<EnumeratedFactDto> gameTypes = new ArrayList<>();
    private List<String> businessAddressList = new ArrayList<>();

    public List<String> getBusinessAddressList() {
        return businessAddressList;
    }

    public void setBusinessAddressList(List<String> businessAddressList) {
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
