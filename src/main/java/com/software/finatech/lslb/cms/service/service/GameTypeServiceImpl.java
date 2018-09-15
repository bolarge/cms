package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        private Institution getInstitution (String institutionId){
            return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
        }

        private Agent getAgent (String agentId){
            return (Agent) mongoRepositoryReactive.findById(agentId, Agent.class).block();
        }
    }
