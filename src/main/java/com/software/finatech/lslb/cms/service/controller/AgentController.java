package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.AgentCreateDto;
import com.software.finatech.lslb.cms.service.dto.AgentDto;
import com.software.finatech.lslb.cms.service.service.contracts.AgentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "Agents", description = "For everything related to agents", tags = "")
@RestController
@RequestMapping("/api/v1/agents")
public class AgentController extends BaseController {

    private AgentService agentService;

    @Autowired
    public void setAgentService(AgentService agentService) {
        this.agentService = agentService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "institutionIds", "gameTypeIds"})
    @ApiOperation(value = "Get all agents", response = AgentDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllAgents(@RequestParam("page") int page,
                                             @RequestParam("pageSize") int pageSize,
                                             @RequestParam("sortType") String sortType,
                                             @RequestParam("sortProperty") String sortParam,
                                             @RequestParam("gameTypeIds") String gameTypeIds,
                                             @RequestParam("institutionIds") String institutionIds,
                                             HttpServletResponse httpServletResponse) {
        return agentService.findAllAgents(page, pageSize, sortType, sortParam, institutionIds, gameTypeIds, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create an Agent", response = AgentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createAgent(@RequestBody @Valid AgentCreateDto agentCreateDto) {
        return agentService.createAgent(agentCreateDto);
    }

}
