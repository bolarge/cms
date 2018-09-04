package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.GameType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface GameTypeService {

    GameType findById(String gameTypeId);

    Mono<ResponseEntity> getAllGameTypesForInstitution(String institutionId);
}
