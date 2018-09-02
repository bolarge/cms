package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordUpdateDto;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
                                                   @RequestParam("feeId") String feeId,
                                                   @RequestParam("institutionId") String institutionId,
                                                   @RequestParam("approverId") String approverId,

                                                   HttpServletResponse httpServletResponse) {
        return paymentRecordService.findAllPaymentRecords(page, pageSize, sortType, sortParam, approverId, institutionId,  feeId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/by-institutionId-gameTypeId", params={"institutionId","agentId","gamingMachineId","gameTypeId","mostRecent"})
    @ApiOperation(value = "Get specific payment Status", response = EnumeratedFactDto.class,responseContainer = "List",consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getPaymentRecordsByInstitution(@RequestParam("institutionId") String institutionId,
                                                               @RequestParam("gameTypeId") String gameTypeId,
                                                               @RequestParam("agentId") String agentId,
                                                               @RequestParam("gamingMachineId") String gamingMachineId,
                                                               @RequestParam("mostRecent") String isMostRecent ) {
        boolean isMostRecentI;
        if(StringUtils.isEmpty(isMostRecent)){
            isMostRecentI=false;
        }else{
            isMostRecentI= Boolean.valueOf(isMostRecent);
        }
        if(StringUtils.isEmpty(institutionId)||StringUtils.isEmpty(agentId)||StringUtils.isEmpty(gamingMachineId)){
            return Mono.just(new ResponseEntity<>("Provide InstitutionId or agentId or Gaming Machine Id", HttpStatus.BAD_REQUEST));

        }
        List<PaymentRecord> paymentsRecords = paymentRecordService.findPayments(institutionId,agentId,gamingMachineId,gameTypeId,isMostRecentI);
        List<PaymentRecordDto> paymentRecordDtos= new ArrayList<>();
        paymentsRecords.stream().forEach(paymentRecord -> {
            paymentRecordDtos.add(paymentRecord.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(paymentRecordDtos, HttpStatus.OK));

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
    @ApiOperation(value = "Create new Payment Record", response = PaymentRecord.class, consumes = "application/json")
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
    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update Payment Status", response = PaymentRecord.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> updatePaymentRecord(@RequestBody @Valid PaymentRecordUpdateDto paymentRecordUpdateDto) {
        return paymentRecordService.updatePaymentRecord(paymentRecordUpdateDto);
    }
}
