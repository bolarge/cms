package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.GamingMachineCreateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface GamingMachineService {

    Mono<ResponseEntity> findAllGamingMachines(int page,
                                       int pageSize,
                                       String sortDirection,
                                       String sortProperty,
                                       String institutionId,
                                       String agentId,
                                       HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> createGamingMachine(GamingMachineCreateDto gamingMachineCreateDto);
}
