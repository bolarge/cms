package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.InstitutionCreateDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionUpdateDto;
import com.software.finatech.lslb.cms.service.dto.UploadTransactionResponse;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "AuthInfo", description = "", tags = "Institution Controller")
@RestController
@RequestMapping("/api/v1/institution")
public class InstitutionController extends BaseController {


    private InstitutionService institutionService;

    @Autowired
    public void setInstitutionService(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "gameTypeIds"})
    @ApiOperation(value = "Get all institutions", response = InstitutionDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> getAllInstitutions(@RequestParam("page") int page,
                                                   @RequestParam("pageSize") int pageSize,
                                                   @RequestParam("sortType") String sortType,
                                                   @RequestParam("sortProperty") String sortParam,
                                                   @RequestParam("gameTypeIds") String gameTypeIds,
                                                   HttpServletResponse httpServletResponse) {
        return institutionService.findAllInstitutions(page, pageSize, sortType, sortParam, gameTypeIds, httpServletResponse);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/new")
    @ApiOperation(value = "Create new Institution", response = InstitutionDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createInstitution(@RequestBody @Valid InstitutionCreateDto institutionCreateDto) {
        return institutionService.createInstitution(institutionCreateDto);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Updates an existing Institution", response = Institution.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> updateInstitution(@RequestBody @Valid InstitutionUpdateDto institutionUpdateDto, HttpServletRequest request) {
        return institutionService.updateInstitution(institutionUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/new-applicant-institution")
    @ApiOperation(value = "Create new Institution for an applicant  ", response = InstitutionDto.class, consumes = "application/json",
            notes = "Creates a new institution for an applicant, then makes the applicant a gaming-operator-admin of the institution")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createApplicantInstitution(@RequestBody @Valid InstitutionCreateDto institutionCreateDto) {
        String applicantUserId = institutionCreateDto.getUserId();
        if (StringUtils.isEmpty(applicantUserId)) {
            return Mono.just(new ResponseEntity<>("Please provide applicant user id", HttpStatus.BAD_REQUEST));
        }
        AuthInfo applicantUser = (AuthInfo) mongoRepositoryReactive.findById(applicantUserId, AuthInfo.class).block();
        if (applicantUser == null) {
            return Mono.just(new ResponseEntity<>("Applicant user does not exist", HttpStatus.BAD_REQUEST));
        }
        return institutionService.createApplicantInstitution(institutionCreateDto, applicantUser);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload-multiple-existing-licenced-operators")
    @ApiOperation(value = "Upload multiple licenced institutions", response = UploadTransactionResponse.class,
            notes = "Upload multiple institutions that are already existing in LSLB records and are already licensed")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server error(error occurred while parsing file)")})
    public Mono<ResponseEntity> uploadMultipleInstitutions(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
        return institutionService.uploadMultipleExistingLicensedInstitutions(multipartFile, request);
    }
}
