package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.dto.GameTypeCreateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;

public interface GameTypeService {

    String findNameById(String id);

    GameType findById(String gameTypeId);

    GameType findGameTypeBySearchKey(String searchKey);

    Mono<ResponseEntity> getAllGameTypesForInstitution(String institutionId);

    Mono<ResponseEntity> getAllGameTypesForAgent(String agentId);

    Mono<ResponseEntity> createGameType(GameTypeCreateDto gameTypeCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> findGameTypesForMachineCreation(String agentId, String institutionId, String machineTypeId);
}
