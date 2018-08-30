package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.LicenseUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface LicenseService {

    Mono<ResponseEntity> findAllLicense(int page,
                                        int pageSize,
                                        String sortDirection,
                                        String sortParam,
                                        String institutionId,
                                        String licenseStatusId,
                                        String gameTypeId,
                                        String paymentRecordId,
                                        HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> findLicenseById(String licenseId);

    Mono<ResponseEntity> getAllLicenseStatus();
    Mono<ResponseEntity> findLicenseByInstitutionId(String institutionId, String gameType);
    Mono<ResponseEntity> getExpiringLicenses();
    Mono<ResponseEntity> getExpiringAIPs();
    Mono<ResponseEntity> getExpiredLicenses();
    Mono<ResponseEntity> getExpiredAIPs();
    Mono<ResponseEntity> updateLicense(LicenseUpdateDto licenseUpdateDto);


}
