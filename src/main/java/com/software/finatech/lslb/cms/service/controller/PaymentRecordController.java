package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordService;
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

@Api(value = "Payment Records", description = "For everything related to payment records", tags = "")
@RestController
@RequestMapping("/api/v1/payment-records")
public class PaymentRecordController extends BaseController {
    @Autowired
    private PaymentRecordService paymentRecordService;

    @Autowired
    public void setPaymentRecordService(PaymentRecordService paymentRecordService) {
        this.paymentRecordService = paymentRecordService;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "gameTypeIds", "approverId"})
    @ApiOperation(value = "Get all payment records", response = PaymentRecordDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllPaymentRecords(@RequestParam("page") int page,
                                                   @RequestParam("pageSize") int pageSize,
                                                   @RequestParam("sortType") String sortType,
                                                   @RequestParam("sortProperty") String sortParam,
                                                   @RequestParam("feePaymentTypeId") String feePaymentTypeId,
                                                   @RequestParam("institutionId") String institutionId,
                                                   @RequestParam("approverId") String approverId,

                                                   HttpServletResponse httpServletResponse) {
        return paymentRecordService.findAllPaymentRecords(page, pageSize, sortType, sortParam, approverId, institutionId,  feePaymentTypeId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-payment-status")
    @ApiOperation(value = "Get all payment Status", response = EnumeratedFactDto.class,responseContainer = "List",consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllPaymentStatus() {
        return paymentRecordService.getAllPaymentStatus();
    }



    @RequestMapping(method = RequestMethod.POST, value = "/new")
    @ApiOperation(value = "Create new Payment Record", response = PaymentRecordCreateDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createPaymentRecord(@RequestBody @Valid PaymentRecordCreateDto paymentRecordCreateDto) {
        return paymentRecordService.createPaymentRecord(paymentRecordCreateDto);
    }
}
