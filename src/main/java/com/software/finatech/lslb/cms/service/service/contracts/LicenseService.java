package com.software.finatech.lslb.cms.service.service.contracts;

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
                                        String paymentRecordId,
                                        HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> findLicenseById(String licenseId);

    Mono<ResponseEntity> getAllLicenseStatus();
}
