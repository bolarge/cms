package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.LicenseUpdateAIPToLicenseDto;
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
                                        String paymentRecordId,
                                        String date,
                                        String licenseTypeId, HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllLicenseStatus();
    Mono<ResponseEntity> findLicense(String licenseId, String institutionId, String agentId, String gamingMachineId,String gameTypeId);
    Mono<ResponseEntity> getExpiringLicenses();
    Mono<ResponseEntity> getExpiringAIPs();
    Mono<ResponseEntity> getExpiredLicenses();
    Mono<ResponseEntity> getExpiredAIPs();
    List<EnumeratedFactDto> getLicenseStatus();
    List<EnumeratedFactDto> getAllLicenseTypes();
    Mono<ResponseEntity> getInstitutionAIPs(String institutionId);
    Mono<ResponseEntity> updateToDocumentAIP(String licenseId);
    Mono<ResponseEntity> updateAIPDocToLicense(LicenseUpdateAIPToLicenseDto licenseUpdateDto);
    License findRenewalLicense(String institutionId, String agentId, String gamingMachineId, String gameTypeId, String licenseTypeId);
    boolean institutionIsLicensedForGameType(String institutionId, String gameTypeId);

    void createAIPLicenseForCompletedPayment(PaymentRecord paymentRecord);

    void createFirstLicenseForAgentPayment(PaymentRecord paymentRecord);

    void createFirstLicenseForGamingMachinePayment(PaymentRecord paymentRecord);

    void createRenewedLicenseForPayment(PaymentRecord paymentRecord);

}