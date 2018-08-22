package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.dto.ApplicationFormCreateDto;
import com.software.finatech.lslb.cms.service.dto.ApplicationFormDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionDto;
import com.software.finatech.lslb.cms.service.service.contracts.ApplicationFormService;
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

@Api(value = "Application Form", description = "For everything related to gaming operators application forms", tags = "")
@RestController
@RequestMapping("/api/v1/application-form")
public class ApplicationFormController {


    private ApplicationFormService applicationFormService;

    @Autowired
    public void setApplicationFormService(ApplicationFormService applicationFormService) {
        this.applicationFormService = applicationFormService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize",
            "sortType", "sortProperty", "institutionId", "applicationFormTypeId", "applicationFormStatusId"})
    @ApiOperation(value = "Get all Application Forms", response = ApplicationFormDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllApplicationForms(@RequestParam("page") int page,
                                                   @RequestParam("pageSize") int pageSize,
                                                   @RequestParam("sortType") String sortType,
                                                   @RequestParam("sortProperty") String sortParam,
                                                   @RequestParam("institutionId") String institutionId,
                                                   @RequestParam("applicationFormTypeId") String applicationFormTypeId,
                                                   @RequestParam("applicationFormStatusId") String applicationFormStatusId,
                                                   HttpServletResponse httpServletResponse) {
        return applicationFormService.findAllApplicationForm(page, pageSize, sortType, sortParam, institutionId, applicationFormTypeId, applicationFormStatusId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create an Application Form", response = ApplicationFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createApplicationForm(@RequestBody @Valid ApplicationFormCreateDto applicationFormCreateDto) {
        return applicationFormService.createApplicationForm(applicationFormCreateDto);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-application-form-types")
    @ApiOperation(value = "Get all application form types", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllApplicationFormTypes() {
        return applicationFormService.getAllApplicationFormTypes();
    }
}
