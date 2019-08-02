package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.CreateCustomerCodeDto;
import com.software.finatech.lslb.cms.service.dto.CreateExpiredLicensePaymentDto;
import com.software.finatech.lslb.cms.service.dto.DirectorsUpdateDto;
import com.software.finatech.lslb.cms.service.dto.MigrateCategoryDto;
import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.model.migrations.MigratedInstitutionUpload;
import com.software.finatech.lslb.cms.service.model.migrations.NewMigratedAgent;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.OKResponseUtil;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingAgentLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingGamingMachineLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingGamingTerminalLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingOperatorLoader;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @author adeyi.adebolu
 * created on 15/04/2019
 */

@Api(value = "Migrations",
        description = "For Migrations", tags = "Migrations")
@RestController
@RequestMapping("/api/v1/migrations")
public class MigrationController {
    @Autowired
    private ExistingOperatorLoader existingOperatorLoader;
    @Autowired
    private ExistingAgentLoader existingAgentLoader;
    @Autowired
    private ExistingGamingTerminalLoader existingGamingTerminalLoader;
    @Autowired
    private ExistingGamingMachineLoader existingGamingMachineLoader;


    /**
     * Used to load operators from CSV .
     * Should not be used anymore
     *
     * @param multipartFile
     * @param type
     * @return
     */
    @Deprecated
    @RequestMapping(method = RequestMethod.POST, value = "/load-existing-operators")
    public Mono<ResponseEntity> create(@RequestParam("file") MultipartFile multipartFile,
                                       @RequestParam("type") String type) {
        try {
            if (StringUtils.equalsIgnoreCase("licenced", type)) {
                existingOperatorLoader.loadFromCsv(multipartFile);
            }
            if (StringUtils.equalsIgnoreCase("aip", type) || StringUtils.equalsIgnoreCase("suspended", type)) {
                existingOperatorLoader.loadAIPOrSuspendedFromCsv(multipartFile);
            }
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (LicenseServiceException e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    /**
     * Used to load agents from csv , should not be used anymore
     *
     * @param multipartFile
     * @return
     */
    @Deprecated
    @RequestMapping(method = RequestMethod.POST, value = "/load-existing-agents-from-file")
    public Mono<ResponseEntity> createAgentFromCSV(@RequestParam("file") MultipartFile multipartFile) {
        try {
            existingAgentLoader.loadAgentsFromCSV(multipartFile);
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/create-agent-users")
    public Mono<ResponseEntity> createAgentUsers() {
        try {
            existingAgentLoader.createUserForLiveAgents();
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Used to create Migrated Agent
     *
     * @param newMigratedAgent
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/create-agent-from-migration")
    public Mono<ResponseEntity> createAgentFromMigration(@RequestBody @Valid NewMigratedAgent newMigratedAgent) {
        return existingAgentLoader.loadAgentFromModel(newMigratedAgent);
    }

    /**
     * Used to upload migrated terminals
     *
     * @param institutionId
     * @param gameTypeId
     * @param multipartFile
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/upload-terminals")
    public Mono<ResponseEntity> uploadTerminals(@RequestParam("institutionId") String institutionId,
                                                @RequestParam("gameTypeId") String gameTypeId,
                                                @RequestParam("file") MultipartFile multipartFile) {
        try {
            existingGamingTerminalLoader.loadMachinesFromFile(institutionId, gameTypeId, multipartFile);
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * USed to upload migrated machines
     *
     * @param institutionId
     * @param gameTypeId
     * @param multipartFile
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/upload-machines")
    public Mono<ResponseEntity> uploadMachines(@RequestParam("institutionId") String institutionId,
                                               @RequestParam("gameTypeId") String gameTypeId,
                                               @RequestParam("file") MultipartFile multipartFile) {
        try {
            existingGamingMachineLoader.loadMachines(multipartFile, institutionId, gameTypeId);
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * USed to upload operators from JSON
     *
     * @param migratedInstitutionUpload
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/upload-operator-from-json")
    public Mono<ResponseEntity> uploadInstitution(@RequestBody MigratedInstitutionUpload migratedInstitutionUpload) {
        try {
            Institution institution = existingOperatorLoader.loadMigratedInstitutionUpload(migratedInstitutionUpload);
            return OKResponseUtil.OKResponse(institution.convertToFullDto());
        } catch (Exception e) {
            return ErrorResponseUtil.BadRequestResponse("Please provide date in yyyy-MM-dd");
        }
    }


    /**
     * Used to manually create operator vigipay customer code
     *
     * @param createCustomerCodeDto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/create-operator-vigipay-code")
    public Mono<ResponseEntity> createOperatorVgPayCode(@RequestBody CreateCustomerCodeDto createCustomerCodeDto) {
        return existingOperatorLoader.createVigiPayCustomerCodeForOperator(createCustomerCodeDto);
    }

    /**
     * Used to update migrated operator shareholder details
     *
     * @param shareHoldersUpdateDto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/change-directors")
    public Mono<ResponseEntity> uploadMachines(@RequestBody DirectorsUpdateDto shareHoldersUpdateDto) {
        return existingOperatorLoader.updateDirectorDetails(shareHoldersUpdateDto);
    }

    /**
     * Used to change entire category record of an operator in  a category
     * (Could be useful when a category is splitted into multiple)
     *
     * @param migrateCategoryDto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/change-category-record")
    public Mono<ResponseEntity> uploadMachines(@RequestBody MigrateCategoryDto migrateCategoryDto) {
        return existingOperatorLoader.changeExistingOperatorCategory(migrateCategoryDto);
    }

    /**
     * Used to created expired license payment for a migrated operator
     *
     * @param createExpiredLicensePaymentDto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/create-expired-payment")
    public Mono<ResponseEntity> uploadMachines(@RequestBody CreateExpiredLicensePaymentDto createExpiredLicensePaymentDto) {
        return existingOperatorLoader.createExpiredLicenseForOperator(createExpiredLicensePaymentDto);
    }

    /**
     * used to clear all vigipay customer codes for operators and agents on the system
     * (can be used in a case where one changes the vigipay merchant account used)
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/clear-customer-codes")
    public Mono<ResponseEntity> clearVigiPayCustomerCodes() {
        try {
            existingOperatorLoader.clearAllVigipayCustomerCodes();
            existingAgentLoader.clearAllVigipayCustomerCodes();
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
