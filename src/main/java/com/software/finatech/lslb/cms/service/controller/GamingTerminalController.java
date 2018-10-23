package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.GamingTerminalService;
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

@Api(value = "Gaming terminals", description = "For everything related to gaming terminals", tags = "Gaming Terminal Controller")
@RestController
@RequestMapping("/api/v1/gaming-terminals")
public class GamingTerminalController {

    private GamingTerminalService gamingTerminalService;

    @Autowired
    public void setGamingTerminalService(GamingTerminalService gamingTerminalService) {
        this.gamingTerminalService = gamingTerminalService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "institutionId"})
    @ApiOperation(value = "Get all gaming terminals", response = GamingTerminalDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllGamingTerminals(@RequestParam("page") int page,
                                                     @RequestParam("pageSize") int pageSize,
                                                     @RequestParam("sortType") String sortType,
                                                     @RequestParam("sortProperty") String sortParam,
                                                     @RequestParam("institutionId") String institutionId,
                                                     HttpServletResponse httpServletResponse) {
        return gamingTerminalService.findAllGamingTerminals(page, pageSize, sortType, sortParam, institutionId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create an Gaming Terminal", response = GamingTerminalDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createGamingTerminal(@RequestBody @Valid GamingTerminalCreateDto gamingTerminalCreateDto, HttpServletRequest request) {
        return gamingTerminalService.createGamingTerminal(gamingTerminalCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update a Gaming Terminal", response = GamingTerminalDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateGamingTerminal(@RequestBody @Valid GamingTerminalUpdateDto gamingTerminalUpdateDto, HttpServletRequest request) {
        return gamingTerminalService.updateGamingTerminal(gamingTerminalUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/assign-gaming-terminals")
    @ApiOperation(value = "Assign Gaming Terminals to Agent", response = GamingTerminalDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateGamingTerminal(@RequestBody @Valid AgentGamingTeriminalsUpdateDto agentGamingTeriminalsUpdateDto, HttpServletRequest request) {
        return gamingTerminalService.assignGamingTerminals(agentGamingTeriminalsUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload-multiple", params = {"institutionId", "gameTypeId"})
    @ApiOperation(value = "Upload multiple gaming terminals for institution", response = UploadTransactionResponse.class,
            notes = "User uploading the gaming terminals must specify the category the terminals are operating under via the request param \"gameTypeId\"" +
                    " and also supply the institution id via the request param \"institutionId\" ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server error(error occurred while parsing file)")})
    public Mono<ResponseEntity> uploadTerminalsFromCsv(@RequestParam("institutionId") String institutionId,
                                                      @RequestParam("gameTypeId") String gameTypeId,
                                                      @RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
        return gamingTerminalService.uploadMultipleGamingTerminalsForInstitution(institutionId, gameTypeId, multipartFile, request);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/validate-multiple-tax-payment")
    @ApiOperation(value = "Validate payment for license for multiple gaming terminals", response = GamingTerminalMultiplePaymentResponse.class,
            notes = "This returns the list of valid terminals u can pay license for and list if invalid terminals with the reason," +
                    " also it returns the total amount of the valid license payments")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public Mono<ResponseEntity> validateMultipleGamingTerminalLicensePayment(@RequestBody GamingTerminalMultiplePaymentRequest gamingTerminalMultiplePaymentRequest) {
        return gamingTerminalService.validateMultipleGamingTerminalTaxPayment(gamingTerminalMultiplePaymentRequest);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/validate-multiple-license-renewal-payment")
    @ApiOperation(value = "Validate payment for license renewal for multiple gaming terminals", response = GamingTerminalMultiplePaymentResponse.class,
            notes = "This returns the list of valid terminals u can pay license renewal for and list if invalid terminals with the reason," +
                    " also it returns the total amount of the valid license renewal payments")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server error")})
    public Mono<ResponseEntity> validateMultipleGamingTerminalLicenseRenewalPayment(@RequestBody GamingTerminalMultiplePaymentRequest gamingTerminalMultiplePaymentRequest) {
        return gamingTerminalService.validateMultipleGamingTerminalTaxRenewalPayment(gamingTerminalMultiplePaymentRequest);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search", params = {"searchKey"})
    @ApiOperation(value = "Search for gaming terminals on system", response = GamingTerminalDto.class, responseContainer = "List", consumes = "application/json",
            notes = "Search for agent on system using a search key that matches terminal serial number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> searchGamingTerminals(@RequestParam("searchKey") String searchKey) {
        return gamingTerminalService.findGamingTerminalBySearchKey(searchKey);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-unassigned-terminal", params = {"institutionId"})
    @ApiOperation(value = "Search for institution Unassigned Gaming Terminals", response = GamingTerminalDto.class, responseContainer = "List", consumes = "application/json",
            notes = "Search for Unassigned Gaming Terminals")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> unassignedGamingTerminals(@RequestParam("institutionId") String institutionId) {
        return gamingTerminalService.getUnAssignedInstitutionTerminals(institutionId);
    }
}
