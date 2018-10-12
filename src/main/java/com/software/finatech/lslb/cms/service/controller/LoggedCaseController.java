package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.LoggedCaseService;
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

@Api(value = "Logged Cases", description = "For everything related to logged cases by LSLB", tags = "Logged Cases Controller")
@RestController
@RequestMapping("/api/v1/logged-cases")
public class LoggedCaseController {

    private LoggedCaseService loggedCaseService;

    @Autowired
    public void setLoggedCaseService(LoggedCaseService loggedCaseService) {
        this.loggedCaseService = loggedCaseService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize",
            "sortType", "sortProperty", "institutionId", "agentId", "statusId", "reporterId", "startDate", "endDate"})
    @ApiOperation(value = "Get all logged cases", response = CustomerComplainDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllApplicationForms(@RequestParam("page") int page,
                                                       @RequestParam("pageSize") int pageSize,
                                                       @RequestParam("sortType") String sortType,
                                                       @RequestParam("sortProperty") String sortParam,
                                                       @RequestParam("reporterId") String reporterId,
                                                       @RequestParam("institutionId") String institutionId,
                                                       @RequestParam("agentId") String agentId,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate,
                                                       @RequestParam("statusId") String statusId,
                                                       HttpServletResponse httpServletResponse) {
        return loggedCaseService.findAllLoggedCases(page, pageSize, sortType, sortParam, reporterId, institutionId, statusId, agentId, startDate, endDate, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create", produces = "application/json")
    @ApiOperation(value = "Create Logged Case", response = LoggedCaseDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createNewLoggedCase(@RequestBody @Valid LoggedCaseCreateDto loggedCaseCreateDto, HttpServletRequest request) {
        return loggedCaseService.createCase(loggedCaseCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add-action", produces = "application/json")
    @ApiOperation(value = "Add action to logged case", response = LoggedCaseDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createNewActionToLoggedCase(@RequestBody @Valid LoggedCaseActionCreateDto loggedCaseActionCreateDto, HttpServletRequest request) {
        return loggedCaseService.addLoggedCaseAction(loggedCaseActionCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add-comment", produces = "application/json")
    @ApiOperation(value = "Add comment to logged case", response = LoggedCaseDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createNewCommentToLoggedCase(@RequestBody @Valid LoggedCaseCommentCreateDto loggedCaseCommentCreateDto, HttpServletRequest request) {
        return loggedCaseService.addLoggedCaseComment(loggedCaseCommentCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-logged-case-status", produces = "application/json")
    @ApiOperation(value = "Get all logged case status", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllLoggedCaseStatus() {
        return loggedCaseService.getAllLoggedCaseStatus();
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    @ApiOperation(value = "Get logged case full detail", response = LoggedCaseDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getLoggedCaseFullDetail(@PathVariable("id") String caseId) {
        return loggedCaseService.getLoggedCaseFullDetail(caseId);
    }
}
