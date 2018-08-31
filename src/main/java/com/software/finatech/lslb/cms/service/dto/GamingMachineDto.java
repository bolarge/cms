package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.GamingMachineGameDetails;

import java.util.HashSet;
import java.util.Set;

public class GamingMachineDto {
    private String agentName;
    private String agentId;
    private String institutionId;
    private String institutionName;
    private Set<GamingMachineGameDetails> gameDetailsList = new HashSet<>();
    private  boolean managedByInstitution;
    private String manufacturer;
    private String serialNumber;
    private String machineNumber;

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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

    public Set<GamingMachineGameDetails> getGameDetailsList() {
        return gameDetailsList;
    }

    public void setGameDetailsList(Set<GamingMachineGameDetails> gameDetailsList) {
        this.gameDetailsList = gameDetailsList;
    }

    public boolean isManagedByInstitution() {
        return managedByInstitution;
    }

    public void setManagedByInstitution(boolean managedByInstitution) {
        this.managedByInstitution = managedByInstitution;
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

    public String getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }
}
