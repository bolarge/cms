package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.GameTypeCreateDto;
import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class GameTypeServiceImpl implements GameTypeService {
    private static final Logger logger = LoggerFactory.getLogger(GameTypeServiceImpl.class);

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }


    @Override
    public GameType findById(String gameTypeId) {
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

    @Override
    public Mono<ResponseEntity> getAllGameTypesForInstitution(String institutionId) {
        try {
            Institution institution = getInstitution(institutionId);
            if (institution == null) {
                return Mono.just(new ResponseEntity<>("Institution does not exist", HttpStatus.BAD_REQUEST));
            }
            Set<String> gameTypeIds = institution.getGameTypeIds();
            if (gameTypeIds.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            List<GameTypeDto> gameTypeDtoList = new ArrayList<>();
            for (String gameTypeId : gameTypeIds) {
                GameType gameType = findById(gameTypeId);
                if (gameType != null) {
                    gameTypeDtoList.add(gameType.convertToDto());
                }
            }
            return Mono.just(new ResponseEntity<>(gameTypeDtoList, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting gameTypes for institution", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllGameTypesForAgent(String agentId) {
        try {
            Agent agent = getAgent(agentId);
            if (agent == null) {
                return Mono.just(new ResponseEntity<>("Agent does not exist", HttpStatus.BAD_REQUEST));
            }
            Set<String> gameTypeIds = agent.getGameTypeIds();
            if (gameTypeIds.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            List<GameTypeDto> gameTypeDtoList = new ArrayList<>();
            for (String gameTypeId : gameTypeIds) {
                GameType gameType = findById(gameTypeId);
                if (gameType != null) {
                    gameTypeDtoList.add(gameType.convertToDto());
                }
            }
            return Mono.just(new ResponseEntity<>(gameTypeDtoList, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting gameTypes for agent ", e);
        }
    }

    @Override
    public Mono<ResponseEntity> createGameType(GameTypeCreateDto gameTypeCreateDto) {
        try {
            Mono<ResponseEntity> validateCreateGameTypeResponse = validateCreateGameType(gameTypeCreateDto);
            if (validateCreateGameTypeResponse != null) {
                return validateCreateGameTypeResponse;
            }
            GameType gameType = fromGameTypeCreateDto(gameTypeCreateDto);
            mongoRepositoryReactive.saveOrUpdate(gameType);
            return Mono.just(new ResponseEntity<>(gameType.convertToDto(), HttpStatus.OK));
        } catch (Exception ex) {
            return logAndReturnError(logger, String.format("An error occurred while creating game type %s", gameTypeCreateDto.getName()), ex);
        }
    }

    private GameType fromGameTypeCreateDto(GameTypeCreateDto gameTypeCreateDto) {
        GameType gameType = new GameType();
        gameType.setId(UUID.randomUUID().toString());
        gameType.setAipDurationMonths(gameTypeCreateDto.getAipDurationMonths());
        gameType.setAgentLicenseDurationMonths(gameTypeCreateDto.getAgentLicenseDurationMonths());
        gameType.setGamingMachineLicenseDurationMonths(gameTypeCreateDto.getGamingMachineLicenseDurationMonths());
        gameType.setInstitutionLicenseDurationMonths(gameTypeCreateDto.getLicenseDurationMonths());
        gameType.setName(gameTypeCreateDto.getName());
        gameType.setDescription(gameTypeCreateDto.getDescription());
        gameType.setAllowsGamingMachine(gameTypeCreateDto.getAllowsGamingMachine());
        return gameType;
    }

    private Mono<ResponseEntity> validateCreateGameType(GameTypeCreateDto gameTypeCreateDto) {
        String name = gameTypeCreateDto.getName();
        Query queryForGameTypeWithName = Query.query(Criteria.where("name").is(name));
        GameType gameTypeWithName = (GameType) mongoRepositoryReactive.find(queryForGameTypeWithName, GameType.class).block();
        if (gameTypeWithName != null) {
            return Mono.just(new ResponseEntity<>(String.format("Category with name %s already exist", name), HttpStatus.BAD_REQUEST));
        }
        if (gameTypeCreateDto.getAllowsGamingMachine() && (gameTypeCreateDto.getGamingMachineLicenseDurationMonths() == null || gameTypeCreateDto.getGamingMachineLicenseDurationMonths() <= 0)) {
            return Mono.just(new ResponseEntity<>("Please specify gaming machine licence duration for category in months", HttpStatus.BAD_REQUEST));
        }
        return null;
    }


    private Institution getInstitution(String institutionId) {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    private Agent getAgent(String agentId) {
        return (Agent) mongoRepositoryReactive.findById(agentId, Agent.class).block();
    }
}
