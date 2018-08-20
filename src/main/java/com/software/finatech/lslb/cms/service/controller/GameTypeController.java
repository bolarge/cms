package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Api(value = "GameType", description = "", tags = "")
@RestController
@RequestMapping("/api/v1/gameType")
public class GameTypeController extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/allgametypes")
    @ApiOperation(value = "Get GameType", response = GameTypeDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
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

}
