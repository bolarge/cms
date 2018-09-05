package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

    Mono<ResponseEntity> updatePaymentRecord(PaymentRecordUpdateDto paymentRecordUpdateDto);

    Mono<ResponseEntity> createPaymentRecord(PaymentRecordCreateDto paymentRecordCreateDto);

    List<PaymentRecord> findPayments(String institutionId, String agentId, String gamingMachineId, String gameTypeId, String startYear);

    PaymentRecord findById(String paymentRecordId);

    void savePaymentRecord(PaymentRecord paymentRecord);
}