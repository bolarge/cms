package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.MachineApprovalRequestDto;
import com.software.finatech.lslb.cms.service.service.contracts.MachineApprovalRequestService;
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

@Api(value = "Machine Approval Requests",
        description = "For everything related to machine approval requests", tags = "Machine Approval Requests Controller")
@RestController
@RequestMapping("/api/v1/machine-approval-requests")
public class MachineApprovalRequestController  extends BaseController{

    private MachineApprovalRequestService machineApprovalRequestService;

    @Autowired
    public void setMachineApprovalRequestService(MachineApprovalRequestService machineApprovalRequestService) {
        this.machineApprovalRequestService = machineApprovalRequestService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty",
            "statusId", "approvalRequestTypeId", "initiatorId", "approverId", "approvalRequestTypeId", "rejectorId", "institutionId", "gamingMachineId", "gamingTerminalId", "startDate", "endDate"})
    @ApiOperation(value = "Get all Application Forms", response = MachineApprovalRequestDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllMachineApprovalRequests(@RequestParam("page") int page,
                                                              @RequestParam("pageSize") int pageSize,
                                                              @RequestParam("sortType") String sortType,
                                                              @RequestParam("sortProperty") String sortParam,
                                                              @RequestParam("statusId") String statusId,
                                                              @RequestParam("approvalRequestTypeId") String approvalRequestTypeId,
                                                              @RequestParam("initiatorId") String initiatorId,
                                                              @RequestParam("approverId") String approverId,
                                                              @RequestParam("rejectorId") String rejectorId,
                                                              @RequestParam("institutionId") String institutitonId,
                                                              @RequestParam("gamingMachineId") String gamingMachineId,
                                                              @RequestParam("gamineTerminalId") String gamingTerminalId,
                                                              @RequestParam("startDate") String startDate,
                                                              @RequestParam("endDate") String endDate,
                                                              @RequestParam("machineTypeId") String machineTypeId,
                                                              HttpServletResponse httpServletResponse) {
        return machineApprovalRequestService.findAllMachineApprovalRequests(page, pageSize, sortType,
                sortParam, statusId, approvalRequestTypeId, initiatorId,
                approverId, rejectorId, institutitonId, gamingMachineId, gamingTerminalId, startDate, endDate, machineTypeId, httpServletResponse);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/approve")
    @ApiOperation(value = "Approve a Machine approval request", response = MachineApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveRequest(@RequestBody @Valid ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        return machineApprovalRequestService.approveRequest(approvalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject")
    @ApiOperation(value = "Reject a machine approval request", response = MachineApprovalRequestDto.class, consumes = "application/json",
            notes = "for this request , you may provide reason for rejecting the request ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectRequest(@RequestBody @Valid ApprovalRequestOperationtDto agentApprovalRequestOperationtDto, HttpServletRequest request) {
        return machineApprovalRequestService.rejectRequest(agentApprovalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/all-machine-approval-request-types")
    @ApiOperation(value = "Get all agent approval request types", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllApprovalRequestTypes() {
        return machineApprovalRequestService.getAllMachineApprovalRequestType();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    @ApiOperation(value = "Get Machine approval request full detail", response = MachineApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApprovalRequestFullDetail(@PathVariable("id") String approvalRequestId) {
        return machineApprovalRequestService.getMachineApprovalRequestFullDetail(approvalRequestId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/approve-multiple")
    @ApiOperation(value = "Approve Multiple Machine approval request", response = MachineApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveMultipleRequest(@RequestBody  ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        return machineApprovalRequestService.approveMultipleRequest(approvalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject-multiple")
    @ApiOperation(value = "Reject Multiple Machine approval request", response = MachineApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectMultipleRequest(@RequestBody  ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        return machineApprovalRequestService.rejectMultipleRequest(approvalRequestOperationtDto, request);
    }

}
