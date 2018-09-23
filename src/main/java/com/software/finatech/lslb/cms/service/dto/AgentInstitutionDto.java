package com.software.finatech.lslb.cms.service.dto;


import java.util.ArrayList;
import java.util.List;

public class AgentInstitutionDto {
    private String institutionName;
    private String institutionId;
    private String gameTypeId;
    private String gameTypeName;
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

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }
}
