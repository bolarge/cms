package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.MachineGameDetails;
import com.software.finatech.lslb.cms.service.referencedata.MachineTypeReferenceData;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class MachineCreateDto {
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
    @NotEmpty(message = "please provide machine type id")
    private String machineTypeId;
    @NotEmpty(message = "please provide machine status id")
    private String machineStatusId;

    public String getMachineStatusId() {
        return machineStatusId;
    }

    public void setMachineStatusId(String machineStatusId) {
        this.machineStatusId = machineStatusId;
    }

    public String getMachineTypeId() {
        return machineTypeId;
    }

    public void setMachineTypeId(String machineTypeId) {
        this.machineTypeId = machineTypeId;
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

    public boolean isCreateGamingMachine() {
        return StringUtils.equals(MachineTypeReferenceData.GAMING_MACHINE_ID, this.machineTypeId);
    }

    public boolean isCreateGamingTerminal() {
        return StringUtils.equals(MachineTypeReferenceData.GAMING_TERMINAL_ID, this.machineTypeId);
    }
}
