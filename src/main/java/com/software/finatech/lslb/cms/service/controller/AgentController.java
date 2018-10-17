package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.AgentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "Agents", description = "For everything related to agents",
        tags = "Agent Controller")
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
    public Mono<ResponseEntity> createAgent(@RequestBody @Valid AgentCreateDto agentCreateDto, HttpServletRequest request) {
        return agentService.createAgent(agentCreateDto, request);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update an Agent", response = AgentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateAgent(@RequestBody @Valid AgentUpdateDto agentUpdateDto, HttpServletRequest request) {
        return agentService.updateAgent(agentUpdateDto, request);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/institution/create-agent")
    @ApiOperation(value = "Create Agent for institution", response = AgentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createAgentForInstitution(@RequestBody @Valid AgentInstitutionCreateDto agentInstitutionCreateDto, HttpServletRequest request) {
        return agentService.createAgentUnderInstitution(agentInstitutionCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{agentId}")
    @ApiOperation(value = "Get Agent full detail by id", response = AgentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createAgentForInstitution(@PathVariable("agentId") String agentId) {
        return agentService.getAgentFullDetailById(agentId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/validate-agent-profile")
    @ApiOperation(value = "Validate Agent profile On System", response = AgentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> validateAgentProfileOnSystem(@RequestBody @Valid AgentValidationDto agentValidationDto) {
        return agentService.validateAgentProfileOnSystem(agentValidationDto);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search", params = {"searchKey"})
    @ApiOperation(value = "Search for agent on system", response = AgentDto.class,  responseContainer = "List",consumes = "application/json",
    notes = "Search for agent on system using a search key that matches either agent id or agent name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> searchAgents(@RequestParam("searchKey") String searchKey) {
        return agentService.findAgentsBySearchKey(searchKey);
    }
}
