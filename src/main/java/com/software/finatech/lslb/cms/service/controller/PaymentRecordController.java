package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

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


    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "agentId",
            "institutionId", "gamingMachineId", "gameTypeId","feePaymentTypeId", "revenueNameId", "paymentStatusId"})
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
                                                     @RequestParam("gamingMachineId") String gamingMachineId,
                                                     @RequestParam("gameTypeId") String gameTypeId,
                                                     @RequestParam("institutionId") String institutionId,
                                                     @RequestParam("feePaymentTypeId") String feePaymentTypeId,
                                                     @RequestParam("revenueNameId") String revenueNameId,
                                                     @RequestParam("agentId") String agentId,
                                                     @RequestParam("paymentStatusId") String paymentStatusId,
                                                     HttpServletResponse httpServletResponse) {
        try {
            return paymentRecordService.findAllPaymentRecords(page, pageSize, sortType, sortParam, institutionId,
                    agentId, gamingMachineId, gameTypeId, feePaymentTypeId, revenueNameId,paymentStatusId, httpServletResponse);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }
    }



    @RequestMapping(method = RequestMethod.GET, value = "/all-payment-status")
    @ApiOperation(value = "Get all payment Status", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllPaymentStatus() {
        try {
            return paymentRecordService.getAllPaymentStatus();
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));
        }
    }
}
