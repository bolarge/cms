package com.software.finatech.lslb.cms.service.util.adapters;

import com.software.finatech.lslb.cms.service.domain.AgentInstitution;
import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.AgentInstitutionDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class AgentInstitutionAdapter {

    public static AgentInstitutionDto convertAgentInstitutionToDto(AgentInstitution agentInstitution, MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        AgentInstitutionDto agentInstitutionDto = new AgentInstitutionDto();
        Institution institution = getInstitution(agentInstitution.getInstitutionId(), mongoRepositoryReactive);
        if (institution != null) {
            agentInstitutionDto.setInstitutionId(institution.getId());
            agentInstitutionDto.setInstitutionName(institution.getInstitutionName());
        }

        GameType gameType = getGameType(agentInstitution.getGameTypeId(), mongoRepositoryReactive);
        if (gameType != null) {
            agentInstitutionDto.setGameTypeId(gameType.getId());
            agentInstitutionDto.setGameTypeName(gameType.getName());
        }
        agentInstitutionDto.setBusinessAddressList(agentInstitution.getBusinessAddressList());
        return agentInstitutionDto;
    }

    private static GameType getGameType(String gameTypeId, MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        if (StringUtils.isEmpty(gameTypeId)) {
            return null;
        }
        Map gameTypeMap = Mapstore.STORE.get("GameType");
        GameType gameType = null;
        if (gameTypeMap != null) {
            gameType = (GameType) gameTypeMap.get(gameTypeId);
        }
        if (gameType == null) {
            gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
            if (gameType != null && gameTypeMap != null) {
                gameTypeMap.put(gameTypeId, gameType);
            }
        }
        return gameType;
    }

    public static Institution getInstitution(String institutionId, MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        if (StringUtils.isEmpty(institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }
}
