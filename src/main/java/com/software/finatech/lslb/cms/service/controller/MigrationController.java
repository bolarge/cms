package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingAgentLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingGamingMachineLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingGamingTerminalLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingOperatorLoader;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

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
            existingAgentLoader.createUserAndCustomerCodeForLiveAgents();
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


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
}
