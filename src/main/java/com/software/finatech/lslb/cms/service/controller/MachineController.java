package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.MachineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "Machines", description = "For everything related to Machines (Gaming terminals and Gaming Machines)", tags = "Machine Controller(Gaming Machines and Terminals)")
@RestController
@RequestMapping("/api/v1/machines")
public class MachineController {

    private MachineService machineService;

    @Autowired
    public void setMachineService(MachineService machineService) {
        this.machineService = machineService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "institutionId", "agentId", "machineTypeId"})
    @ApiOperation(value = "Get all gaming machines", response = MachineDto.class, responseContainer = "List", consumes = "application/json")
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
                                                     @RequestParam("agentId") String agentId,
                                                     @RequestParam("machineTypeId") String machineTypeId,
                                                     @RequestParam("machineStatusId") String machineStatusId,
                                                     HttpServletResponse httpServletResponse) {
        return machineService.findAllMachines(page, pageSize, sortType, sortParam, institutionId, agentId, machineTypeId,machineStatusId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create a Machine", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createMachine(@RequestBody @Valid MachineCreateDto machineCreateDto, HttpServletRequest request) {
        return machineService.createMachine(machineCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update a Machine", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateMachine(@RequestBody @Valid MachineUpdateDto machineUpdateDto, HttpServletRequest request) {
        return machineService.updateMachine(machineUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update-status")
    @ApiOperation(value = "Update a Machine Status", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateMachineStatus(@RequestBody @Valid MachineStatusUpdateDto statusUpdateDto, HttpServletRequest request) {
        return machineService.updateMachineStatus(statusUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload-multiple", params = {"institutionId", "gameTypeId"})
    @ApiOperation(value = "Upload multiple gaming machines for institution", response = UploadTransactionResponse.class,
            notes = "User uploading the gaming machines must specify the category the machines are operating under via the request param \"gameTypeId\"" +
                    " and also supply the institution id via the request param \"institutionId\" ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server error(error occurred while parsing file)")})
    public Mono<ResponseEntity> uploadMachinesFromCsv(@RequestParam("institutionId") String institutionId,
                                                      @RequestParam("gameTypeId") String gameTypeId,
                                                      @RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
        return machineService.uploadMultipleMachinesForInstitution(institutionId, gameTypeId, multipartFile, request);
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
        return machineService.validateMultipleGamingMachineLicensePayment(gamingMachineMultiplePaymentRequest);
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
        return machineService.validateMultipleGamingMachineLicenseRenewalPayment(gamingMachineMultiplePaymentRequest);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search", params = {"searchKey"})
    @ApiOperation(value = "Search for gaming machines on system", response = MachineDto.class, responseContainer = "List", consumes = "application/json",
            notes = "Search for agent on system using a search key that matches machine serial number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> searchGamingMachines(@RequestParam("searchKey") String searchKey) {
        return machineService.findMachineBySearchKey(searchKey);
    }
}
