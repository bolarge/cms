package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.FeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Api(value = "Fees", description = "For everything related to fees configurations", tags = "Fee Controller")
@RestController
@RequestMapping("/api/v1/fees")
public class FeeController extends BaseController {
    @Autowired
    public FeeService feeService;

    @Autowired
    public FeeService getFeeService() {
        return feeService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"feePaymentTypeId", "gameTypeId", "licenseTypeId"})
    @ApiOperation(value = "Get all Fees", response = FeeDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllFees(@RequestParam("feePaymentTypeId") String feePaymentTypeId,
                                           @RequestParam("gameTypeId") String gameTypeId,
                                           @RequestParam("licenseTypeId") String revenueNameId) {
        return feeService.getAllFees(feePaymentTypeId, gameTypeId, revenueNameId);
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
    public Mono<ResponseEntity> createFee(@RequestBody @Valid FeeCreateDto feeCreateDto, HttpServletRequest request) {
        return feeService.createFee(feeCreateDto, request);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/updateFeePaymentType")
    @ApiOperation(value = "Update FeePaymentType Configuration", response = FeePaymentTypeDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateFeePaymentType(@RequestBody @Valid FeePaymentTypeDto feeUpdateDto, HttpServletRequest request) {
        return feeService.updateFeePaymentType(feeUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createFeePayment")
    @ApiOperation(value = "Create Fee Payment Configuration", response = FeePaymentTypeDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createFee(@RequestBody @Valid FeePaymentTypeDto feeCreateDto, HttpServletRequest request) {
        return feeService.createFeePaymentType(feeCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-revenue-names")
    @ApiOperation(value = "Get all Revenue Names", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllRevenueNames() {
        try {
            List<EnumeratedFactDto> revenueNames = feeService.getLicenseTypes();
            if (revenueNames == null) {
                return Mono.just(new ResponseEntity<>("No Revenue Name Record", HttpStatus.OK));

            }
            return Mono.just(new ResponseEntity<>(revenueNames, HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-processing-fees")
    @ApiOperation(value = "Get all Processing Fees", response = FeesTypeDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllProcessingFees() {
        try {
            List<FeesTypeDto> feesTypeDtos = feeService.getAllFeesType();
            if (feesTypeDtos.size() == 0) {
                return Mono.just(new ResponseEntity<>("No Processing FeeType ", HttpStatus.OK));

            }
            return Mono.just(new ResponseEntity<>(feesTypeDtos, HttpStatus.OK));

        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/find-fee", params = {"feePaymentTypeId", "gameTypeId", "licenseTypeId"})
    @ApiOperation(value = "Get Fee with revenue name, gameType and Fee Payment Type", response = FeeDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> findFeeByParams(@RequestParam("feePaymentTypeId") String feePaymentTypeId,
                                                @RequestParam("gameTypeId") String gameTypeId,
                                                @RequestParam("licenseTypeId") String revenueNameId) {
        return feeService.findActiveFeeByGameTypeAndPaymentTypeAndRevenueName(gameTypeId, feePaymentTypeId, revenueNameId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/fee-payment-types/for-license-type", params = {"licenseTypeId"})
    @ApiOperation(value = "Get Fee Payment Types for Revenue name", response = EnumeratedFactDto.class, consumes = "application/json",
            notes = "This endpoint find all fee payment types for revenue name \n(Returns only License and License Renewal for agents and gaming machine, \n returns all fee payment types for institutions)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> findFeeByParams(@RequestParam("licenseTypeId") String revenueNameId) {
        return feeService.findAllFeePaymentTypeForLicenseType(revenueNameId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/revenue-names/by-params", params = {"institutionId", "agentId"})
    @ApiOperation(value = "Get all Revenue Names", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> findRevenueNamesByParam(@RequestParam("institutionId") String institutionId, @RequestParam("agentId") String agentId) {
        return feeService.findLicenseTypeByParams(institutionId, agentId);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/set-fee-end-date")
    @ApiOperation(value = "Set fee end date", response = FeeApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> findRevenueNamesByParam(@RequestBody FeeEndDateUpdateDto feeEndDateUpdateDto, HttpServletRequest request) {
        return feeService.setFeeEndDate(feeEndDateUpdateDto, request);
    }
}
