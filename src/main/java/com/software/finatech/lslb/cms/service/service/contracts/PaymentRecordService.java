package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.PaymentRecordCreateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface PaymentRecordService {

    Mono<ResponseEntity> findAllPaymentRecords(int page,
                                              int pageSize,
                                              String sortDirection,
                                              String sortProperty,
                                              String institutionId,
                                              String approverId,
                                              String feeId,
                                              HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllPaymentStatus();
    Mono<ResponseEntity> createPaymentRecord(PaymentRecordCreateDto paymentRecordCreateDto);
    Mono<ResponseEntity> findPaymentRecords(String institutionId, String gameTypeId,String objectType);


}
