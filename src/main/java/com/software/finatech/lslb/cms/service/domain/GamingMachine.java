package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.GamingMachineDto;
import com.software.finatech.lslb.cms.service.model.GamingMachineGameDetails;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "GamingMachines")
public class GamingMachine extends AbstractFact {
    protected String institutionId;
    protected String manufacturer;
    protected String serialNumber;
    protected Set<String> gameList = new HashSet<>();
    protected String machineNumber;
    private String machineAddress;
    private String gameTypeId;


    public Set<String> getGameList() {
        return gameList;
    }

    public void setGameList(Set<String> gameList) {
        this.gameList = gameList;
    }

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

    public String getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }

    public Institution getInstitution() {
        if (StringUtils.isEmpty(this.institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
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


    public GamingMachineDto convertToDto() {
        GamingMachineDto gamingMachineDto = new GamingMachineDto();
        gamingMachineDto.setInstitutionId(getInstitutionId());
        gamingMachineDto.setMachineNumber(getMachineNumber());
        gamingMachineDto.setManufacturer(getManufacturer());
        gamingMachineDto.setSerialNumber(getSerialNumber());
        gamingMachineDto.setMachineAddress(getMachineAddress());
        gamingMachineDto.setInstitutionId(getInstitutionId());
        gamingMachineDto.setId(getId());
        Institution institution = getInstitution();
        if (institution != null) {
            gamingMachineDto.setInstitutionName(institution.getInstitutionName());
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            gamingMachineDto.setGameTypeId(getGameTypeId());
            gamingMachineDto.setGameTypeName(gameType.getName());
        }
        return gamingMachineDto;
    }

    @Override
    public String getFactName() {
        return "GamingMachine";
    }
}
