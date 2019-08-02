package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.PaymentConfirmationApprovalRequestDto;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentConfirmationApprovalRequestService;
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

@Api(value = "Payment Confirmation Approval Requests",
        description = "For everything related to payment confirmation approval requests", tags = "Payment Confirmation Approval Requests Controller")
@RestController
@RequestMapping("/api/v1/payment-confirmation-approval-requests")
public class PaymentConfirmationApprovalRequestController {
    private PaymentConfirmationApprovalRequestService approvalRequestService;

    @Autowired
    public void setApprovalRequestService(PaymentConfirmationApprovalRequestService approvalRequestService) {
        this.approvalRequestService = approvalRequestService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty",
            "statusId", "approvalRequestTypeId", "initiatorId", "approverId", "approvalRequestTypeId", "rejectorId", "paymentOwnerName", "startDate", "endDate"})
    @ApiOperation(value = "Get all payment confirmation approval requests", response = PaymentConfirmationApprovalRequestDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllApprovalRequests(@RequestParam("page") int page,
                                                       @RequestParam("pageSize") int pageSize,
                                                       @RequestParam("sortType") String sortType,
                                                       @RequestParam("sortProperty") String sortParam,
                                                       @RequestParam("statusId") String statusId,
                                                       @RequestParam("approvalRequestTypeId") String approvalRequestTypeId,
                                                       @RequestParam("initiatorId") String initiatorId,
                                                       @RequestParam("approverId") String approverId,
                                                       @RequestParam("rejectorId") String rejectorId,
                                                       @RequestParam("paymentOwnerName") String paymentOwnerName,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate,
                                                       HttpServletResponse httpServletResponse) {
        return approvalRequestService.findAllPaymentConfirmationApprovalRequests(page, pageSize, sortType, sortParam,
                statusId, approvalRequestTypeId, initiatorId, approverId, rejectorId, startDate, endDate, paymentOwnerName, httpServletResponse);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/approve")
    @ApiOperation(value = "Approve an approval request", response = PaymentConfirmationApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveRequest(@RequestBody @Valid ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        return approvalRequestService.approveRequest(approvalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject")
    @ApiOperation(value = "Reject a user approval request", response = PaymentConfirmationApprovalRequestDto.class, consumes = "application/json",
            notes = "for this request , you may provide reason for rejecting the request ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectRequest(@RequestBody @Valid ApprovalRequestOperationtDto agentApprovalRequestOperationtDto, HttpServletRequest request) {
        return approvalRequestService.rejectRequest(agentApprovalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/all-approval-request-types")
    @ApiOperation(value = "Get all payment confirmation approval request types", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllApprovalRequestTypes() {
        return approvalRequestService.getAllApprovalRequestType();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    @ApiOperation(value = "Get Payment confirmation approval request full detail", response = PaymentConfirmationApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApprovalRequestFullDetail(@PathVariable("id") String approvalRequestId) {
        return approvalRequestService.getPaymentConfirmationApprovalRequestFullDetail(approvalRequestId);
    }
}
