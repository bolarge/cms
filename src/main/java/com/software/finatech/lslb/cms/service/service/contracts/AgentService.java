package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.AgentCreateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface AgentService {

    Mono<ResponseEntity> findAllAgents(int page,
                                       int pageSize,
                                       String sortDirection,
                                       String sortProperty,
                                       String institutionIds,
                                       String gameTypeIds,
                                       HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> createAgent(AgentCreateDto agentCreateDto);
}
