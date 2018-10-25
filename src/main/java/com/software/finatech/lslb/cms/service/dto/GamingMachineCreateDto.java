package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.MachineGameDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class GamingMachineCreateDto {
    @NotNull(message = "please enter machine number")
    private String gameMachineNumber;
    @NotEmpty(message = "please provide machine address")
    private String machineAddress;
    @NotEmpty(message = "please provide machine manufacturer")
    private String manufacturer;
    @NotEmpty(message = "please provide machine serial Number")
    private String serialNumber;
    @NotNull(message = "please provide game details")
    private Set<MachineGameDetails> gameDetailsList;
    @NotEmpty(message = "please provide institutionId")
    private String institutionId;
    @NotEmpty(message = "please provide game type id")
    private String gameTypeId;

    public String getGameMachineNumber() {
        return gameMachineNumber;
    }

    public void setGameMachineNumber(String gameMachineNumber) {
        this.gameMachineNumber = gameMachineNumber;
    }

    public String getMachineAddress() {
        return machineAddress;
    }

    public void setMachineAddress(String machineAddress) {
        this.machineAddress = machineAddress;
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

    public Set<MachineGameDetails> getGameDetailsList() {
        return gameDetailsList;
    }

    public void setGameDetailsList(Set<MachineGameDetails> gameDetailsList) {
        this.gameDetailsList = gameDetailsList;
    }


    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }
}
