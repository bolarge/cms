package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordDetailService;
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

import javax.validation.Valid;

@Api(value = "Payment Record Details", description = "For everything related to payment record details", tags = "")
@RestController
@RequestMapping("/api/v1/payment-record-details")
public class PaymentRecordDetailController extends BaseController {

    private PaymentRecordDetailService paymentRecordDetailService;

    @Autowired
    public void setPaymentRecordDetailService(PaymentRecordDetailService paymentRecordDetailService) {
        this.paymentRecordDetailService = paymentRecordDetailService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create/in-branch")
    @ApiOperation(value = "Create a new payment record detail in branch payment", response = PaymentRecordDetailDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createPaymentRecordDetailInBranch(@RequestBody @Valid PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        return paymentRecordDetailService.createInBranchPaymentRecordDetail(paymentRecordDetailCreateDto);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/create/web-payment")
    @ApiOperation(value = "Create a new payment record detail web payment", response = PaymentRecordDetailDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createPaymentRecordDetailWebPayment(@RequestBody @Valid PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        return paymentRecordDetailService.createWebPaymentPaymentRecordDetail(paymentRecordDetailCreateDto);
    }



    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update an existing payment record detail", response = PaymentRecordDetailDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> updatePaymentRecord(@RequestBody @Valid PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto) {
        return paymentRecordDetailService.updatePaymentRecordDetail(paymentRecordDetailUpdateDto);
    }
}
