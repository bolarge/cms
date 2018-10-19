package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.FeeApprovalRequestDto;
import com.software.finatech.lslb.cms.service.service.contracts.FeeApprovalRequestService;
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

@Api(value = "Fee Approval Requests", description = "For everything related to fee approval Requests", tags = "Fee Approval Requests Controller")
@RestController
@RequestMapping("/api/v1/fee-approval-requests")
public class FeeApprovalRequestController extends BaseController {

    private FeeApprovalRequestService feeApprovalRequestService;

    @Autowired
    public void setFeeApprovalRequestService(FeeApprovalRequestService feeApprovalRequestService) {
        this.feeApprovalRequestService = feeApprovalRequestService;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty",
            "statusId", "approvalRequestTypeId", "initiatorId", "approverId", "rejectorId", "startDate", "endDate"})
    @ApiOperation(value = "Get all Application Forms", response = FeeApprovalRequestDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllFeeApprovalRequests(@RequestParam("page") int page,
                                                          @RequestParam("pageSize") int pageSize,
                                                          @RequestParam("sortType") String sortType,
                                                          @RequestParam("sortProperty") String sortParam,
                                                          @RequestParam("statusId") String statusId,
                                                          @RequestParam("approvalRequestTypeId") String approvalRequestTypeId,
                                                          @RequestParam("initiatorId") String initiatorId,
                                                          @RequestParam("approverId") String approverId,
                                                          @RequestParam("rejectorId") String rejectorId,
                                                          @RequestParam("startDate") String startDate,
                                                          @RequestParam("endDate") String endDate,
                                                          HttpServletResponse httpServletResponse) {
        return feeApprovalRequestService.findAllFeeApprovalRequests(page, pageSize, sortType, sortParam, statusId, approvalRequestTypeId, initiatorId, approverId, rejectorId, startDate, endDate, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/approve")
    @ApiOperation(value = "Approve a fee approval request", response = FeeApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveRequest(@RequestParam("approvalRequestId") String approvalRequestId, HttpServletRequest request) {
        return feeApprovalRequestService.approveFeeApprovalRequest(approvalRequestId, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject")
    @ApiOperation(value = "Reject a fee approval request", response = FeeApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectRequest(@RequestParam("approvalRequestId") String approvalRequestId, HttpServletRequest request) {
        return feeApprovalRequestService.rejectFeeApprovalRequest(approvalRequestId, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    @ApiOperation(value = "Get Fee approval request full dto", response = FeeApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getFullDetail(@PathVariable("id") String id) {
        return feeApprovalRequestService.getFeeApprovalRequestFullDetail(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/all-approval-request-types")
    @ApiOperation(value = "Get all Fee approval request types", response = EnumeratedFactDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllRequestTypes() {
        return feeApprovalRequestService.getFeeApprovalRequestTypes();
    }
}