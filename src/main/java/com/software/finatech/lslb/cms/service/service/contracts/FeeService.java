package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.dto.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface FeeService {
    Mono<ResponseEntity> createFee(FeeCreateDto feeCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> getAllFees(String feePaymentTypeId, String gameTypeId, String revenueNameId, int page, int pageSize, HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllFeePaymentType();

    Mono<ResponseEntity> setFeeEndDate(FeeEndDateUpdateDto feeEndDateUpdateDto, HttpServletRequest request);

    Mono<ResponseEntity> updateFeePaymentType(FeePaymentTypeDto feeTypeCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> createFeePaymentType(FeePaymentTypeDto feeTypeCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> createLicenseType(RevenueNameDto revenueNameDto, HttpServletRequest request);

    Mono<ResponseEntity> getLicenseTypes();

    Mono<ResponseEntity> findActiveFeeByGameTypeAndPaymentTypeAndRevenueName(String gameTypeId, String feePaymentTypeId, String revenueNameId);

    Fee findFeeById(String feeId);

    Mono<ResponseEntity> findAllFeePaymentTypeForLicenseType(String licenseTypeId);

    Mono<ResponseEntity> findLicenseTypeByParams(String institutionId, String agentId);

    Fee findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(String licenseTypeId, String gameTypeId, String feePaymentTypeId);

    Fee findMostRecentFeeByLicenseTypeGameTypeAndFeePaymentType(String licenseTypeId, String gameTypeId, String feePaymentTypeId);

    FeePaymentType getFeePaymentTypeById(String feePaymentTypeId);
}
