package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.RenewalFormStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.RenewalFormServiceImpl;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Api(value = "Renewal Form", description = "", tags = "Renewal Form Controller")
@RestController
@RequestMapping("/api/v1/renewalForm")
public class RenewalFormController extends BaseController {
    @Autowired
    private AuditLogHelper auditLogHelper;
    @Autowired
    protected SpringSecurityAuditorAware springSecurityAuditorAware;

    private RenewalFormServiceImpl renewalFormService;

    @Autowired
    public void setApplicationFormService(RenewalFormServiceImpl renewalFormService) {
        this.renewalFormService = renewalFormService;
    }
    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "gameTypeIds", "institutionId","formStatusId"})
    @ApiOperation(value = "Get all Renewal Form", response = RenewalFormDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> getAllRenewalForms(
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("sortType") String sortType,
            @RequestParam("sortProperty") String sortParam,
            @RequestParam("institutionId") String institutionId,
            @RequestParam("formStatusId") String formStatusId,
            @RequestParam("gameTypeIds") String gameTypeIds,
            HttpServletResponse httpServletResponse) {
       return renewalFormService.getAllRenewalForms(page, pageSize, sortType, sortParam, institutionId, formStatusId, gameTypeIds,
        httpServletResponse);

    }

    @RequestMapping(method = RequestMethod.POST, value = "/new")
    @ApiOperation(value = "Create new Renewal Form", response = RenewalFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createRenewalForm(@RequestBody @Valid RenewalFormCreateDto renewalFormCreateDto) {
       return renewalFormService.createRenewalForm(renewalFormCreateDto);

    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Updates an Renewal Form", response = GameType.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> updateRenewalForm(@RequestBody @Valid RenewalFormUpdateDto renewalFormUpdateDto) {
        return renewalFormService.updateRenewalForm(renewalFormUpdateDto);

    }
    @RequestMapping(method = RequestMethod.POST, value = "/approve-renewal-form", params = {"renewalId","userId"})
    @ApiOperation(value = "Approve Renewal form", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveAIPForm(@RequestParam("renewalId") String renewalId,
                                               @RequestParam("userId") String approverId,HttpServletRequest request) {
        return renewalFormService.approveRenewalForm(renewalId,approverId, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create-comment", params = {"renewalId"})
    @ApiOperation(value = "Create comment for AIP form from LSLB Admin", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createAIPFormComment(@RequestParam("renewalId") String renewalId,
                                                     @RequestBody @Valid FormCreateCommentDto formCreateCommentDto, HttpServletRequest request) {
        return renewalFormService.addCommentsToFormFromLslbAdmin(renewalId, formCreateCommentDto, request);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/complete-renewal-form", params = {"renewalId"})
    @ApiOperation(value = "complete filling renewal form", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> completeApplicationForm(@RequestParam("renewalId") String renewalId, @RequestParam("isResubmit") boolean isResubmit, HttpServletRequest request) {
        return renewalFormService.completeRenewalForm(renewalId, isResubmit, request);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/reject-renewal-form", params = {"renewalFormId"})
    @ApiOperation(value = "Reject renewal application form", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectRenewalForm(@RequestParam("renewalFormId") String renewalFormId, @RequestBody @Valid RenewalFormRejectDto renewalFormRejectDto, HttpServletRequest request) {
        return renewalFormService.rejectRenewalForm(renewalFormId, renewalFormRejectDto, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-renewal-form-statuses")
    @ApiOperation(value = "Get all renewal form statuses", response = RenewalForm.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> getRenewalFormStatus() {
      return renewalFormService.getRenewalFormStatus();

    }
    @RequestMapping(method = RequestMethod.GET, value = "/get-renewal-form-by-institution", params = {"institutionId"})
    @ApiOperation(value = "Get all Institution Renewal Form", response = RenewalForm.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> getRenewalForms(@RequestParam("institutionId") String institutionId) {

        return renewalFormService.getAllRenewalForms(institutionId);

    }


}
