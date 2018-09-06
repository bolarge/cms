package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.domain.PaymentRecordDetail;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.FeeService;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordDetailService;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentRecordDetailServiceImpl implements PaymentRecordDetailService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordDetailServiceImpl.class);

    private FeeService feeService;
    private PaymentRecordService paymentRecordService;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public PaymentRecordDetailServiceImpl(FeeService feeService,
                                          PaymentRecordService paymentRecordService,
                                          MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.feeService = feeService;
        this.paymentRecordService = paymentRecordService;
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public Mono<ResponseEntity> createPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        try {
            String paymentRecordId = paymentRecordDetailCreateDto.getPaymentRecordId();
            if (!StringUtils.isEmpty(paymentRecordId)) {
                return Mono.just(new ResponseEntity<>(null, HttpStatus.OK));
            } else {

                //  now check if it is in branch or web payment
                PaymentRecordDetail paymentRecordDetail = savePaymentRecordAndPaymentRecordDetail(paymentRecordDetailCreateDto);
                return Mono.just(new ResponseEntity<>(paymentRecordDetail.convertToDto(), HttpStatus.OK));
            }
        } catch (FactNotFoundException e) {
            return Mono.just(new ResponseEntity<>(String.format("Fee with id %s not found", e.getPropertyName()), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while creating payment record detail", e);
        }
    }


    @Override
    public Mono<ResponseEntity> updatePaymentRecordDetail(PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto) {
        PaymentRecordDetail existingPaymentRecordDetail = findById(paymentRecordDetailUpdateDto.getId());


        return null;
    }

    private PaymentRecordDetail savePaymentRecordAndPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) throws FactNotFoundException {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setId(UUID.randomUUID().toString());
        String feeId = paymentRecordDetailCreateDto.getFeeId();
        Fee fee = feeService.findFeeById(feeId);
        if (fee == null) {
            throw new FactNotFoundException("Fee", feeId);
        }

        PaymentRecordDetail paymentRecordDetail = new PaymentRecordDetail();
        paymentRecordDetail.setId(UUID.randomUUID().toString());
        paymentRecordDetail.setAmount(paymentRecordDetailCreateDto.getAmount());
        paymentRecordDetail.setPaymentDate(DateTime.now());
        paymentRecordDetail.setModeOfPaymentId(paymentRecordDetailCreateDto.getModeOfPaymentId());
        paymentRecordDetail.setInvoiceNumber(paymentRecordDetailCreateDto.getInvoiceNumber());
        paymentRecordDetail.setPaymentStatusId(paymentRecordDetailCreateDto.getPaymentStatusId());
        savePaymentRecordDetail(paymentRecordDetail);


        paymentRecord.setAmount(fee.getAmount());
        paymentRecord.setGamingMachineId(paymentRecordDetailCreateDto.getGamingMachineId());
        paymentRecord.setFeeId(feeId);
        paymentRecord.setInstitutionId(paymentRecordDetailCreateDto.getInstitutionId());
        paymentRecord.setAmountOutstanding(fee.getAmount());
        paymentRecord.setAmountPaid(0);
        paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
        List<String> paymentRecordDetailIds = new ArrayList<>();
        paymentRecordDetailIds.add(paymentRecordDetail.getId());
        paymentRecord.setPaymentRecordDetailIds(paymentRecordDetailIds);
        paymentRecordService.savePaymentRecord(paymentRecord);

        //check if payment is confirmed and subtract
        if (StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecordDetailCreateDto.getPaymentStatusId())) {
            double amountPaid = paymentRecord.getAmountPaid();
            amountPaid = amountPaid + paymentRecordDetail.getAmount();
            paymentRecord.setAmountPaid(amountPaid);

            double amountOutstanding = paymentRecord.getAmountOutstanding();
            amountOutstanding = amountOutstanding - paymentRecordDetail.getAmount();
            paymentRecord.setAmountOutstanding(amountOutstanding);
            if (amountOutstanding <= 0) {
                paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID);
            }
            paymentRecordService.savePaymentRecord(paymentRecord);
        }


        return paymentRecordDetail;
    }

    @Override
    public void savePaymentRecordDetail(PaymentRecordDetail paymentRecordDetail) {
        mongoRepositoryReactive.saveOrUpdate(paymentRecordDetail);
    }

    @Override
    public PaymentRecordDetail findById(String paymentRecordDetailId) {
        return (PaymentRecordDetail) mongoRepositoryReactive.findById(paymentRecordDetailId, PaymentRecordDetail.class).block();
    }
}
