package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.dto.LicenseUpdateDto;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
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
            "sortType", "sortProperty", "institutionId", "licenseStatusId","gameTypeId", "paymentRecordId"})
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
                                               @RequestParam("licenseStatusId") String licenseStatusId,
                                               @RequestParam("gameTypeId") String gameTypeId,
                                               @RequestParam("paymentRecordId") String paymentRecordId,
                                               HttpServletResponse httpServletResponse) {
        return licenseService.findAllLicense(page, pageSize, sortType, sortParam, institutionId, licenseStatusId, gameTypeId,paymentRecordId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-by-licenseId")
    @ApiOperation(value = "Get License by Id", response = LicenseDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getLicenseById(@PathVariable("licenseId") String licenseId) {
        return licenseService.findLicenseById(licenseId);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/get-by-institutionId")
    @ApiOperation(value = "Get License by InstitutionId", response = LicenseDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getLicenseByInstitutionId(@RequestParam("institutionId") String institutionId, @RequestParam("gameTypeId")String gameTypeId) {
        return licenseService.findLicenseByInstitutionId(institutionId, gameTypeId);
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
        return licenseService.getExpiredLicenses();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-expired-aips")
    @ApiOperation(value = "Get all Expired AIPs", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllExpiredAIPStatus() {
        return licenseService.getExpiredAIPs();
    }


    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update License", response = License.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateLicense(@RequestBody @Valid LicenseUpdateDto licenseUpdateDto) {
        return licenseService.updateLicense(licenseUpdateDto);
    }
}
