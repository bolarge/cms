package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.RenewalFormCreateDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormStatusDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormUpdateDto;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.RenewalFormStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Api(value = "Renewal Form", description = "", tags = "")
@RestController
@RequestMapping("/api/v1/renewalForm")
public class RenewalFormController extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "gameTypeIds", "institutionId","formStatusId"})
    @ApiOperation(value = "Get all Renewal Form", response = RenewalForm.class, responseContainer = "List", consumes = "application/json")
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
        //   return institutionService.findAllInstitutions(page, pageSize, sortType, sortParam,institutionId, gameTypeIds, httpServletResponse);

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(gameTypeIds)) {
                List<String> gameTypeIdList = Arrays.asList(gameTypeIds.split("-"));
                query.addCriteria(Criteria.where("gameTypeId").in(gameTypeIdList));
            }
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").in(institutionId));
            }
            if (!StringUtils.isEmpty(formStatusId)) {
                query.addCriteria(Criteria.where("formStatusId").in(formStatusId));
            }
            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, RenewalForm.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }
            Sort sort;
            if (!StringUtils.isEmpty(sortType) && !StringUtils.isEmpty(sortParam)) {
                sort = new Sort((sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortParam);
            } else {
                sort = new Sort(Sort.Direction.DESC, "id");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            ArrayList<RenewalForm> renewalForms = (ArrayList<RenewalForm>) mongoRepositoryReactive
                    .findAll(query, Institution.class).toStream().collect(Collectors.toList());
            if (renewalForms.size() == 0) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.BAD_REQUEST));
            }
            ArrayList<RenewalFormDto> renewalFormDtos = new ArrayList<>();
            renewalForms.forEach(entry -> {
                renewalFormDtos.add(entry.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(renewalFormDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all institutions";
            return Mono.just(new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST));

        }
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
        PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.findById(renewalFormCreateDto.getPaymentRecordId(), PaymentRecord.class).block();

        if (!paymentRecord.getInstitutionId().equals(renewalFormCreateDto.getInstitutionId())) {
            return Mono.just(new ResponseEntity<>("Invalid Institution Selected", HttpStatus.BAD_REQUEST));

        }
        if (!paymentRecord.convertToDto().getGameTypeId().equals(renewalFormCreateDto.getGameTypeId())) {
            return Mono.just(new ResponseEntity<>("Invalid institution Selected", HttpStatus.BAD_REQUEST));
        }
        if (paymentRecord.convertToDto().getFeePaymentTypeId().equals(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID)) {
            return Mono.just(new ResponseEntity<>("Invalid Payment Record Selected", HttpStatus.BAD_REQUEST));
        }

        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckChangeInGamingMachines())) {
            return Mono.just(new ResponseEntity<>("Enter CheckChangeInGamingMachines", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckConvictedCrime())) {
            return Mono.just(new ResponseEntity<>("Enter CheckConvictedCrime", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckNewInvestors())) {
            return Mono.just(new ResponseEntity<>("Enter CheckNewInvestors", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckPoliticalOffice())) {
            return Mono.just(new ResponseEntity<>("Enter CheckPoliticalOffice", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckPoliticalParty())) {
            return Mono.just(new ResponseEntity<>("Enter CheckPoliticalParty", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckTechnicalPartner())) {
            return Mono.just(new ResponseEntity<>("Enter CheckTechnicalPartner", HttpStatus.BAD_REQUEST));

        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckStakeHoldersChange())) {
            return Mono.just(new ResponseEntity<>("Enter CheckStakeHoldersChange", HttpStatus.BAD_REQUEST));
        }
        if (StringUtils.isEmpty(renewalFormCreateDto.getCheckSharesAquisition())) {
            return Mono.just(new ResponseEntity<>("Enter CheckSharesAquisition", HttpStatus.BAD_REQUEST));

        }
        try {
            Query queryRenewal = new Query();
            queryRenewal.addCriteria(Criteria.where("paymentRecordId").is(renewalFormCreateDto.getPaymentRecordId()));
            RenewalForm renewalFormCheck = (RenewalForm) mongoRepositoryReactive.find(queryRenewal, RenewalForm.class).block();
            if (renewalFormCheck != null) {
                return Mono.just(new ResponseEntity<>("An existing renewal application is tied to this payment", HttpStatus.BAD_REQUEST));
            }
            Query queryLicense = new Query();
            queryLicense.addCriteria(Criteria.where("paymentRecordId").is(renewalFormCreateDto.getPaymentRecordId()));
            License license = (License) mongoRepositoryReactive.find(queryRenewal, License.class).block();

            RenewalForm renewalForm = new RenewalForm();
            renewalForm.setId(UUID.randomUUID().toString());
            renewalForm.setLicensedId(license.getId());
            renewalForm.setPaymentRecordId(renewalFormCreateDto.getPaymentRecordId());
            renewalForm.setCheckChangeInGamingMachines(renewalFormCreateDto.getCheckChangeInGamingMachines());
            renewalForm.setCheckConvictedCrime(renewalFormCreateDto.getCheckConvictedCrime());
            renewalForm.setCheckNewInvestors(renewalFormCreateDto.getCheckNewInvestors());
            renewalForm.setCheckPoliticalOffice(renewalFormCreateDto.getCheckPoliticalOffice());
            renewalForm.setCheckPoliticalParty(renewalFormCreateDto.getCheckPoliticalParty());
            renewalForm.setCheckSharesAquisition(renewalFormCreateDto.getCheckSharesAquisition());
            renewalForm.setCheckStakeHoldersChange(renewalFormCreateDto.getCheckStakeHoldersChange());
            renewalForm.setCheckTechnicalPartner(renewalFormCreateDto.getCheckTechnicalPartner());
            renewalForm.setChangeInGamingMachines(renewalFormCreateDto.getChangeInGamingMachines());
            renewalForm.setNewInvestors(renewalFormCreateDto.getNewInvestors());
            renewalForm.setPoliticalParty(renewalFormCreateDto.getPoliticalParty());
            renewalForm.setPoliticalOffice(renewalFormCreateDto.getPoliticalOffice());
            renewalForm.setConvictedCrime(renewalFormCreateDto.getConvictedCrime());
            renewalForm.setSharesAquisition(renewalFormCreateDto.getSharesAquisition());
            renewalForm.setStakeHoldersChange(renewalFormCreateDto.getStakeHoldersChange());
            renewalForm.setTechnicalPartner(renewalFormCreateDto.getTechnicalPartner());
            renewalForm.setInstitutionId(renewalFormCreateDto.getInstitutionId());
            renewalForm.setGameTypeId(renewalFormCreateDto.getGameTypeId());
            renewalForm.setFormStatusId(RenewalFormStatusReferenceData.PENDING_DOCUMENT_UPLOAD);
            mongoRepositoryReactive.saveOrUpdate(renewalForm);
            return Mono.just(new ResponseEntity<>(renewalForm.convertToDto(), HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));
        }

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

        try {
            RenewalForm renewalForm = (RenewalForm) mongoRepositoryReactive.findById(renewalFormUpdateDto.getId(), RenewalForm.class).block();
            if (renewalForm == null) {

                return Mono.just(new ResponseEntity<>("Invalid Renewal Form Selected", HttpStatus.BAD_REQUEST));

            }
            renewalForm.setCheckChangeInGamingMachines(renewalFormUpdateDto.getCheckChangeInGamingMachines());
            renewalForm.setCheckConvictedCrime(renewalFormUpdateDto.getCheckConvictedCrime());
            renewalForm.setCheckNewInvestors(renewalFormUpdateDto.getCheckNewInvestors());
            renewalForm.setCheckPoliticalOffice(renewalFormUpdateDto.getCheckPoliticalOffice());
            renewalForm.setCheckPoliticalParty(renewalFormUpdateDto.getCheckPoliticalParty());
            renewalForm.setCheckSharesAquisition(renewalFormUpdateDto.getCheckSharesAquisition());
            renewalForm.setCheckStakeHoldersChange(renewalFormUpdateDto.getCheckStakeHoldersChange());
            renewalForm.setCheckTechnicalPartner(renewalFormUpdateDto.getCheckTechnicalPartner());
            renewalForm.setChangeInGamingMachines(renewalFormUpdateDto.getChangeInGamingMachines());
            renewalForm.setNewInvestors(renewalFormUpdateDto.getNewInvestors());
            renewalForm.setPoliticalParty(renewalFormUpdateDto.getPoliticalParty());
            renewalForm.setPoliticalOffice(renewalFormUpdateDto.getPoliticalOffice());
            renewalForm.setConvictedCrime(renewalFormUpdateDto.getConvictedCrime());
            renewalForm.setSharesAquisition(renewalFormUpdateDto.getSharesAquisition());
            renewalForm.setStakeHoldersChange(renewalFormUpdateDto.getStakeHoldersChange());
            renewalForm.setTechnicalPartner(renewalFormUpdateDto.getTechnicalPartner());
            renewalForm.setFormStatusId(RenewalFormStatusReferenceData.PENDING_DOCUMENT_UPLOAD);

            mongoRepositoryReactive.saveOrUpdate(renewalForm);
            return Mono.just(new ResponseEntity<>(renewalForm.convertToDto(), HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
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

        try {
            List<RenewalFormStatus> renewalFormStatuses = (List<RenewalFormStatus>)mongoRepositoryReactive.findAll(new Query(), RenewalFormStatus.class).toStream().collect(Collectors.toList());
            List<RenewalFormStatusDto> renewalFormStatusDtos = new ArrayList<>();
          renewalFormStatuses.stream().forEach(renewalFormStatus ->{
              renewalFormStatusDtos.add(renewalFormStatus.convertToDto());
          } );

            return Mono.just(new ResponseEntity<>(renewalFormStatusDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all formStatuses";
            return Mono.just(new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST));
        }
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
    public Mono<ResponseEntity> getRenewalForms(

            @RequestParam("institutionId") String institutionId
    ) {

        try {
            Query query = new Query();

            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").in(institutionId));
            }

            ArrayList<RenewalForm> renewalForms = (ArrayList<RenewalForm>) mongoRepositoryReactive
                    .findAll(query, RenewalForm.class).toStream().collect(Collectors.toList());
            if (renewalForms.size() == 0) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.BAD_REQUEST));
            }
            ArrayList<RenewalFormDto> renewalFormDtos = new ArrayList<>();
            renewalForms.forEach(entry -> {
                renewalFormDtos.add(entry.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(renewalFormDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all institutions";
            return Mono.just(new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST));
        }
    }
}
