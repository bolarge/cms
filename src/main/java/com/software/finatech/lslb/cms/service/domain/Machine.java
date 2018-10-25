package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.MachineDto;
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
public class Machine extends AbstractFact {
    private String institutionId;
    private String manufacturer;
    private String serialNumber;
    private String machineAddress;
    private String gameTypeId;
    private String agentId;
    private String machineTypeId;
    private String machineStatusId;

    public String getMachineStatusId() {
        return machineStatusId;
    }

    public void setMachineStatusId(String machineStatusId) {
        this.machineStatusId = machineStatusId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getMachineTypeId() {
        return machineTypeId;
    }

    public void setMachineTypeId(String machineTypeId) {
        this.machineTypeId = machineTypeId;
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


    public MachineType getMachineType() {
        if (StringUtils.isEmpty(this.machineTypeId)) {
            return null;
        }
        Map machineTypeMap = Mapstore.STORE.get("MachineType");
        MachineType machineType = null;
        if (machineType != null) {
            machineType = (MachineType) machineTypeMap.get(this.machineTypeId);
        }
        if (machineType == null) {
            machineType = (MachineType) mongoRepositoryReactive.findById(this.machineTypeId, MachineType.class).block();
            if (machineType != null && machineTypeMap != null) {
                machineTypeMap.put(this.machineTypeId, machineType);
            }
        }
        return machineType;
    }

    public MachineStatus getMachineStatus() {
        if (StringUtils.isEmpty(this.machineStatusId)) {
            return null;
        }
        Map machineStatusMap = Mapstore.STORE.get("MachineStatus");
        MachineStatus machineStatus = null;
        if (machineStatus != null) {
            machineStatus = (MachineStatus) machineStatusMap.get(this.machineStatusId);
        }
        if (machineStatus == null) {
            machineStatus = (MachineStatus) mongoRepositoryReactive.findById(this.machineStatusId, MachineStatus.class).block();
            if (machineStatus != null && machineStatusMap != null) {
                machineStatusMap.put(this.machineTypeId, machineStatus);
            }
        }
        return machineStatus;
    }


    public MachineDto convertToDto() {
        MachineDto dto = new MachineDto();
        dto.setInstitutionId(getInstitutionId());
        dto.setManufacturer(getManufacturer());
        dto.setSerialNumber(getSerialNumber());
        dto.setMachineAddress(getMachineAddress());
        dto.setInstitutionId(getInstitutionId());
        dto.setId(getId());
        Institution institution = getInstitution();
        if (institution != null) {
            dto.setInstitutionName(institution.getInstitutionName());
        }
        Agent agent = getAgent();
        if (agent != null) {
            dto.setAgentFullName(agent.getFullName());
            dto.setAgentId(this.agentId);
        }
        MachineType machineType = getMachineType();
        if (machineType != null) {
            dto.setMachineType(machineType.toString());
            dto.setMachineTypeId(this.machineTypeId);
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            dto.setGameTypeId(getGameTypeId());
            dto.setGameTypeName(gameType.getName());
        }
        return dto;
    }

    public MachineDto convertToFullDto() {
        MachineDto dto = convertToDto();
        dto.setMachineGames(getMachineGames());
        return dto;
    }

    public Agent getAgent() {
        if (StringUtils.isEmpty(this.agentId)) {
            return null;
        }
        return (Agent) mongoRepositoryReactive.findById(this.agentId, Agent.class).block();
    }

    private ArrayList<MachineGameDetails> getMachineGames() {
        ArrayList<MachineGameDetails> machineGameDetails = new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("machineId").is(this.id));
        ArrayList<MachineGame> machineGames = (ArrayList<MachineGame>) mongoRepositoryReactive.findAll(query, MachineGame.class).toStream().collect(Collectors.toList());
        for (MachineGame machineGame : machineGames) {
            machineGameDetails.add(MachineGameDetails.fromGameNameAndVersionAndState(machineGame.getGameName(), machineGame.getGameVersion(), machineGame.getActive()));
        }
        return machineGameDetails;
    }

    @Override
    public String getFactName() {
        return "Machine";
    }
}
