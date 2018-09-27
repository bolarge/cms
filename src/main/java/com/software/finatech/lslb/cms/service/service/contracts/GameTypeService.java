package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.dto.GameTypeCreateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface GameTypeService {

    GameType findById(String gameTypeId);

    Mono<ResponseEntity> getAllGameTypesForInstitution(String institutionId);

    Mono<ResponseEntity> getAllGameTypesForAgent(String agentId);

    Mono<ResponseEntity> createGameType(GameTypeCreateDto gameTypeCreateDto);
}
