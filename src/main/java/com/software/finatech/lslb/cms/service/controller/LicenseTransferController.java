package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.LicenseTrannsferCreateRequest;
import com.software.finatech.lslb.cms.service.dto.LicenseTransferDto;
import com.software.finatech.lslb.cms.service.dto.LicenseTransferSetToInstitutionRequest;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseTransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "License Transfer", description = "For License Transfers ", tags = "License Transfer Controller")
@RestController
@RequestMapping("/api/v1/license-transfers")
public class LicenseTransferController  extends BaseController{
    private LicenseTransferService licenseTransferService;

    @Autowired
    public void setLicenseTransferService(LicenseTransferService licenseTransferService) {
        this.licenseTransferService = licenseTransferService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize",
            "sortType", "sortProperty", "fromInstitutionId", "toInstitutionId", "statusId", "gameTypeId"})
    @ApiOperation(value = "Get all License Transfers", response = LicenseTransferDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllLicenseTransfers(@RequestParam("page") int page,
                                                       @RequestParam("pageSize") int pageSize,
                                                       @RequestParam("sortType") String sortType,
                                                       @RequestParam("sortProperty") String sortParam,
                                                       @RequestParam("fromInstitutionId") String fromInstitutionId,
                                                       @RequestParam("toInstitutionId") String toInstitutionId,
                                                       @RequestParam("statusId") String statusId,
                                                       @RequestParam("gameTypeId") String gameTypeId,
                                                       HttpServletResponse httpServletResponse) {
        return licenseTransferService.findAllLicenseTransfers(page, pageSize, sortType, sortParam, fromInstitutionId, toInstitutionId, statusId, gameTypeId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create", produces = "application/json")
    @ApiOperation(value = "Create License Transfer", response = LicenseTransferDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createNewLicenseTransfer(@RequestBody @Valid LicenseTrannsferCreateRequest licenseTransferCreateRequest, HttpServletRequest request) {
        return licenseTransferService.createLicenseTransfer(licenseTransferCreateRequest, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/set-to-institution", produces = "application/json")
    @ApiOperation(value = "Set License Transfer to Receiving Institution", response = LicenseTransferDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> setToInstitution(@RequestBody @Valid LicenseTransferSetToInstitutionRequest setToInstitutionRequest, HttpServletRequest request) {
        return licenseTransferService.setToFromInstitution(setToInstitutionRequest, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/approve", produces = "application/json")
    @ApiOperation(value = "Approve License Transfer", response = LicenseTransferDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveLicenseTransfer(@RequestBody @Valid ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        return licenseTransferService.approveLicenseTransfer(approvalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject", produces = "application/json")
    @ApiOperation(value = "Reject License Transfer", response = LicenseTransferDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectLicenseTransfer(@RequestBody @Valid ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        return licenseTransferService.rejectLicenseTransfer(approvalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    @ApiOperation(value = "Get License Transfer Full Detail", response = LicenseTransferDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getFullDetail(@PathVariable("id") String id) {
        return licenseTransferService.getLicenseTransferFullDetail(id);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/all-license-transfer-status", produces = "application/json")
    @ApiOperation(value = "Get All License Transfer Status", response = LicenseTransferDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllLicenseTransferStatus() {
        return licenseTransferService.getAllLicenseTransferStatus();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-licence-transfer-for-payment", produces = "application/json", params = {"institutionId", "gameTypeId"})
    @ApiOperation(value = "Get All License Transfer For Payment", response = LicenseTransferDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllLicenseTransferForPayment(@RequestParam("institutionId") String institutionId,
                                                                @RequestParam("gameTypeId") String gameTypeId) {
        return licenseTransferService.getAllLicenseTransferForPayment(institutionId, gameTypeId);
    }
}
