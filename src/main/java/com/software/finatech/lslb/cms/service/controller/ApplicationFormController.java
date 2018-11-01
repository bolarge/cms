package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.model.applicantDetails.ApplicantDetails;
import com.software.finatech.lslb.cms.service.model.applicantMembers.ApplicantMemberDetails;
import com.software.finatech.lslb.cms.service.model.contactDetails.ApplicantContactDetails;
import com.software.finatech.lslb.cms.service.model.criminalityDetails.ApplicantCriminalityDetails;
import com.software.finatech.lslb.cms.service.model.declaration.ApplicantDeclarationDetails;
import com.software.finatech.lslb.cms.service.model.otherInformation.ApplicantOtherInformation;
import com.software.finatech.lslb.cms.service.model.outletInformation.ApplicantOutletInformation;
import com.software.finatech.lslb.cms.service.service.contracts.ApplicationFormService;
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

@Api(value = "Application Form", description = "For everything related to gaming operators application forms",
        tags = "Application Form Controller")
@RestController
@RequestMapping("/api/v1/application-form")
public class ApplicationFormController {


    private ApplicationFormService applicationFormService;

    @Autowired
    public void setApplicationFormService(ApplicationFormService applicationFormService) {
        this.applicationFormService = applicationFormService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize",
            "sortType", "sortProperty", "institutionId", "applicationFormStatusId", "gameTypeId"})
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
                                                       @RequestParam("applicationFormStatusId") String applicationFormStatusId,
                                                       @RequestParam("approverId") String approverId,
                                                       @RequestParam("gameTypeId") String gameTypeId,
                                                       HttpServletResponse httpServletResponse) {
        return applicationFormService.findAllApplicationForm(page, pageSize, sortType, sortParam, institutionId, applicationFormStatusId, approverId, gameTypeId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create an Application Form", response = ApplicationFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createApplicationForm(@RequestBody @Valid ApplicationFormCreateDto applicationFormCreateDto, HttpServletRequest request) {
        return applicationFormService.createApplicationForm(applicationFormCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-application-form-status")
    @ApiOperation(value = "Get all application form Statuses", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllApplicationFormStatuses() {
        return applicationFormService.getAllApplicationFormStatus();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-applicant-details", params = {"applicationFormId"})
    @ApiOperation(value = "Get applicant details", response = ApplicantDetails.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApplicantDetails(@RequestParam("applicationFormId") String applicationFormId) {
        return applicationFormService.getApplicantDetails(applicationFormId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{applicationFormId}/save-applicant-details")
    @ApiOperation(value = "Save Applicant details", response = ApplicationFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> saveApplicantDetails(@PathVariable("applicationFormId") String applicationFormId, @RequestBody ApplicantDetails applicantDetails, HttpServletRequest request) {
        return applicationFormService.saveApplicantDetails(applicationFormId, applicantDetails, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-applicant-members-details", params = {"applicationFormId"})
    @ApiOperation(value = "Get applicant members details", response = ApplicantMemberDetails.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApplicantMembersDetails(@RequestParam("applicationFormId") String applicationFormId) {
        return applicationFormService.getApplicantMembersDetails(applicationFormId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{applicationFormId}/save-applicant-members-details")
    @ApiOperation(value = "Save applicant members details ", response = ApplicationFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> saveApplicantMemberDetails(@PathVariable("applicationFormId") String applicationFormId, @RequestBody ApplicantMemberDetails applicantMemberDetails, HttpServletRequest request) {
        return applicationFormService.saveApplicantMembersDetails(applicationFormId, applicantMemberDetails, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-applicant-contact-details", params = {"applicationFormId"})
    @ApiOperation(value = "Get applicant contact details", response = ApplicantContactDetails.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApplicantContactDetails(@RequestParam("applicationFormId") String applicationFormId) {
        return applicationFormService.getApplicantContactDetails(applicationFormId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{applicationFormId}/save-applicant-contact-details")
    @ApiOperation(value = "Save applicant contact details", response = ApplicationFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> saveApplicantContactDetails(@PathVariable("applicationFormId") String applicationFormId, @RequestBody ApplicantContactDetails applicantContactDetails, HttpServletRequest request) {
        return applicationFormService.saveApplicantContactDetails(applicationFormId, applicantContactDetails, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-applicant-criminality-details", params = {"applicationFormId"})
    @ApiOperation(value = "Get applicant criminality details", response = ApplicantCriminalityDetails.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApplicantCriminalityDetails(@RequestParam("applicationFormId") String applicationFormId) {
        return applicationFormService.getApplicantCriminalityDetails(applicationFormId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{applicationFormId}/save-applicant-criminality-details")
    @ApiOperation(value = "Save applicant criminality details", response = ApplicationFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> saveApplicantCriminalityDetails(@PathVariable("applicationFormId") String applicationFormId, @RequestBody ApplicantCriminalityDetails applicantCriminalityDetails, HttpServletRequest request) {
        return applicationFormService.saveApplicantCriminalityDetails(applicationFormId, applicantCriminalityDetails, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-applicant-declaration-details", params = {"applicationFormId"})
    @ApiOperation(value = "Get applicant declaration details", response = ApplicantDeclarationDetails.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApplicantDeclarationDetails(@RequestParam("applicationFormId") String applicationFormId) {
        return applicationFormService.getApplicantDeclarationDetails(applicationFormId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{applicationFormId}/save-applicant-declaration-details")
    @ApiOperation(value = "Save applicant declaration details", response = ApplicationFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> saveApplicantDeclarationDetails(@PathVariable("applicationFormId") String applicationFormId, @RequestBody ApplicantDeclarationDetails applicantDeclarationDetails, HttpServletRequest request) {
        return applicationFormService.saveApplicantDeclarationDetails(applicationFormId, applicantDeclarationDetails, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-applicant-other-information", params = {"applicationFormId"})
    @ApiOperation(value = "Get applicant other information", response = ApplicantOtherInformation.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApplicantOtherInformation(@RequestParam("applicationFormId") String applicationFormId) {
        return applicationFormService.getApplicantOtherInformation(applicationFormId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{applicationFormId}/save-applicant-other-information")
    @ApiOperation(value = "Save applicant other information", response = ApplicationFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> saveApplicantOtherInformation(@PathVariable("applicationFormId") String applicationFormId, @RequestBody ApplicantOtherInformation applicantOtherInformation, HttpServletRequest request) {
        return applicationFormService.saveApplicantOtherInformation(applicationFormId, applicantOtherInformation, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-applicant-outlet-information", params = {"applicationFormId"})
    @ApiOperation(value = "Get applicant outlet information", response = ApplicantOutletInformation.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getApplicantOutletInformation(@RequestParam("applicationFormId") String applicationFormId) {
        return applicationFormService.getApplicantOutletInformation(applicationFormId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{applicationFormId}/save-applicant-outlet-information")
    @ApiOperation(value = "Save applicant outlet information", response = ApplicationFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> saveApplicantOutletInformation(@PathVariable("applicationFormId") String applicationFormId, @RequestBody ApplicantOutletInformation applicantOutletInformation, HttpServletRequest request) {
        return applicationFormService.saveApplicantOutletInformation(applicationFormId, applicantOutletInformation, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/approve-application-form", params = {"applicationFormId", "userId"})
    @ApiOperation(value = "Approve application form", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveApplicationForm(@RequestParam("applicationFormId") String applicationFormId, @RequestParam("userId") String approverId, HttpServletRequest request) {
        return applicationFormService.approveApplicationForm(applicationFormId, approverId, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/approve-aip-form", params = {"aipFormId", "userId"})
    @ApiOperation(value = "Approve aip form", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveAIPForm(@RequestParam("aipFormId") String aipFormId, @RequestParam("userId") String approverId, HttpServletRequest request) {
        return applicationFormService.approveAIPForm(aipFormId, approverId, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject-application-form", params = {"applicationFormId"})
    @ApiOperation(value = "Reject application form", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectApplicationForm(@RequestParam("applicationFormId") String applicationFormId, @RequestBody @Valid ApplicationFormRejectDto applicationFormRejectDto, HttpServletRequest request) {
        return applicationFormService.rejectApplicationForm(applicationFormId, applicationFormRejectDto, request);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/complete-application-form", params = {"applicationFormId"})
    @ApiOperation(value = "complete filling application form", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> completeApplicationForm(@RequestParam("applicationFormId") String applicationFormId, @RequestParam("isResubmit") boolean isResubmit, HttpServletRequest request) {
        return applicationFormService.completeApplicationForm(applicationFormId, isResubmit, request);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/complete-aip-form", params = {"institutionId","gameTypeId"})
    @ApiOperation(value = "complete filling AIP form", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> completeAIPForm(@RequestParam("institutionId") String institutionId,
                                                @RequestParam("gameTypeId") String gameTypeId, HttpServletRequest request) {
        return applicationFormService.completeAIPForm(institutionId,gameTypeId, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/payment-records", params = {"applicationFormId"})
    @ApiOperation(value = "Get payment records for application form", response = PaymentRecordDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getPaymentRecordsApplicationForm(@RequestParam("applicationFormId") String applicationFormId) {
        return applicationFormService.getPaymentRecordsForApplicationForm(applicationFormId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create-comment", params = {"applicationFormId"})
    @ApiOperation(value = "Create comment for application form from LSLB Admin", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createApplicationFormComment(@RequestParam("applicationFormId") String applicationFormId,
                                                             @RequestBody @Valid ApplicationFormCreateCommentDto formCreateCommentDto, HttpServletRequest request) {
        return applicationFormService.addCommentsToFormFromLslbAdmin(applicationFormId, formCreateCommentDto, request);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/create-aip-comment", params = {"aipFormId"})
    @ApiOperation(value = "Create comment for AIP form from LSLB Admin", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createAIPFormComment(@RequestParam("aipFormId") String aipFormId,
                                                     @RequestBody @Valid FormCreateCommentDto formCreateCommentDto, HttpServletRequest request) {
        return applicationFormService.addCommentsToAIPFormFromLslbAdmin(aipFormId, formCreateCommentDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{applicationFormId}/add-comment")
    @ApiOperation(value = "Add Comment to application form", response = ApplicationFormDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> addCommentToApplicationForm(@PathVariable("applicationFormId") String applicationFormId, @RequestBody AddCommentDto addCommentDto, HttpServletRequest request) {
        return applicationFormService.addCommentsToForm(applicationFormId, addCommentDto, request);
    }
}
