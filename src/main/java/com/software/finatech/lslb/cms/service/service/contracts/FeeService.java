package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.dto.FeeCreateDto;
import com.software.finatech.lslb.cms.service.dto.FeePaymentTypeDto;
import com.software.finatech.lslb.cms.service.dto.FeeUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface FeeService {
    Mono<ResponseEntity> createFee(FeeCreateDto feeCreateDto);
    Mono<ResponseEntity> updateFee(FeeUpdateDto feeUpdateDto);
    Mono<ResponseEntity> getAllFees(String feePaymentTypeId, String gameTypeId);
    Mono<ResponseEntity> getAllFeePaymentType();
    Mono<ResponseEntity> updateFeePaymentType(FeePaymentTypeDto feeTypeCreateDto);
    Mono<ResponseEntity> createFeePaymentType(FeePaymentTypeDto feeTypeCreateDto);

}
