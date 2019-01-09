package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LicenseValidatorUtil {

    private GameTypeService gameTypeService;
    private LicenseService licenseService;
    private InstitutionService institutionService;

    @Autowired
    public LicenseValidatorUtil(GameTypeService gameTypeService,
                                LicenseService licenseService,
                                InstitutionService institutionService) {
        this.gameTypeService = gameTypeService;
        this.licenseService = licenseService;
        this.institutionService = institutionService;
    }

    public Mono<ResponseEntity> validateInstitutionLicenseForGameType(String institutionId, String gameTypeId) {
//        if (licenseService.institutionIsLicensedForGameType(institutionId, gameTypeId)){
//            return  null;
//        } else {
//            GameType gameType = gameTypeService.findById(gameTypeId);
//            String gameTypeName = gameTypeId;
//            if (gameType != null) {
//                gameTypeName = gameType.getDescription();
//            }
//            Institution institution = institutionService.findByInstitutionId(institutionId);
//            String institutionName = institutionId;
//            if (institution != null) {
//                institutionName = institution.getInstitutionName();
//            }
//            return Mono.just(new ResponseEntity<>(String.format("%s is not currently licensed for category %s", institutionName, gameTypeName), HttpStatus.BAD_REQUEST));
//        }

        License currentLicence = licenseService.findInstitutionActiveLicenseInGameType(institutionId, gameTypeId);
        GameType gameType = gameTypeService.findById(gameTypeId);

        String badRequestResponseString = "";
        if (currentLicence == null) {
            badRequestResponseString = String.format("Operator is not currently licenced for %s", gameType);
            Institution institution = institutionService.findByInstitutionId(institutionId);
            String institutionName = institutionId;
            if (institution != null) {
                institutionName = institution.getInstitutionName();
            }
            badRequestResponseString = badRequestResponseString.replaceAll("Operator", institutionName);
            return Mono.just(new ResponseEntity<>(badRequestResponseString, HttpStatus.BAD_REQUEST));
        }
        if (!LicenseStatusReferenceData.getAllowedLicensedStatusIds().contains(currentLicence.getLicenseStatusId())) {
            badRequestResponseString = String.format("Operator licence for %s is not valid", gameType);
        }
        if (currentLicence.isSuspendedLicence()) {
            badRequestResponseString = String.format("Operator licence for %s is currently suspended", gameType);
        }
        if (currentLicence.isRevokedLicence()) {
            badRequestResponseString = String.format("Operator licence for %s is currently revoked", gameType);
        }
        if (currentLicence.isTerminatedLicence()) {
            badRequestResponseString = String.format("Operator licence for %s is currently terminated", gameType);
        }
        if (!StringUtils.isEmpty(badRequestResponseString)) {
            Institution institution = institutionService.findByInstitutionId(institutionId);
            String institutionName = institutionId;
            if (institution != null) {
                institutionName = institution.getInstitutionName();
            }
            badRequestResponseString = badRequestResponseString.replaceAll("Operator", institutionName);
            return Mono.just(new ResponseEntity<>(badRequestResponseString, HttpStatus.BAD_REQUEST));
        }
        return null;
    }
}
