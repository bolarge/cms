package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.MachineGame;
import com.software.finatech.lslb.cms.service.model.MachineGameDetails;

import java.util.ArrayList;
import java.util.HashSet;

public class GamingMachineDto {
    private String institutionId;
    private String institutionName;
    private ArrayList<MachineGameDetails> machineGames = new ArrayList<>();
    private String manufacturer;
    private String serialNumber;
    private String machineNumber;
    private String machineAddress;
    private String gameTypeId;
    private String gameTypeName;
    private String id;

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

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public ArrayList<MachineGameDetails> getMachineGames() {
        return machineGames;
    }

    public void setMachineGames(ArrayList<MachineGameDetails> machineGames) {
        this.machineGames = machineGames;
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
