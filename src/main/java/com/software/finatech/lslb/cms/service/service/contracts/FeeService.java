package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.dto.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FeeService {
    Mono<ResponseEntity> createFee(FeeCreateDto feeCreateDto, HttpServletRequest request);
    Mono<ResponseEntity> getAllFees(String feePaymentTypeId, String gameTypeId, String revenueNameId);
    Mono<ResponseEntity> getAllFeePaymentType();
    Mono<ResponseEntity> updateFeePaymentType(FeePaymentTypeDto feeTypeCreateDto, HttpServletRequest request);
    Mono<ResponseEntity> createFeePaymentType(FeePaymentTypeDto feeTypeCreateDto, HttpServletRequest request);
    Mono<ResponseEntity> createRevenueName(RevenueNameDto revenueNameDto, HttpServletRequest request);
    List<EnumeratedFactDto> getRevenueNames();
    List<FeesTypeDto>  getAllFeesType();

    Mono<ResponseEntity> findActiveFeeByGameTypeAndPaymentTypeAndRevenueName(String gameTypeId, String feePaymentTypeId, String revenueNameId);
    Fee findFeeById(String feeId);

    Mono<ResponseEntity> findAllFeePaymentTypeForRevenueName(String revenueNameId);

    Mono<ResponseEntity> findRevenueNameByParams(String institutionId, String agentId);

    Fee findFeeByRevenueNameGameTypeAndFeePaymentType(String revenueNameId, String gameTypeId, String feePaymentTypeId);

    FeePaymentType getFeePaymentTypeById(String feePaymentTypeId);
}
