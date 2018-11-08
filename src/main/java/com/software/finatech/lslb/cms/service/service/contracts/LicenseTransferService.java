package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.LicenseTransfer;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.LicenseTrannsferCreateRequest;
import com.software.finatech.lslb.cms.service.dto.LicenseTransferSetToInstitutionRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LicenseTransferService {
    Mono<ResponseEntity> findAllLicenseTransfers(int page,
                                                 int pageSize,
                                                 String sortType,
                                                 String sortParam,
                                                 String fromInstitutionId,
                                                 String toInstitutionId,
                                                 String statusId,
                                                 String gameTypeId,
                                                 HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> createLicenseTransfer(LicenseTrannsferCreateRequest licenseTransferCreateRequest, HttpServletRequest request);

    Mono<ResponseEntity> setToFromInstitution(LicenseTransferSetToInstitutionRequest setToInstitutionRequest, HttpServletRequest request);

    Mono<ResponseEntity> approveLicenseTransfer(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request);

    Mono<ResponseEntity> rejectLicenseTransfer(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request);

    LicenseTransfer findLicenseTransferById(String id);

    Mono<ResponseEntity> getLicenseTransferFullDetail(String id);

    Mono<ResponseEntity> getAllLicenseTransferStatus();

    Mono<ResponseEntity> getAllLicenseTransferForPayment(String institutionId, String gameTypeId);
}
