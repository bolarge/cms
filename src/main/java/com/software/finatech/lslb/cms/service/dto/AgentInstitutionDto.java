package com.software.finatech.lslb.cms.service.dto;


public class AgentInstitutionDto {
    private String institutionName;
    private String institutionId;
    private String gameTypeId;
    private String gameTypeName;
    private String gameTypeDescription;

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

    public String getGameTypeDescription() {
        return gameTypeDescription;
    }

    public void setGameTypeDescription(String gameTypeDescription) {
        this.gameTypeDescription = gameTypeDescription;
    }
}
