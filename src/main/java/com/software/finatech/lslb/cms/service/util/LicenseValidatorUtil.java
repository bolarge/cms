package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
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

    public Mono<ResponseEntity> validateInstitutionLicenseForGameType(String institutionId, String gameTypeId){
        if (licenseService.institutionIsLicensedForGameType(institutionId, gameTypeId)){
            return  null;
        } else {
            GameType gameType = gameTypeService.findById(gameTypeId);
            String gameTypeName = gameTypeId;
            if (gameType != null) {
                gameTypeName = gameType.getDescription();
            }
            Institution institution = institutionService.findByInstitutionId(institutionId);
            String institutionName = institutionId;
            if (institution != null) {
                institutionName = institution.getInstitutionName();
            }
            return Mono.just(new ResponseEntity<>(String.format("%s is not currently licensed for category %s", institutionName, gameTypeName), HttpStatus.BAD_REQUEST));
        }
    }
}
