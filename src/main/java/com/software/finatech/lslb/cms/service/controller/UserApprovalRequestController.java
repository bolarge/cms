package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.UserApprovalRequestDto;
import com.software.finatech.lslb.cms.service.service.contracts.UserApprovalRequestService;
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

@Api(value = "User Approval Requests",
        description = "For everything related to user approval requests", tags = "User Approval Requests Controller")
@RestController
@RequestMapping("/api/v1/user-approval-requests")
public class UserApprovalRequestController {

    private UserApprovalRequestService userApprovalRequestService;

    @Autowired
    public void setUserApprovalRequestService(UserApprovalRequestService userApprovalRequestService) {
        this.userApprovalRequestService = userApprovalRequestService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty",
            "statusId", "approvalRequestTypeId", "initiatorId", "approverId", "approvalRequestTypeId", "rejectorId", "userId", "startDate", "endDate"})
    @ApiOperation(value = "Get all user approval requests", response = UserApprovalRequestDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllUserApprovalRequestsForms(@RequestParam("page") int page,
                                                       @RequestParam("pageSize") int pageSize,
                                                       @RequestParam("sortType") String sortType,
                                                       @RequestParam("sortProperty") String sortParam,
                                                       @RequestParam("statusId") String statusId,
                                                       @RequestParam("approvalRequestTypeId") String approvalRequestTypeId,
                                                       @RequestParam("initiatorId") String initiatorId,
                                                       @RequestParam("approverId") String approverId,
                                                       @RequestParam("rejectorId") String rejectorId,
                                                       @RequestParam("userId") String userId,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate,
                                                       HttpServletResponse httpServletResponse) {
        return userApprovalRequestService.findAllUserApprovalRequests(page, pageSize, sortType, sortParam, statusId, approvalRequestTypeId, initiatorId, approverId, rejectorId, userId, startDate,endDate, httpServletResponse);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/approve")
    @ApiOperation(value = "Approve an user approval request", response = UserApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveRequest(@RequestBody @Valid ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        return userApprovalRequestService.approveRequest(approvalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject")
    @ApiOperation(value = "Reject a user approval request", response = UserApprovalRequestDto.class, consumes = "application/json",
            notes = "for this request , you may provide reason for rejecting the request ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectRequest(@RequestBody @Valid ApprovalRequestOperationtDto agentApprovalRequestOperationtDto, HttpServletRequest request) {
        return userApprovalRequestService.rejectRequest(agentApprovalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/all-user-approval-request-types")
    @ApiOperation(value = "Get all agent approval request types", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllApprovalRequestTypes() {
        return userApprovalRequestService.getAllUserApprovalRequestType();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    @ApiOperation(value = "Get User approval request full detail", response = UserApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApprovalRequestFullDetail(@PathVariable("id") String approvalRequestId) {
        return userApprovalRequestService.getUserApprovalRequestFullDetail(approvalRequestId);
    }
}
