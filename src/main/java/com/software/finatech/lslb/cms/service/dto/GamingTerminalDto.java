package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.GamingTerminalGameDetails;

import java.util.HashSet;
import java.util.Set;

public class GamingTerminalDto {
    private String institutionId;
    private String institutionName;
    private Set<GamingTerminalGameDetails> gameDetailsList = new HashSet<>();
    private String manufacturer;
    private String serialNumber;
    //private String terminalNumber;
  //  private String terminalAddress;
    private String gameTypeId;
    private String gameTypeName;
    private String id;
    private String agentId;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Set<GamingTerminalGameDetails> getGameDetailsList() {
        return gameDetailsList;
    }

    public void setGameDetailsList(Set<GamingTerminalGameDetails> gameDetailsList) {
        this.gameDetailsList = gameDetailsList;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

}
