package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.dto.FullPaymentConfirmationRequest;
import com.software.finatech.lslb.cms.service.dto.PartialPaymentConfirmationRequest;
import com.software.finatech.lslb.cms.service.dto.PaymentConfirmationApprovalRequestDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.service.OutsideSystemPaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(value = "Outside System Payments", description = "For initiating outside system payments", tags = "Outside System Payments Controller")
@RestController
@RequestMapping("/api/v1/outside-system-payments")
public class OutsideSystemPaymentsController {
    private OutsideSystemPaymentService outsideSystemPaymentService;

    @Autowired
    public void setOutsideSystemPaymentService(OutsideSystemPaymentService outsideSystemPaymentService) {
        this.outsideSystemPaymentService = outsideSystemPaymentService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/initiate-full-payment")
    @ApiOperation(value = "Initiate Full Payment", response = PaymentConfirmationApprovalRequestDto.class, consumes = "application/json",
            notes = "Used to create payments for payments that didnt begin on the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createNewFullPayment(@RequestBody FullPaymentConfirmationRequest fullPaymentConfirmationRequest, HttpServletRequest request) {
        return outsideSystemPaymentService.createFullPaymentConfirmationRequest(fullPaymentConfirmationRequest, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/initiate-partial-payment")
    @ApiOperation(value = "Initiate Partial Payment", response = PaymentConfirmationApprovalRequestDto.class, consumes = "application/json",
            notes = "Used to create payment confirmations for payments that began on the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createNewPartialPayment(@RequestBody PartialPaymentConfirmationRequest partialPaymentConfirmationRequest, HttpServletRequest request) {
        return outsideSystemPaymentService.createPartialPaymentConfirmationRequest(partialPaymentConfirmationRequest, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update-offline-payment")
    @ApiOperation(value = "Update an existing offline payment record detail", response = PaymentConfirmationApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> updateOfflinePaymentRecord(@RequestBody @Valid PartialPaymentConfirmationRequest partialPaymentConfirmationRequest, org.apache.catalina.servlet4preview.http.HttpServletRequest request) {
        return outsideSystemPaymentService.updateOfflinePaymentRecordDetail(partialPaymentConfirmationRequest, request);
    }
}
