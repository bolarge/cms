package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.GamingMachineGameDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class GamingMachineUpdateDto {
    @NotNull(message = "please enter machine number")
    private String gameMachineNumber;
    @NotEmpty(message = "please provide machine address")
    private String machineAddress;
    @NotEmpty(message = "please provide machine manufacturer")
    private String manufacturer;
    @NotEmpty(message = "please provide machine serial Number")
    private String serialNumber;
    @NotNull(message = "please provide game details")
    private Set<GamingMachineGameDetails> gameDetailsList;
    @NotNull(message = "please provide gaming machine id")
    private String id;

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

    public Set<GamingMachineGameDetails> getGameDetailsList() {
        return gameDetailsList;
    }

    public void setGameDetailsList(Set<GamingMachineGameDetails> gameDetailsList) {
        this.gameDetailsList = gameDetailsList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
