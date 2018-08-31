package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import com.software.finatech.lslb.cms.service.dto.GamingMachineDto;
import com.software.finatech.lslb.cms.service.model.GamingMachineGameDetails;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "GamingMachines")
public class GamingMachine extends AbstractFact {
   protected String institutionId;
   protected String agentId;
   protected boolean managedByInstitution;
   protected String manufacturer;
   protected String serialNumber;
   protected Set<GamingMachineGameDetails> gameDetailsList = new HashSet<>();
   protected String machineNumber;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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

    public Set<GamingMachineGameDetails> getGameDetailsList() {
        return gameDetailsList;
    }

    public void setGameDetailsList(Set<GamingMachineGameDetails> gameDetailsList) {
        this.gameDetailsList = gameDetailsList;
    }

    public String getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }

    public Institution getInstitution() {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    private AuthInfo getAgentUser() {
        AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(agentId, AuthInfo.class).block();
        if (authInfo == null) {
            return null;
        } else {
            return authInfo;
        }
    }


    public GamingMachineDto convertToDto(){
        GamingMachineDto gamingMachineDto = new GamingMachineDto();
        gamingMachineDto.setAgentId(getAgentId());
        gamingMachineDto.setGameDetailsList(getGameDetailsList());
        gamingMachineDto.setInstitutionId(getInstitutionId());
        gamingMachineDto.setMachineNumber(getMachineNumber());
        gamingMachineDto.setManagedByInstitution(isManagedByInstitution());
        gamingMachineDto.setManufacturer(getManufacturer());

        AuthInfo agent = getAgentUser();
        if (agent != null){
            gamingMachineDto.setAgentName(agent.getFullName());
        }
        Institution institution = getInstitution();
        if (institution != null){
            gamingMachineDto.setInstitutionName(institution.getInstitutionName());
        }
        return gamingMachineDto;
    }

    @Override
    public String getFactName() {
        return "GamingMachine";
    }
}
