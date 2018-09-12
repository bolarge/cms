package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.GamingMachineDto;
import com.software.finatech.lslb.cms.service.model.GamingMachineGameDetails;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "GamingMachines")
public class GamingMachine extends AbstractFact {
   protected String institutionId;
   protected String manufacturer;
   protected String serialNumber;
   protected Set<GamingMachineGameDetails> gameDetailsList = new HashSet<>();
   protected String machineNumber;
   private String machineAddress;
   private String gameTypeId;

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getMachineAddress() {
        return machineAddress;
    }

    public void setMachineAddress(String machineAddress) {
        this.machineAddress = machineAddress;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
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

    public GamingMachineDto convertToDto(){
        GamingMachineDto gamingMachineDto = new GamingMachineDto();
        gamingMachineDto.setGameDetailsList(getGameDetailsList());
        gamingMachineDto.setInstitutionId(getInstitutionId());
        gamingMachineDto.setMachineNumber(getMachineNumber());
        gamingMachineDto.setManufacturer(getManufacturer());
        gamingMachineDto.setSerialNumber(getSerialNumber());
        gamingMachineDto.setMachineAddress(getMachineAddress());
        gamingMachineDto.setInstitutionId(getInstitutionId());
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
