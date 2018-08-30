package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.CreateGamingMachineDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface GamingOperatorMachineService {

    Mono<ResponseEntity> findAllAgents(int page,
                                       int pageSize,
                                       String sortDirection,
                                       String sortProperty,
                                       String institutionId,
                                       String agentId,
                                       HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> createGamingMachine(CreateGamingMachineDto createGamingMachineDto);
}
