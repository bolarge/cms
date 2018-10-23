package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.GamingTerminalDto;
import com.software.finatech.lslb.cms.service.model.GamingTerminalGameDetails;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "GamingTerminals")
public class GamingTerminal extends AbstractFact {
    protected String institutionId;
    protected String manufacturer;
    protected String serialNumber;
    private Set<GamingTerminalGameDetails> gameDetailsList;
    private String gameTypeId;
    private String agentId;
    private boolean assigned;

    public Set<GamingTerminalGameDetails> getGameDetailsList() {
        return gameDetailsList;
    }

    public void setGameDetailsList(Set<GamingTerminalGameDetails> gameDetailsList) {
        this.gameDetailsList = gameDetailsList;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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


    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

//    public String getTerminalNumber() {
//        return terminalNumber;
//    }
//
//    public void setTerminalNumber(String terminalNumber) {
//        this.terminalNumber = terminalNumber;
//    }

    public Institution getInstitution() {
        if (StringUtils.isEmpty(this.institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getInstitutionName() {
        Institution institution = getInstitution();
        if (institution != null) {
            return institution.getInstitutionName();
        }
        return null;
    }

    public GameType getGameType() {
        if (this.gameTypeId == null) {
            return null;
        }
        Map gameTypeMap = Mapstore.STORE.get("GameType");
        GameType gameType = null;
        if (gameTypeMap != null) {
            gameType = (GameType) gameTypeMap.get(this.gameTypeId);
        }
        if (gameType == null) {
            gameType = (GameType) mongoRepositoryReactive.findById(this.gameTypeId, GameType.class).block();
            if (gameType != null && gameTypeMap != null) {
                gameTypeMap.put(this.gameTypeId, gameType);
            }
        }
        return gameType;
    }


    public GamingTerminalDto convertToDto() {
        GamingTerminalDto gamingTerminalDto = new GamingTerminalDto();
        gamingTerminalDto.setInstitutionId(getInstitutionId());
        if(isAssigned()==true){
            gamingTerminalDto.setAgentId(getAgentId());
        }
        gamingTerminalDto.setSerialNumber(getSerialNumber());
        gamingTerminalDto.setGameDetailsList(getGameDetailsList());
        gamingTerminalDto.setManufacturer(getManufacturer());
        gamingTerminalDto.setInstitutionId(getInstitutionId());
        gamingTerminalDto.setId(getId());
        Institution institution = getInstitution();
        if (institution != null) {
            gamingTerminalDto.setInstitutionName(institution.getInstitutionName());
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            gamingTerminalDto.setGameTypeId(getGameTypeId());
            gamingTerminalDto.setGameTypeName(gameType.getName());
        }
        return gamingTerminalDto;
    }

    @Override
    public String getFactName() {
        return "GamingTerminal";
    }
}
