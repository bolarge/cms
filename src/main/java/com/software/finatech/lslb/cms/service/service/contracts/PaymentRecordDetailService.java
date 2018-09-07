package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.PaymentRecordDetail;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface PaymentRecordDetailService {

    Mono<ResponseEntity> createInBranchPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto);

    Mono<ResponseEntity> createWebPaymentPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto);

    void savePaymentRecordDetail(PaymentRecordDetail paymentRecordDetail);

    Mono<ResponseEntity> updatePaymentRecordDetail(PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto);
    PaymentRecordDetail findById(String paymentRecordDetailId);
}
