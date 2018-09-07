package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.LicenseUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
                                        String paymentRecordId, String licenseType, HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllLicenseStatus();
    Mono<ResponseEntity> findLicense(String licenseId, String institutionId, String agentId, String gamingMachineId,String gameTypeId);
    Mono<ResponseEntity> getExpiringLicenses();
    Mono<ResponseEntity> getExpiringAIPs();
    Mono<ResponseEntity> getExpiredLicenses();
    Mono<ResponseEntity> getExpiredAIPs();
    Mono<ResponseEntity> updateLicense(LicenseUpdateDto licenseUpdateDto);
    List<EnumeratedFactDto> getLicenseStatus();
    Mono<ResponseEntity> getInstitutionAIPs(String institutionId);
    Mono<ResponseEntity> updateToDocumentAIP(String licenseId);
    Mono<ResponseEntity> updateAIPDocToLicense(String institutionId, String gameTypeId);

    boolean institutionIsLicensedForGameType(String institutionId, String gameTypeId);
}
