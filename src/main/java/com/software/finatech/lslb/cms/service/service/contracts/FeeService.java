package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.FeeCreateDto;
import com.software.finatech.lslb.cms.service.dto.FeeUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface FeeService {
    Mono<ResponseEntity> updateFee(FeeUpdateDto feeUpdateDto);
    Mono<ResponseEntity> getAllFees(String feePaymentTypeId, String gameTypeId);
    Mono<ResponseEntity> getAllFeePaymentType();

    Mono<ResponseEntity> createFee(FeeCreateDto feeCreateDto);
}
