package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
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
                                        String licenseNumber,
                                        String licenseTypeId,
                                        String startDate,
                                        String endDate,
                                        String dateProperty,
                                        HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllLicenseStatus();

    Mono<ResponseEntity> findLicense(String licenseId, String institutionId, String agentId, String gamingMachineId, String gameTypeId);

    Mono<ResponseEntity> getExpiringLicenses();

    Mono<ResponseEntity> getExpiringAIPs();

    Mono<ResponseEntity> getExpiredLicenses();

    Mono<ResponseEntity> getExpiredAIPs();

    Mono<ResponseEntity> getAllLicenseTypes();

    Mono<ResponseEntity> getInstitutionAIPs(String institutionId);

    Mono<ResponseEntity> getInstitutionCloseToExpirationLicenses(String institutionId);

    Mono<ResponseEntity> getAgentLicensesCloseToExpiration(String agentId);

    Mono<ResponseEntity> getGamingMachineLicensesCloseToExpiration(String gamingMachineId);

    Mono<ResponseEntity> updateRenewalLicenseToReview(String paymentRecordId);

    Mono<ResponseEntity> getInstitutionAIPUploaded(String institutionId);

    Mono<ResponseEntity> getLicensesInRenewalInReview(String institutionId);

    //Mono<ResponseEntity> updateRenewalReviewToInProgress(RenewalFormCommentDto renewalFormCommentDto);
    License findRenewalLicense(String institutionId, String agentId, String gamingMachineId, String gameTypeId, String licenseTypeId);

    boolean institutionIsLicensedForGameType(String institutionId, String gameTypeId);

    void createAIPLicenseForCompletedPayment(PaymentRecord paymentRecord);

    void createFirstLicenseForAgentPayment(PaymentRecord paymentRecord);

    void createLicenseForGamingMachinePayment(PaymentRecord paymentRecord);

    void createLicenseForGamingTerminalPayment(PaymentRecord paymentRecord);

    void createRenewedLicenseForPayment(PaymentRecord paymentRecord);

    License findLicenseById(String id);

    License findInstitutionActiveLicenseInGameType(String institutionId, String gameTypeId);
}