package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.InstitutionDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormCreateDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormUpdateDto;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(value = "GameType", description = "", tags = "")
@RestController
@RequestMapping("/api/v1/renewalForm")
public class RenewalFormController extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "gameTypeIds","institutionId"})
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

            ArrayList<Institution> institutions = (ArrayList<Institution>) mongoRepositoryReactive
                    .findAll(query, Institution.class).toStream().collect(Collectors.toList());
            if (institutions.size() == 0) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            ArrayList<InstitutionDto> institutionDtos = new ArrayList<>();
            institutions.forEach(entry -> {
                institutionDtos.add(entry.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(institutionDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all institutions";
            return null;//logAndReturnError(errorMsg, errorMsg, e);
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

        if(!paymentRecord.getInstitutionId().equals(renewalFormCreateDto.getInstitutionId())){
            return Mono.just(new ResponseEntity<>("Invalid Institution Selected", HttpStatus.BAD_REQUEST));

        }if(!paymentRecord.convertToDto().getFee().getGameType().getId().equals(renewalFormCreateDto.getGameTypeId())){
            return Mono.just(new ResponseEntity<>("Invalid institution Selected", HttpStatus.BAD_REQUEST));
        }
        if (paymentRecord.convertToDto().getFee().getFeePaymentType().getId().equals(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID)){
            return Mono.just(new ResponseEntity<>("Invalid Payment Record Selected", HttpStatus.BAD_REQUEST));
        }

        if(StringUtils.isEmpty(renewalFormCreateDto.getCheckChangeInGamingMachines())){
            return Mono.just(new ResponseEntity<>("Enter CheckChangeInGamingMachines", HttpStatus.BAD_REQUEST));

        }if(StringUtils.isEmpty(renewalFormCreateDto.getCheckConvictedCrime())){
            return Mono.just(new ResponseEntity<>("Enter CheckConvictedCrime", HttpStatus.BAD_REQUEST));

        }if(StringUtils.isEmpty(renewalFormCreateDto.getCheckNewInvestors())){
            return Mono.just(new ResponseEntity<>("Enter CheckNewInvestors", HttpStatus.BAD_REQUEST));

        }if(StringUtils.isEmpty(renewalFormCreateDto.getCheckPoliticalOffice())){
            return Mono.just(new ResponseEntity<>("Enter CheckPoliticalOffice", HttpStatus.BAD_REQUEST));

        }if(StringUtils.isEmpty(renewalFormCreateDto.getCheckPoliticalParty())){
            return Mono.just(new ResponseEntity<>("Enter CheckPoliticalParty", HttpStatus.BAD_REQUEST));

        }if(StringUtils.isEmpty(renewalFormCreateDto.getCheckTechnicalPartner())){
            return Mono.just(new ResponseEntity<>("Enter CheckTechnicalPartner", HttpStatus.BAD_REQUEST));

        }if(StringUtils.isEmpty(renewalFormCreateDto.getCheckStakeHoldersChange())){
            return Mono.just(new ResponseEntity<>("Enter CheckStakeHoldersChange", HttpStatus.BAD_REQUEST));
        }if(StringUtils.isEmpty(renewalFormCreateDto.getCheckSharesAquisition())){
            return Mono.just(new ResponseEntity<>("Enter CheckSharesAquisition", HttpStatus.BAD_REQUEST));

        }
    try{    Query queryLicenceStatus= new Query();
        queryLicenceStatus.addCriteria(Criteria.where("paymentRecordId").is(renewalFormCreateDto.getPaymentRecordId()));
        queryLicenceStatus.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.LICENSE_IN_PROGRESS_LICENSE_STATUS_ID));
        License license = (License)mongoRepositoryReactive.find(queryLicenceStatus, License.class).block();
        if(paymentRecord==null || license==null || !license.getLicenseStatusId().equals(LicenseStatusReferenceData.LICENSE_IN_PROGRESS_LICENSE_STATUS_ID)){
            return Mono.just(new ResponseEntity<>("Invalid payment record", HttpStatus.BAD_REQUEST));
        }
        Query queryRenewal= new Query();
        queryRenewal.addCriteria(Criteria.where("paymentRecordId").is(renewalFormCreateDto.getPaymentRecordId()));
        RenewalForm renewalFormCheck = (RenewalForm)mongoRepositoryReactive.find(queryRenewal, RenewalForm.class).block();
        if(renewalFormCheck!=null){
            return Mono.just(new ResponseEntity<>("An existing renewal application is tied to this payment", HttpStatus.BAD_REQUEST));
        }
        license.setRenewalStatus("false");
        license.setStartDate(LocalDateTime.now());
        Query queryGameType = new Query();
        queryGameType.addCriteria(Criteria.where("id").is(license.getPaymentRecord().convertToDto().getFee().getGameType().getId()));
        GameType gameType = (GameType) mongoRepositoryReactive.find(queryGameType, GameType.class).block();
        int duration=0;
        if(license.getLicenseStatusId().equals(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID)){
            duration = Integer.parseInt(gameType.convertToDto().getAipDuration());
        }else if(license.getLicenseStatusId().equals(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID)){
            if(license.getLicenseType().equalsIgnoreCase("agent")){
                duration = Integer.parseInt(gameType.convertToDto().getAgentLicenseDuration());
            }else if(license.getLicenseType().equalsIgnoreCase("gamingMachine")){
                duration = Integer.parseInt(gameType.convertToDto().getGamingMachineLicenseDuration());
            }else if(license.getLicenseType().equalsIgnoreCase("institution")){
                duration = Integer.parseInt(gameType.convertToDto().getLicenseDuration());
            }
        }
        license.setEndDate(license.getStartDate().plusMonths(duration));
        license.setRenewalStatus("false");

        RenewalForm renewalForm = new RenewalForm();
        renewalForm.setId(UUID.randomUUID().toString());
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
        mongoRepositoryReactive.saveOrUpdate(renewalForm);
        mongoRepositoryReactive.saveOrUpdate(license);
        return Mono.just(new ResponseEntity<>(renewalForm.convertToDto(), HttpStatus.OK));}
        catch (Exception ex){
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));
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
    public Mono<ResponseEntity> updateGameType(@RequestBody @Valid RenewalFormUpdateDto renewalFormUpdateDto) {

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
            mongoRepositoryReactive.saveOrUpdate(renewalForm);
            return Mono.just(new ResponseEntity<>(renewalForm.convertToDto(), HttpStatus.OK));
        }catch (Exception ex){
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }
    }
}
