package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FeeService {
    Mono<ResponseEntity> createFee(FeeCreateDto feeCreateDto);
    Mono<ResponseEntity> updateFee(FeeUpdateDto feeUpdateDto);
    Mono<ResponseEntity> getAllFees(String feePaymentTypeId, String gameTypeId);
    Mono<ResponseEntity> getAllFeePaymentType();
    Mono<ResponseEntity> updateFeePaymentType(FeePaymentTypeDto feeTypeCreateDto);
    Mono<ResponseEntity> createFeePaymentType(FeePaymentTypeDto feeTypeCreateDto);
    Mono<ResponseEntity> createRevenueName(RevenueNameDto revenueNameDto);
    List<EnumeratedFactDto> getRevenueNames();
    List<FeesTypeDto>  getAllFeesType();

}
