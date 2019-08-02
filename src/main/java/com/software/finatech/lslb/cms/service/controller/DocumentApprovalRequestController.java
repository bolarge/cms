package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.DocumentApprovalRequestDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.service.contracts.DocumentApprovalRequestService;
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

@Api(value = "Document Approval Requests",
        description = "For everything related to document approval requests", tags = "Document Approval Requests Controller")
@RestController
@RequestMapping("/api/v1/document-approval-requests")
public class DocumentApprovalRequestController  extends BaseController{

    private DocumentApprovalRequestService documentApprovalRequestService;

    @Autowired
    public void setDocumentApprovalRequestService(DocumentApprovalRequestService documentApprovalRequestService) {
        this.documentApprovalRequestService = documentApprovalRequestService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty",
            "statusId", "approvalRequestTypeId", "initiatorId", "approverId", "approvalRequestTypeId", "rejectorId", "startDate", "endDate"})
    @ApiOperation(value = "Get all Application Forms", response = DocumentApprovalRequestDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllDocumentApprovalRequests(@RequestParam("page") int page,
                                                       @RequestParam("pageSize") int pageSize,
                                                       @RequestParam("sortType") String sortType,
                                                       @RequestParam("sortProperty") String sortParam,
                                                       @RequestParam("statusId") String statusId,
                                                       @RequestParam("approvalRequestTypeId") String approvalRequestTypeId,
                                                       @RequestParam("initiatorId") String initiatorId,
                                                       @RequestParam("approverId") String approverId,
                                                       @RequestParam("rejectorId") String rejectorId,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate,
                                                       HttpServletResponse httpServletResponse) {
        return documentApprovalRequestService.findAllDocumentApprovalRequests(page, pageSize, sortType, sortParam, statusId, approvalRequestTypeId, initiatorId, approverId, rejectorId, startDate, endDate, httpServletResponse);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/approve")
    @ApiOperation(value = "Approve approval request", response = DocumentApprovalRequestDto.class, consumes = "application/json", tags = "Approve Request")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveRequest(@RequestBody @Valid ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        return documentApprovalRequestService.approveRequest(approvalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject")
    @ApiOperation(value = "Reject a user approval request", response = DocumentApprovalRequestDto.class, consumes = "application/json",
            notes = "for this request , you may provide reason for rejecting the request ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectRequest(@RequestBody @Valid ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        return documentApprovalRequestService.rejectRequest(approvalRequestOperationtDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/all-document-approval-request-types")
    @ApiOperation(value = "Get all document approval request types", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllApprovalRequestTypes() {
        return documentApprovalRequestService.getAllDocumentApprovalRequestType();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    @ApiOperation(value = "Get approval request full detail", response = DocumentApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApprovalRequestFullDetail(@PathVariable("id") String approvalRequestId) {
        return documentApprovalRequestService.getDocumentApprovalRequestFullDetail(approvalRequestId);
    }
}
