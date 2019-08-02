package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.GamingTerminalGameDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class GamingTerminalUpdateDto {
//    @NotNull(message = "please enter terminal number")
//    private String gameTerminalNumber;
//    @NotEmpty(message = "please provide terminal address")
//    private String terminalAddress;
//    @NotEmpty(message = "please provide terminal manufacturer")
    private String manufacturer;
    @NotEmpty(message = "please provide terminal serial Number")
    private String serialNumber;
    @NotNull(message = "please provide game details")
    private Set<GamingTerminalGameDetails> gameDetailsList;
    @NotNull(message = "please provide gaming terminal id")
    private String id;
    @NotNull(message = "please provide game type id")
    private String gameTypeId;
    private String agentId;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

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
//
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
}
