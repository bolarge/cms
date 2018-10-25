package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.GamingMachineDto;
import com.software.finatech.lslb.cms.service.model.MachineGameDetails;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
@Document(collection = "GamingMachines")
public class GamingMachine extends AbstractFact {
    protected String institutionId;
    protected String manufacturer;
    protected String serialNumber;
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
        if (StringUtils.isEmpty(this.gameTypeId)) {
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

    public GamingMachineDto convertToFullDto() {
        GamingMachineDto dto = convertToDto();
        dto.setMachineGames(getMachineGames());
        return dto;
    }

    private ArrayList<MachineGameDetails> getMachineGames() {
        ArrayList<MachineGameDetails> machineGameDetails = new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("gamingMachineId").is(this.id));
        ArrayList<MachineGame> machineGames = (ArrayList<MachineGame>) mongoRepositoryReactive.findAll(query, MachineGame.class).toStream().collect(Collectors.toList());
        for (MachineGame machineGame : machineGames) {
            machineGameDetails.add(MachineGameDetails.fromGameNameAndVersionAndState(machineGame.getGameName(), machineGame.getGameVersion(), machineGame.getActive()));
        }
        return machineGameDetails;
    }


    @Override
    public String getFactName() {
        return "GamingMachine";
    }
}
