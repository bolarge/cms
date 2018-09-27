package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.GamingMachineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "Gaming machines", description = "For everything related to gaming machines", tags = "")
@RestController
@RequestMapping("/api/v1/gaming-machines")
public class GamingMachineController {

    private GamingMachineService gamingMachineService;

    @Autowired
    public void setGamingMachineService(GamingMachineService gamingMachineService) {
        this.gamingMachineService = gamingMachineService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "institutionId"})
    @ApiOperation(value = "Get all gaming machines", response = GamingMachineDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllGamingMachines(@RequestParam("page") int page,
                                                     @RequestParam("pageSize") int pageSize,
                                                     @RequestParam("sortType") String sortType,
                                                     @RequestParam("sortProperty") String sortParam,
                                                     @RequestParam("institutionId") String institutionId,
                                                     HttpServletResponse httpServletResponse) {
        return gamingMachineService.findAllGamingMachines(page, pageSize, sortType, sortParam, institutionId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create an Gaming Machine", response = GamingMachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createGamingMachine(@RequestBody @Valid GamingMachineCreateDto gamingMachineCreateDto) {
        return gamingMachineService.createGamingMachine(gamingMachineCreateDto);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update a Gaming Machine", response = GamingMachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateGamingMachine(@RequestBody @Valid GamingMachineUpdateDto gamingMachineUpdateDto) {
        return gamingMachineService.updateGamingMachine(gamingMachineUpdateDto);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload-multiple", params = {"institutionId", "gameTypeId"})
    @ApiOperation(value = "Upload multiple gaming machines for institution", response = UploadTransactionResponse.class,
            notes = "User uploading the gaming machines must specify the category the machines are operating under via the request param \"gameTypeId\"" +
                    " and also supply the institution id via the request param \"institutionId\" ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server error(error occurred while parsing file)")})
    public Mono<ResponseEntity> uploadTransactionsFromCsv(@RequestParam("institutionId") String institutionId,
                                                          @RequestParam("gameTypeId") String gameTypeId,
                                                          @RequestParam("file") MultipartFile multipartFile) {
        return gamingMachineService.uploadMultipleGamingMachinesForInstitution(institutionId, gameTypeId, multipartFile);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/validate-multiple-license-payment")
    @ApiOperation(value = "Validate payment for license for multiple gaming machines", response = GamingMachineMultiplePaymentResponse.class,
            notes = "This returns the list of valid machines u can pay license for and list if invalid machines with the reason," +
                    " also it returns the total amount of the valid license payments")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public Mono<ResponseEntity> validateMultipleGamingMachineLicensePayment(@RequestBody GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest) {
        return gamingMachineService.validateMultipleGamingMachineLicensePayment(gamingMachineMultiplePaymentRequest);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/validate-multiple-license-renewal-payment")
    @ApiOperation(value = "Validate payment for license renewal for multiple gaming machines", response = GamingMachineMultiplePaymentResponse.class,
            notes = "This returns the list of valid machines u can pay license renewal for and list if invalid machines with the reason," +
                    " also it returns the total amount of the valid license renewal payments")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public Mono<ResponseEntity> validateMultipleGamingMachineLicenseRenewalPayment(@RequestBody GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest) {
        return gamingMachineService.validateMultipleGamingMachineLicenseRenewalPayment(gamingMachineMultiplePaymentRequest);
    }
}
