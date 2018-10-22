package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.dto.AgentApprovalRequestDto;
import com.software.finatech.lslb.cms.service.dto.ApplicationFormDto;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.service.contracts.AgentApprovalRequestService;
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

@Api(value = "Agent Approval Requests",
        description = "For everything related to agent approval requests", tags = "Agent Approval Requests Controller")
@RestController
@RequestMapping("/api/v1/agent-approval-requests")
public class AgentApprovalRequestController {

    private AgentApprovalRequestService agentApprovalRequestService;

    @Autowired
    public void setAgentApprovalRequestService(AgentApprovalRequestService agentApprovalRequestService) {
        this.agentApprovalRequestService = agentApprovalRequestService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty",
            "institutionId", "approvalStatusId", "gameTypeId", "agentId", "agentApprovalRequestTypeId", "approverId", "rejectorId", "startDate", "endDate"})
    @ApiOperation(value = "Get all Application Forms", response = ApplicationFormDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllAgentApprovalRequests(@RequestParam("page") int page,
                                                       @RequestParam("pageSize") int pageSize,
                                                       @RequestParam("sortType") String sortType,
                                                       @RequestParam("sortProperty") String sortParam,
                                                       @RequestParam("institutionId") String institutionId,
                                                       @RequestParam("agentId") String agentId,
                                                       @RequestParam("approverId") String approverId,
                                                       @RequestParam("gameTypeId") String gameTypeId,
                                                       @RequestParam("rejectorId") String rejectorId,
                                                       @RequestParam("approvalStatusId") String approvalStatusId,
                                                       @RequestParam("agentApprovalRequestTypeId") String agentApprovalRequestTypeId,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate,
                                                       HttpServletResponse httpServletResponse) {
        return agentApprovalRequestService.findAllAgentApprovalRequests(page, pageSize, sortType, sortParam, institutionId, agentId, approverId, gameTypeId, rejectorId, agentApprovalRequestTypeId, approvalStatusId, startDate, endDate, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/approve")
    @ApiOperation(value = "Approve an agent approval request", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveRequest(@RequestBody @Valid ApprovalRequestOperationtDto agentApprovalRequestOperationtDto, HttpServletRequest request) {
        return agentApprovalRequestService.approveRequest(agentApprovalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject")
    @ApiOperation(value = "Reject an agent approval request", response = String.class, consumes = "application/json",
            notes = "for this request please make sure you provide the reason ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectAgentApprovalRequest(@RequestBody @Valid ApprovalRequestOperationtDto agentApprovalRequestOperationtDto, HttpServletRequest request) {
        return agentApprovalRequestService.rejectRequest(agentApprovalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-agent-approval-request-types")
    @ApiOperation(value = "Get all agent approval request types", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllAgentApprovalRequestTypes() {
        return agentApprovalRequestService.getAllAgentApprovalRequestType();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-approval-request-status")
    @ApiOperation(value = "Get all agent approval request statuses", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllAgentApprovalRequestStatuses() {
        return agentApprovalRequestService.getAllApprovalRequestStatus();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ApiOperation(value = "Get Agent approval request full detail", response = AgentApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllAgentApprovalRequestStatuses(@PathVariable("id") String agentApprovalRequestId) {
        return agentApprovalRequestService.getAgentApprovalRequestFullDetail(agentApprovalRequestId);
    }

}
