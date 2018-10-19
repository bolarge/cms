package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
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
                                               String agentId,
                                               String gamingMachineId,
                                               String gameTypeId,
                                               String feePaymentTypeId,
                                               String revenueNameId,
                                               String paymentStatusId,
                                               HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllPaymentStatus();

    List<PaymentRecord> findPayments(String institutionId, String agentId, String gamingMachineId, String gameTypeId, String startYear);

    PaymentRecord findById(String paymentRecordId);

    void savePaymentRecord(PaymentRecord paymentRecord);

    PaymentRecord findExistingConfirmedApplicationFeeForInstitutionAndGameType(String institutionId, String gameTypeId);

    PaymentRecord findPaymentRecordForGamingMachine(String gamingMachineId,
                                                    String gameTypeId,
                                                    String institutionId,
                                                    String feePaymentTypeId);

    Mono<ResponseEntity> findPaymentRecordById(String paymentRecordId);

    Mono<ResponseEntity> getPaymentReceiptDetails(String paymentRecordId);
}