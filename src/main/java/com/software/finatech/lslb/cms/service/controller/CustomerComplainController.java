package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.CustomerComplainService;
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

@Api(value = "Customer Complains", description = "For everything related to customer complains on th CMS platform", tags = "Customer Complain Controller")
@RestController
@RequestMapping("/api/v1/customer-complains")
public class CustomerComplainController  extends BaseController{

    private CustomerComplainService customerComplainService;

    @Autowired
    public void setCustomerComplainService(CustomerComplainService customerComplainService) {
        this.customerComplainService = customerComplainService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "customerEmail",
            "customerPhone", "statusId", "startDate", "endDate", "categoryId", "typeId"})
    @ApiOperation(value = "Get all customer complains", response = CustomerComplainDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllApplicationForms(@RequestParam("page") int page,
                                                       @RequestParam("pageSize") int pageSize,
                                                       @RequestParam("sortType") String sortType,
                                                       @RequestParam("sortProperty") String sortParam,
                                                       @RequestParam("customerEmail") String customerEmail,
                                                       @RequestParam("customerPhone") String customerPhone,
                                                       @RequestParam("statusId") String statusId,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate,
                                                       @RequestParam("categoryId") String categoryId,
                                                       @RequestParam("typeId") String typeId,
                                                       HttpServletResponse httpServletResponse) {
        return customerComplainService.findAllCustomerComplains(page, pageSize, sortType, sortParam, customerEmail, customerPhone, statusId, startDate, endDate, categoryId, typeId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create", produces = "application/json")
    @ApiOperation(value = "Create customer complain", response = CustomerComplainDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createNewCustomerComplain(@RequestBody @Valid CustomerComplainCreateDto customerComplainCreateDto, HttpServletRequest request) {
        return customerComplainService.createCustomerComplain(customerComplainCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/close", produces = "application/json")
    @ApiOperation(value = "Close customer complain", response = CustomerComplainDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> closeCustomerComplain(
            @RequestParam("complaintId") String complaintId, HttpServletRequest request) {
        return customerComplainService.closeCustomerComplain(complaintId, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    @ApiOperation(value = "Get customer complain full detail", response = CustomerComplainDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getCustomerComplain(@PathVariable("id") String complainId) {
        return customerComplainService.getCustomerComplainFullDetail(complainId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update", produces = "application/json")
    @ApiOperation(value = "Update Customer complain status", response = CustomerComplainDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateCustomerComplain(@RequestBody @Valid CustomerComplainUpdateDto customerComplainUpdateDto, HttpServletRequest request) {
        return customerComplainService.updateCustomerComplainStatus(customerComplainUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-customer-complain-status", produces = "application/json")
    @ApiOperation(value = "Get all customer complain status", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllCustomerComplainStatus() {
        return customerComplainService.getAllCustomerComplainStatus();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/start-review", produces = "application/json")
    @ApiOperation(value = "Start Customer Complain Review", response = CustomerComplainDto.class, consumes = "application/json",
            notes = "Supply the  category and type to proceed to review")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> beginCustomerComplainReview(CustomerComplainReviewRequest reviewRequest, HttpServletRequest request) {
        return customerComplainService.beginCustomerComplainReview(reviewRequest, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add-comment", produces = "application/json")
    @ApiOperation(value = "Add comment Customer Complain", response = CustomerComplainDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createNewCommentToCustomerComplain(@RequestBody @Valid CustomerComplainCommentCreateDto customerComplainCommentCreateDto, HttpServletRequest request) {
        return customerComplainService.addCustomerComplaintComment(customerComplainCommentCreateDto, request);
    }
}

