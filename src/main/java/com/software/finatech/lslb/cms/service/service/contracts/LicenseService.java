package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.LicenseUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface LicenseService {

    Mono<ResponseEntity> findAllLicense(int page,
                                        int pageSize,
                                        String sortDirection,
                                        String sortProperty,
                                        String institutionId,
                                        String agentId,
                                        String gamingMachineId,
                                        String licenseStatusId,
                                        String gameTypeId,
                                        String paymentRecordId, HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> findLicenseById(String licenseId);

    Mono<ResponseEntity> getAllLicenseStatus();
    Mono<ResponseEntity> findLicense(String institutionId, String agentId, String gamingMachineId,String gameTypeId);
    Mono<ResponseEntity> getExpiringLicenses();
    Mono<ResponseEntity> getExpiringAIPs();
    Mono<ResponseEntity> getExpiredLicenses();
    Mono<ResponseEntity> getExpiredAIPs();
    Mono<ResponseEntity> updateLicense(LicenseUpdateDto licenseUpdateDto);


}
