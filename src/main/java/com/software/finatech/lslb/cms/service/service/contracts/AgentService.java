package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.dto.AgentCreateDto;
import com.software.finatech.lslb.cms.service.dto.AgentInstitutionCreateDto;
import com.software.finatech.lslb.cms.service.dto.AgentUpdateDto;
import com.software.finatech.lslb.cms.service.dto.AgentValidationDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AgentService {

    Mono<ResponseEntity> findAllAgents(int page,
                                       int pageSize,
                                       String sortDirection,
                                       String sortProperty,
                                       String institutionIds,
                                       String gameTypeIds,
                                       HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> findAgentsBySearchKey(String searchKey);

    Mono<ResponseEntity> createAgent(AgentCreateDto agentCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> updateAgent(AgentUpdateDto agentUpdateDto, HttpServletRequest request);

    Agent findAgentById(String agentId);

    void saveAgent(Agent agent);

    Mono<ResponseEntity> createAgentUnderInstitution(AgentInstitutionCreateDto agentInstitutionCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> getAgentFullDetailById(String agentId);

    Mono<ResponseEntity> validateAgentProfileOnSystem(AgentValidationDto agentValidationDto);

    Mono<ResponseEntity> createUserForAgent(String agentId);
}
