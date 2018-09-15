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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(value = "GameType", description = "", tags = "")
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
    @ApiOperation(value = "Create new GameType", response = GameType.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createGameType(@RequestBody @Valid GameTypeCreateDto gameTypeCreateDto) {
       try {
           GameType gameType = new GameType();
           gameType.setId(UUID.randomUUID().toString());
           gameType.setAipDurationMonths(gameTypeCreateDto.getAipDurationMonths());
           gameType.setAgentLicenseDurationMonths(gameTypeCreateDto.getAgentLicenseDurationMonths());
           gameType.setGamingMachineLicenseDurationMonths(gameTypeCreateDto.getGamingMachineLicenseDurationMonths());
           gameType.setInstitutionLicenseDurationMonths(gameTypeCreateDto.getLicenseDurationMonths());
           gameType.setName(gameTypeCreateDto.getName());
           gameType.setDescription(gameTypeCreateDto.getDescription());
           mongoRepositoryReactive.saveOrUpdate(gameType);

           return Mono.just(new ResponseEntity<>(gameType.convertToDto(), HttpStatus.OK));
       }catch (Exception ex){
           return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

       }

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
        mongoRepositoryReactive.saveOrUpdate(gameType);
        return Mono.just(new ResponseEntity<>(gameType.convertToDto(), HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{institutionId}/")
    @ApiOperation(value = "Get GameTypes for institution", response = GameTypeDto.class, responseContainer = "List",consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getGameTypesForInstitution(@PathVariable("institutionId") String institutionId) {
        return gameTypeService.getAllGameTypesForInstitution(institutionId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{agentId}/")
    @ApiOperation(value = "Get GameTypes for agent ", response = GameTypeDto.class, responseContainer = "List",consumes = "application/json",
    notes = "returns all game types an agent operates in")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getGameTypesForAgent(@PathVariable("agentId") String agentId) {
        return gameTypeService.getAllGameTypesForAgent(agentId);
    }
}
