package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.PaymentRecordDetail;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.model.vigipay.VigiPayMessage;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface PaymentRecordDetailService {

    Mono<ResponseEntity> createInBranchPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> createWebPaymentPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, HttpServletRequest request);

    void savePaymentRecordDetail(PaymentRecordDetail paymentRecordDetail);

    Mono<ResponseEntity> findAllPaymentRecordDetailForPaymentRecord(String paymentRecordId);

    Mono<ResponseEntity> updateWebPaymentRecordDetail(PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto, HttpServletRequest request);

    PaymentRecordDetail findById(String paymentRecordDetailId);

    //   Mono<ResponseEntity> handleVigipayInBranchNotification(VigipayInBranchNotification vigipayInBranchNotification);
    Mono<ResponseEntity> handleVigipayInBranchNotification(VigiPayMessage vigiPayMessage);

    Mono<ResponseEntity> getPaymentInvoiceDetails(String id);
}
