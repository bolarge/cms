package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.dto.LicenseUpdateAIPToLicenseDto;
import com.software.finatech.lslb.cms.service.dto.LicenseUpdateDto;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "License", description = "For everything related to gaming operators licenses", tags = "")
@RestController
@RequestMapping("/api/v1/license")
public class LicenseController {
@Autowired
    private LicenseService licenseService;

    @Autowired
    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize",
            "sortType", "sortProperty", "institutionId","gamingMachineId","agentId", "licenseStatusId","gameTypeId", "paymentRecordId","licenseType"})
    @ApiOperation(value = "Get all licenses", response = LicenseDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllLicenses(@RequestParam("page") int page,
                                               @RequestParam("pageSize") int pageSize,
                                               @RequestParam("sortType") String sortType,
                                               @RequestParam("sortProperty") String sortParam,
                                               @RequestParam("institutionId") String institutionId,
                                               @RequestParam("agentId") String agentId,
                                               @RequestParam("licenseType") String licenseType,
                                               @RequestParam("gamingMachineId") String gamingMachineId,
                                               @RequestParam("licenseStatusId") String licenseStatusId,
                                               @RequestParam("gameTypeId") String gameTypeId,
                                               @RequestParam("paymentRecordId") String paymentRecordId,
                                               HttpServletResponse httpServletResponse) {
        return licenseService.findAllLicense(page, pageSize, sortType, sortParam, institutionId,agentId,gamingMachineId, licenseStatusId, gameTypeId,paymentRecordId,licenseType, httpServletResponse);
    }



    @RequestMapping(method = RequestMethod.GET, value = "/get-license")
    @ApiOperation(value = "Get License by InstitutionId, agentId, gamingMachineId and gameType", response = LicenseDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getLicense(
            @RequestParam("institutionId") String institutionId,
            @RequestParam("agentId") String agentId,
            @RequestParam("licensedId") String licensedId,
            @RequestParam("gamingMachineId") String gamingMachineId,
            @RequestParam("gameTypeId")String gameTypeId) {
        return licenseService.findLicense(licensedId,institutionId, agentId, gamingMachineId,gameTypeId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-license-status")
    @ApiOperation(value = "Get all license status", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllLicenseStatus() {
        return licenseService.getAllLicenseStatus();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-expiring-licenses")
    @ApiOperation(value = "Get all Expiring license", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllExpiringLicenseStatus() {
        return licenseService.getExpiringLicenses();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-expiring-aips")
    @ApiOperation(value = "Get all Expiring AIPs", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllExpiringAIPStatus() {
        return licenseService.getExpiringAIPs();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-expired-licenses")
    @ApiOperation(value = "Get all Expired license", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllExpiredLicenseStatus() {
        try{return licenseService.getExpiredLicenses();}
        catch (Exception ex){
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-expired-aips")
    @ApiOperation(value = "Get all Expired AIPs", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllExpiredAIPStatus() {
        try{return licenseService.getExpiredAIPs();}catch (Exception ex){
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }
    }@RequestMapping(method = RequestMethod.GET, value = "/get-institution-aips", params={"institutionId"})
    @ApiOperation(value = "Get all Institution AIPs", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllAIPStatus(@RequestParam("institutionId")String institutionId) {
        try{return licenseService.getInstitutionAIPs(institutionId);}catch (Exception ex){
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update License", response = License.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateLicense(@RequestBody @Valid LicenseUpdateDto licenseUpdateDto) {
        try {return licenseService.updateLicense(licenseUpdateDto);}
        catch (Exception ex){
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/update-to-aip-document", params={"licensedId"})
    @ApiOperation(value = "Update AIP to AIP Document Upload Status", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateLicenseToAIP(@RequestParam("licensedId") String licensedId) {
        try {return licenseService.updateToDocumentAIP(licensedId);}
        catch (Exception ex){
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }
    }
    @RequestMapping(method = RequestMethod.POST, value = "/update-aipdoc-to-license")
    @ApiOperation(value = "Update AIP to AIP Document Upload Status", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateAIPToLicense(@RequestBody @Valid LicenseUpdateAIPToLicenseDto licenseUpdateAIPToLicenseDto) {
        try {
            return licenseService.updateAIPDocToLicense(licenseUpdateAIPToLicenseDto);}
        catch (Exception ex){
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }
    }
}
