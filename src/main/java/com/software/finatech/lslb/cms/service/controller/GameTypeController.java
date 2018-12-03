package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.dto.GameTypeCreateDto;
import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import com.software.finatech.lslb.cms.service.dto.GameTypeUpdateDto;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Api(value = "GameType", description = "", tags = "Game Type Controller")
@RestController
@RequestMapping("/api/v1/gameType")
public class GameTypeController extends BaseController {

    private GameTypeService gameTypeService;

    @Autowired
    public void setGameTypeService(GameTypeService gameTypeService) {
        this.gameTypeService = gameTypeService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/allgametypes")
    @ApiOperation(value = "Get GameType", response = GameTypeDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> gameTypeDtos() {
        try {
            //@TODO validate request params
            // List<FactObject> gameTypes = Mapstore.STORE.get("GameType").values().stream().collect(Collectors.toList());
            ArrayList<GameType> gameTypes = (ArrayList<GameType>) mongoRepositoryReactive
                    .findAll(new Query(), GameType.class).toStream().collect(Collectors.toList());
            ArrayList<GameTypeDto> gameTypeDtos = new ArrayList<>();
            gameTypes.forEach(entry -> {
                gameTypeDtos.add(entry.convertToDto());
            });

            if (gameTypeDtos.size() == 0) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            return Mono.just(new ResponseEntity<>(gameTypeDtos, HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/new")
    @ApiOperation(value = "Create new GameType", response = GameTypeDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createGameType(@RequestBody @Valid GameTypeCreateDto gameTypeCreateDto, HttpServletRequest request) {
        return gameTypeService.createGameType(gameTypeCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Updates an existing GameType", response = GameType.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> updateGameType(@RequestBody @Valid GameTypeUpdateDto gameTypeUpdateDto) {
        GameType gameType = (GameType) mongoRepositoryReactive.findById(gameTypeUpdateDto.getId(), GameType.class).block();
        if (gameType == null) {
            return Mono.just(new ResponseEntity<>("Invalid GameType Selected", HttpStatus.BAD_REQUEST));
        }
        gameType.setAipDurationMonths(gameTypeUpdateDto.getAipDurationMonths());
        gameType.setInstitutionLicenseDurationMonths(gameTypeUpdateDto.getLicenseDuration());
        gameType.setName(gameTypeUpdateDto.getName());
        gameType.setDescription(gameTypeUpdateDto.getDescription());
        gameType.setAgentLicenseDurationMonths(gameTypeUpdateDto.getAgentLicenseDurationMonths());
        gameType.setGamingMachineLicenseDurationMonths(gameTypeUpdateDto.getGamingMachineLicenseDurationMonths());
        gameType.setAllowsGamingTerminal(gameTypeUpdateDto.getAllowsGamingTerminal());
        gameType.setAllowsGamingMachine(gameTypeUpdateDto.getAllowsGamingMachine());
        gameType.setGamingTerminalLicenseDurationMonths(gameTypeUpdateDto.getGamingTerminalLicenseDurationMonths());
        mongoRepositoryReactive.saveOrUpdate(gameType);
        return Mono.just(new ResponseEntity<>(gameType.convertToDto(), HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/for-institution/")
    @ApiOperation(value = "Get GameTypes for institution", response = GameTypeDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getGameTypesForInstitution(@RequestParam("institutionId") String institutionId) {
        return gameTypeService.getAllGameTypesForInstitution(institutionId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/for-agent/")
    @ApiOperation(value = "Get GameTypes for agent ", response = GameTypeDto.class, responseContainer = "List", consumes = "application/json",
            notes = "returns all game types an agent operates in")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getGameTypesForAgent(@RequestParam("agentId") String agentId) {
        return gameTypeService.getAllGameTypesForAgent(agentId);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/for-payment")
    @ApiOperation(value = "Get GameTypes for a payment operation", response = GameTypeDto.class, responseContainer = "List", consumes = "application/json",
            notes = "returns all game types for a payment operation, You supply agent id or institution id , it finds all the game types registered for the person")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getGameTypesForPayment(@RequestParam("agentId") String agentId, @RequestParam("institutionId") String institutionId) {
        if (!StringUtils.isEmpty(institutionId) && StringUtils.isEmpty(agentId)) {
            return getGameTypesForInstitution(institutionId);
        }
        if (!StringUtils.isEmpty(agentId) && StringUtils.isEmpty(institutionId)) {
            return getGameTypesForAgent(agentId);
        }
        return Mono.just(new ResponseEntity<>("Please supply one of agent id or institution Id", HttpStatus.BAD_REQUEST));
    }


    @RequestMapping(method = RequestMethod.GET, value = "/for-machine-creation", params = {"institutionId", "agentId", "machineTypeId"})
    @ApiOperation(value = "Get GameTypes for machine creation", response = GameTypeDto.class, responseContainer = "List", consumes = "application/json",
            notes = "it finds all the game types registered for the person, and filters if they allow gaming terminal or gaming machine")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getGameTypesForMachineCreation(@RequestParam("agentId") String agentId,
                                                               @RequestParam("institutionId") String institutionId,
                                                               @RequestParam("machineTypeId") String machineTypeId) {
        return gameTypeService.findGameTypesForMachineCreation(agentId, institutionId, machineTypeId);
    }
}
