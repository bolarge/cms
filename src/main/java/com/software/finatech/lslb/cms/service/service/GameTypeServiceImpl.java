package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.EnumeratedFact;
import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.GameTypeCreateDto;
import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.MachineTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class GameTypeServiceImpl implements GameTypeService {
    private static final Logger logger = LoggerFactory.getLogger(GameTypeServiceImpl.class);
    private static final String configurationsAuditActionId = AuditActionReferenceData.CONFIGURATIONS_ID;

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;

    @Autowired
    public void setSpringSecurityAuditorAware(SpringSecurityAuditorAware springSecurityAuditorAware) {
        this.springSecurityAuditorAware = springSecurityAuditorAware;
    }

    @Autowired
    public void setAuditLogHelper(AuditLogHelper auditLogHelper) {
        this.auditLogHelper = auditLogHelper;
    }

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
    public GameType findGameTypeBySearchKey(String searchKey) {
        if (StringUtils.equalsIgnoreCase("Scartch Card", searchKey)) {
            searchKey = "Scratch Card";
        }
        Collection<EnumeratedFact> gameTypes = ReferenceDataUtil.getAllEnumeratedFacts("GameType");
        for (EnumeratedFact enumeratedFact : gameTypes) {
            GameType gameType = (GameType) enumeratedFact;
            if (StringUtils.equalsIgnoreCase(searchKey, gameType.getName())
                    || StringUtils.equalsIgnoreCase(searchKey, gameType.getShortCode())) {
                return gameType;
            }
        }
        return null;
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
    public Mono<ResponseEntity> createGameType(GameTypeCreateDto gameTypeCreateDto, HttpServletRequest request) {
        try {
            Mono<ResponseEntity> validateCreateGameTypeResponse = validateCreateGameType(gameTypeCreateDto);
            if (validateCreateGameTypeResponse != null) {
                return validateCreateGameTypeResponse;
            }
            GameType gameType = fromGameTypeCreateDto(gameTypeCreateDto);
            mongoRepositoryReactive.saveOrUpdate(gameType);

            String verbiage = String.format("Created category, Name -> %s ", gameType);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(configurationsAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(gameType.convertToDto(), HttpStatus.OK));
        } catch (Exception ex) {
            return logAndReturnError(logger, String.format("An error occurred while creating game type %s", gameTypeCreateDto.getName()), ex);
        }
    }

    @Override
    public Mono<ResponseEntity> findGameTypesForMachineCreation(String agentId, String institutionId, String machineTypeId) {
        try {
            if ((StringUtils.isEmpty(agentId) && StringUtils.isEmpty(institutionId))
                    || (!StringUtils.isEmpty(agentId) && !StringUtils.isEmpty(institutionId))) {
                return Mono.just(new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST));
            }
            Set<String> gameTypeIds = new HashSet<>();
            Set<GameTypeDto> gameTypeDtos = new HashSet<>();
            if (!StringUtils.isEmpty(agentId)) {
                Agent agent = getAgent(agentId);
                if (agent == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Agent with id %s not found", agentId), HttpStatus.BAD_REQUEST));
                }
                gameTypeIds = agent.getGameTypeIds();
            }

            if (!StringUtils.isEmpty(institutionId)) {
                Institution institution = getInstitution(institutionId);
                if (institution == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Operator with id %s not found", institutionId), HttpStatus.BAD_REQUEST));
                }
                gameTypeIds = institution.getGameTypeIds();
            }

            if (gameTypeIds.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            for (String gameTypeId : gameTypeIds) {
                GameType gameType = findById(gameTypeId);
                if (gameType != null) {
                    if (StringUtils.equals(MachineTypeReferenceData.GAMING_TERMINAL_ID, machineTypeId)
                            && gameType.getAllowsGamingTerminal()) {
                        gameTypeDtos.add(gameType.convertToDto());
                    }
                    if (StringUtils.equals(MachineTypeReferenceData.GAMING_MACHINE_ID, machineTypeId)
                            && gameType.getAllowsGamingMachine()) {
                        gameTypeDtos.add(gameType.convertToDto());
                    }
                }
            }
            return Mono.just(new ResponseEntity<>(gameTypeDtos, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting game tyoes for machine creation", e);
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
        gameType.setAllowsGamingTerminal(gameTypeCreateDto.getAllowsGamingTerminal());
        gameType.setGamingTerminalLicenseDurationMonths(gameTypeCreateDto.getGamingTerminalLicenseDurationMonths());
        return gameType;
    }

    private Mono<ResponseEntity> validateCreateGameType(GameTypeCreateDto gameTypeCreateDto) {
        String name = gameTypeCreateDto.getName();
        Query queryForGameTypeWithName = Query.query(Criteria.where("name").is(name));
        GameType gameTypeWithName = (GameType) mongoRepositoryReactive.find(queryForGameTypeWithName, GameType.class).block();
        if (gameTypeWithName != null) {
            return Mono.just(new ResponseEntity<>(String.format("Category with name %s already exist", name), HttpStatus.BAD_REQUEST));
        }
        if (gameTypeCreateDto.getAllowsGamingMachine() && (gameTypeCreateDto.getGamingMachineLicenseDurationMonths() <= 0)) {
            return Mono.just(new ResponseEntity<>("Gaming Machine licence duration should be at least one month", HttpStatus.BAD_REQUEST));
        }
        if (gameTypeCreateDto.getAllowsGamingTerminal() && (gameTypeCreateDto.getGamingTerminalLicenseDurationMonths() <= 0)) {
            return Mono.just(new ResponseEntity<>("Gaming Terminal licence duration should be at least one month", HttpStatus.BAD_REQUEST));
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
