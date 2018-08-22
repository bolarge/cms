package com.software.finatech.lslb.cms.service.service.contracts;

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
                                              String feePaymentTypeId,
                                              HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllPaymentStatus();
}
