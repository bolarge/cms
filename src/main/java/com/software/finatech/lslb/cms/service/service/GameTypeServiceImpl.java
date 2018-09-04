package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
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

@Service
public class GameTypeServiceImpl implements GameTypeService {
    private static final Logger logger = LoggerFactory.getLogger(GameTypeServiceImpl.class);

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private InstitutionService institutionService;

    @Autowired
    public GameTypeServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                               InstitutionService institutionService) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.institutionService = institutionService;
    }

    @Override
    public GameType findById(String gameTypeId) {
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
            Institution institution = institutionService.findById(institutionId);
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
            return ErrorResponseUtil.logAndReturnError(logger, "An error occured while getting gameTypes for institutions ", e);
        }
    }
}
