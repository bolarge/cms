package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.dto.AgentCreateDto;
import com.software.finatech.lslb.cms.service.dto.AgentInstitutionCreateDto;
import com.software.finatech.lslb.cms.service.dto.AgentUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

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

    Mono<ResponseEntity> createAgent(AgentCreateDto agentCreateDto);

    Mono<ResponseEntity> updateAgent(AgentUpdateDto agentUpdateDto);

    Agent findById(String agentId);

    List<Agent> getAllUncreatedOnVGPay();

    void saveAgent(Agent agent);

    Mono<ResponseEntity> createAgentUnderInstitution(AgentInstitutionCreateDto agentInstitutionCreateDto);

    Mono<ResponseEntity> getAgentFullDetailById(String agentId);
}
