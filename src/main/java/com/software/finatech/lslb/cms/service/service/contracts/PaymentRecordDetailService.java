package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.PaymentRecordDetail;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface PaymentRecordDetailService {
    Mono<ResponseEntity> createPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto);

    void savePaymentRecordDetail(PaymentRecordDetail paymentRecordDetail);
}
