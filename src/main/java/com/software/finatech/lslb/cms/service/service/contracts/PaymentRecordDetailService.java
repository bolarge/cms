package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.PaymentRecordDetail;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.model.vigipay.VigiPayMessage;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;

public interface PaymentRecordDetailService {

    Mono<ResponseEntity> createInBranchPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> createWebPaymentPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, HttpServletRequest request);

    void savePaymentRecordDetail(PaymentRecordDetail paymentRecordDetail);

    Mono<ResponseEntity> findAllPaymentRecordDetailForPaymentRecord(String paymentRecordId);

    Mono<ResponseEntity> updatePaymentRecordDetail(PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto, HttpServletRequest request);

    PaymentRecordDetail findById(String paymentRecordDetailId);

    Mono<ResponseEntity> handleVigipayInBranchNotification(VigiPayMessage vigiPayMessage);

    Mono<ResponseEntity> getPaymentInvoiceDetails(String id);
}
