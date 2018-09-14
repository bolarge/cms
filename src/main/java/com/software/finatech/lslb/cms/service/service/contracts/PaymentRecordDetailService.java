package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.PaymentRecordDetail;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayInBranchNotification;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface PaymentRecordDetailService {

    Mono<ResponseEntity> createInBranchPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto);

    Mono<ResponseEntity> createWebPaymentPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto);

    void savePaymentRecordDetail(PaymentRecordDetail paymentRecordDetail);

    Mono<ResponseEntity> findAllPaymentRecordDetailForPaymentRecord(String paymentRecordId);

    Mono<ResponseEntity> updateWebPaymentRecordDetail(PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto);

    PaymentRecordDetail findById(String paymentRecordDetailId);

    Mono<ResponseEntity> handleVigipayInBranchNotification(VigipayInBranchNotification vigipayInBranchNotification);
}
