package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.FeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Api(value = "Fees", description = "For everything related to fees configurations", tags = "")
@RestController
@RequestMapping("/api/v1/fees")
public class FeeController extends BaseController {
    //private FeeService feeService;
    @Autowired
    public FeeService feeService;

    @Autowired
    public FeeService getFeeService() {
        return feeService;
    }

    public void setFeeService(FeeService feeService) {
        this.feeService = feeService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"feePaymentTypeId", "gameTypeId"})
    @ApiOperation(value = "Get all Fees", response = FeeDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllFees(@RequestParam("feePaymentTypeId") String feePaymentTypeId, @RequestParam("gameTypeId") String gameTypeId) {
        return feeService.getAllFees(feePaymentTypeId, gameTypeId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-fee-payment-types")
    @ApiOperation(value = "Get all Fee payment types", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllFeePaymentTypes() {
        return feeService.getAllFeePaymentType();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create Fee Configuration", response = FeeDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createFee(@RequestBody @Valid FeeCreateDto feeCreateDto) {
        return feeService.createFee(feeCreateDto);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update Fee Configuration", response = FeeDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateFee(@RequestBody @Valid FeeUpdateDto feeUpdateDto) {
        return feeService.updateFee(feeUpdateDto);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/updateFeePaymentType")
    @ApiOperation(value = "Update FeePaymentType Configuration", response = FeePaymentTypeDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateFeePaymentType(@RequestBody @Valid FeePaymentTypeDto feeUpdateDto) {
        return feeService.updateFeePaymentType(feeUpdateDto);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/createFeePayment")
    @ApiOperation(value = "Create Fee Payment Configuration", response = FeePaymentTypeDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createFee(@RequestBody @Valid FeePaymentTypeDto feeCreateDto) {
        return feeService.createFeePaymentType(feeCreateDto);
    }
}
