package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.dto.LicenseStatusDto;
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

import java.util.List;

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

    public Mono<ResponseEntity> validateInstitutionGameTypeLicenseConfirmed(String institutionId, String gameTypeId) {
        GameType gameType = gameTypeService.findById(gameTypeId);
        String gameTypeName = gameTypeId;
        if (gameType != null) {
            gameTypeName = gameType.getDescription();
        }

        Object licenseDtoEntity = licenseService.findLicense(null, institutionId, null, null, gameTypeId).block().getBody();
        if (licenseDtoEntity instanceof List) {
            List<LicenseDto> licenseDtosList = (List<LicenseDto>) licenseDtoEntity;
            //check if he has existing license record
            if (licenseDtosList.isEmpty()) {
                Institution institution = institutionService.findById(institutionId);
                String institutionName = institutionId;
                if (institution != null) {
                    institutionName = institution.getInstitutionName();
                }
                return Mono.just(new ResponseEntity<>(String.format("Institution %s does not have an existing license record for gameType %s", institutionName, gameTypeName), HttpStatus.BAD_REQUEST));
            }

            //check if he has  more than one license record (which is never meant to happen)
            if (licenseDtosList.size() > 1) {
                Institution institution = institutionService.findById(institutionId);
                String institutionName = institutionId;
                if (institution != null) {
                    institutionName = institution.getInstitutionName();
                }
                return Mono.just(new ResponseEntity<>(String.format("Institution %s has more than one license record for gameType %s", institutionName, gameTypeName), HttpStatus.BAD_REQUEST));
            }

            //get the first and only record of the license and check if he has a proper LICENSED status for it
            LicenseDto licenseDto = licenseDtosList.get(0);
            LicenseStatusDto licenseStatusDto = licenseDto.getLicenseStatus();
            if (licenseStatusDto != null) {
                String licensedStatusId = LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID;
                if (!StringUtils.equals(licensedStatusId, licenseStatusDto.getId())) {
                    Institution institution = institutionService.findById(institutionId);
                    String institutionName = institutionId;
                    if (institution != null) {
                        institutionName = institution.getInstitutionName();
                    }
                    return Mono.just(new
                            ResponseEntity<>(String.format("Institution %s has license status %s for gameType %s , and it is meant to be LICENSED",
                            institutionName, gameTypeName, licenseStatusDto.getName()), HttpStatus.BAD_REQUEST));
                }
            } else {
                return Mono.just(new ResponseEntity<>("Invalid license status found for institution license", HttpStatus.BAD_REQUEST));
            }
        } else {
            Institution institution = institutionService.findById(institutionId);
            String institutionName = institutionId;
            if (institution != null) {
                institutionName = institution.getInstitutionName();
            }
            return Mono.just(new ResponseEntity<>(String.format("Institution %s does not have an existing license record for gameType %s", institutionName, gameTypeName), HttpStatus.BAD_REQUEST));
        }
        return null;
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
            Institution institution = institutionService.findById(institutionId);
            String institutionName = institutionId;
            if (institution != null) {
                institutionName = institution.getInstitutionName();
            }
            return Mono.just(new ResponseEntity<>(String.format("Institution %s does not have an existing license record for gameType %s", institutionName, gameTypeName), HttpStatus.BAD_REQUEST));
        }
    }
}
