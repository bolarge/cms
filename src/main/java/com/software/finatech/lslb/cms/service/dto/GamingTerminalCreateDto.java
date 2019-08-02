package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.GamingTerminalGameDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class GamingTerminalCreateDto {

    @NotEmpty(message = "please provide terminal serial Number")
    private String serialNumber;
    @NotNull(message = "please provide game details")
    private Set<GamingTerminalGameDetails> gameDetailsList;
    @NotEmpty(message = "please provide institutionId")
    private String institutionId;
    @NotEmpty(message = "please provide game type id")
    private String gameTypeId;
    private String manufacturer;



//    public String getGameTerminalNumber() {
//        return gameTerminalNumber;
//    }
//
//    public void setGameTerminalNumber(String gameTerminalNumber) {
//        this.gameTerminalNumber = gameTerminalNumber;
//    }

//    public String getTerminalAddress() {
//        return terminalAddress;
//    }
//
//    public void setTerminalAddress(String terminalAddress) {
//        this.terminalAddress = terminalAddress;
//    }

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

    public Set<GamingTerminalGameDetails> getGameDetailsList() {
        return gameDetailsList;
    }

    public void setGameDetailsList(Set<GamingTerminalGameDetails> gameDetailsList) {
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
