package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "License", description = "For everything related to gaming operators licenses", tags = "Licence Controller")
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
            "sortType", "sortProperty", "institutionId", "gamingMachineId",
            "agentId", "licenseStatusId", "gameTypeId", "paymentRecordId", "licenseTypeId", "date", "licenseNumber"})
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
                                               @RequestParam("agentId") String agentId,
                                               @RequestParam("licenseTypeId") String licenseType,
                                               @RequestParam("gamingMachineId") String gamingMachineId,
                                               @RequestParam("licenseStatusId") String licenseStatusId,
                                               @RequestParam("gameTypeId") String gameTypeId,
                                               @RequestParam("paymentRecordId") String paymentRecordId,
                                               @RequestParam("date") String date,
                                               @RequestParam("licenseNumber") String licenseNumber,
                                               HttpServletResponse httpServletResponse) {
        return licenseService.findAllLicense(page, pageSize, sortType, sortParam, institutionId,
                agentId, gamingMachineId, licenseStatusId, gameTypeId, paymentRecordId, date, licenseNumber,licenseType, httpServletResponse);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/get-license")
    @ApiOperation(value = "Get License by InstitutionId, agentId, gamingMachineId and gameType", response = LicenseDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getLicense(
            @RequestParam("institutionId") String institutionId,
            @RequestParam("agentId") String agentId,
            @RequestParam("licensedId") String licensedId,
            @RequestParam("gamingMachineId") String gamingMachineId,
            @RequestParam("gameTypeId") String gameTypeId) {
        return licenseService.findLicense(licensedId, institutionId, agentId, gamingMachineId, gameTypeId);
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


    @RequestMapping(method = RequestMethod.GET, value = "/all-license-types")
    @ApiOperation(value = "Get all license types", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllLicenseTypes() {
        return Mono.just(new ResponseEntity<>(licenseService.getAllLicenseTypes(), HttpStatus.OK));

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
        try {
            return licenseService.getExpiredLicenses();
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-expired-aips")
    @ApiOperation(value = "Get all Expired AIPs", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllExpiredAIPStatus() {
        try {
            return licenseService.getExpiredAIPs();
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-institution-aips", params = {"institutionId"})
    @ApiOperation(value = "Get all Institution AIPs", response = AIPCheckDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllAIPStatus(@RequestParam("institutionId") String institutionId) {
        try {
            return licenseService.getInstitutionAIPs(institutionId);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }
    @RequestMapping(method = RequestMethod.GET, value = "/get-renewal-review-licenses", params = {"institutionId"})
    @ApiOperation(value = "Get all Institution licenses In Renewal In Review", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllRenewalInReviews(@RequestParam("institutionId") String institutionId) {
        try {
            return licenseService.getLicensesInRenewalInReview(institutionId);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }
    @RequestMapping(method = RequestMethod.GET, value = "/get-institution-uploaded-aips", params = {"institutionId"})
    @ApiOperation(value = "Get all Institution Uploaded Document AIPs", response = AIPCheckDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllAIPUploaded(@RequestParam("institutionId") String institutionId) {
        try {
            return licenseService.getInstitutionAIPUploaded(institutionId);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }


    @RequestMapping(method = RequestMethod.GET, value = "/get-institution-licenses-close-to-expiration", params = {"institutionId"})
    @ApiOperation(value = "Get all Institution Licenses that are close to expiration", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllInstitutionCloseToExpirationLicenses(@RequestParam("institutionId") String institutionId) {
        try {
            return licenseService.getInstitutionCloseToExpirationLicenses(institutionId);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }
    @RequestMapping(method = RequestMethod.GET, value = "/get-agent-licenses-close-to-expiration", params = {"agentId"})
    @ApiOperation(value = "Get Agent Licenses that are close to expiration", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllAgentCloseToExpirationLicenses(@RequestParam("agentId") String agentId) {
        try {
            return licenseService.getAgentLicensesCloseToExpiration(agentId);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }
    @RequestMapping(method = RequestMethod.GET, value = "/get-gaming-machine-licenses-close-to-expiration", params = {"gamingMachineId"})
    @ApiOperation(value = "Get Gaming Machine Licenses that are close to expiration", response = License.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllGamingMachinesCloseToExpirationLicenses(@RequestParam("gamingMachineId") String gamingMachineId) {
        try {
            return licenseService.getGamingMachineLicensesCloseToExpiration(gamingMachineId);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }
    @RequestMapping(method = RequestMethod.GET, value = "/update-to-aip-document", params = {"licensedId"})
    @ApiOperation(value = "Update AIP to AIP Document Upload Status", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateLicenseToAIP(@RequestParam("licensedId") String licensedId) {
        try {
            return licenseService.updateToDocumentAIP(licensedId);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }


    @RequestMapping(method = RequestMethod.GET, value = "/update-to-renewal-in-review-from-in-progress", params = {"paymentRecordId"})
    @ApiOperation(value = "Update Renewal In progress to Renewal In Review", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateInProgressToInReview(@RequestParam("paymentRecordId") String paymentRecordId) {
        try {
            return licenseService.updateRenewalLicenseToReview(paymentRecordId);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact Admin", HttpStatus.BAD_REQUEST));

        }
    }


//    @RequestMapping(method = RequestMethod.POST, value = "/update-renewal-in-review-to-in-progress")
//    @ApiOperation(value = "Update Renewal In Review to Renewal In Progress", response = String.class, consumes = "application/json")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "OK"),
//            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
//            @ApiResponse(code = 400, message = "Bad request"),
//            @ApiResponse(code = 404, message = "Not Found")})
//    public Mono<ResponseEntity> updateInReviewToInProgress(@RequestBody @Valid RenewalFormCommentDto renewalFormCommentDto) {
//        try {
//            return licenseService.updateRenewalReviewToInProgress(renewalFormCommentDto);
//        } catch (Exception ex) {
//            return Mono.just(new ResponseEntity<>("Error! Please contact Admin", HttpStatus.BAD_REQUEST));
//
//        }
//    }
    @RequestMapping(method = RequestMethod.GET, value = "/update-to-renewal-in-review-from-in-license", params = {"licenseId"})
    @ApiOperation(value = "Update Renewal In review to Renewal License", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateInReviewToInLicense(@RequestParam("licenseId") String licenseId) {
        try {
            return licenseService.updateInReviewToLicense(licenseId);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact Admin", HttpStatus.BAD_REQUEST));

        }
    }
    @RequestMapping(method = RequestMethod.POST, value = "/update-aipdoc-to-license")
    @ApiOperation(value = "Update AIP to AIP Document Upload Status", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateAIPToLicense(@RequestBody @Valid LicenseUpdateAIPToLicenseDto licenseUpdateAIPToLicenseDto) {
        try {
            return licenseService.updateAIPDocToLicense(licenseUpdateAIPToLicenseDto);
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/specific-license", params = {"institutionId", "agentId", "gamingMachineId", "gameTypeId", "licenseTypeId"})
    @ApiOperation(value = "Get specific license", response = LicenseDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getSpecificLicense(@RequestParam("institutionId") String institutionId,
                                                               @RequestParam("gameTypeId") String gameTypeId,
                                                               @RequestParam("agentId") String agentId,
                                                               @RequestParam("gamingMachineId") String gamingMachineId,
                                                               @RequestParam("licenseTypeId") String licenseTypeId
                                                              ) {
        try {
            if (StringUtils.isEmpty(institutionId) && StringUtils.isEmpty(agentId) && StringUtils.isEmpty(gamingMachineId)) {
                return Mono.just(new ResponseEntity<>("Provide InstitutionId or agentId or Gaming Machine Id", HttpStatus.BAD_REQUEST));

            }
            if (!StringUtils.isEmpty(institutionId) && !StringUtils.isEmpty(agentId) && !StringUtils.isEmpty(gamingMachineId)) {
                return Mono.just(new ResponseEntity<>("Provide InstitutionId or InstitutionId and agentId or InstitutionId and Gaming Machine Id", HttpStatus.BAD_REQUEST));

            }
            if (StringUtils.isEmpty(gameTypeId)) {
                return Mono.just(new ResponseEntity<>("Provide GameTypeId", HttpStatus.BAD_REQUEST));

            } if (StringUtils.isEmpty(licenseTypeId)) {
                return Mono.just(new ResponseEntity<>("Provide LicenseType", HttpStatus.BAD_REQUEST));

            }
            License licenseRecords = licenseService.findRenewalLicense(institutionId, agentId, gamingMachineId, gameTypeId, licenseTypeId);

           if(licenseRecords==null){
               return Mono.just(new ResponseEntity<>("No Payment Record Found or An Incomplete Renewal Application Exists", HttpStatus.BAD_REQUEST));

           }else{
               return Mono.just(new ResponseEntity<>(licenseRecords.convertToDto(), HttpStatus.OK));

           }
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }

    }
}
