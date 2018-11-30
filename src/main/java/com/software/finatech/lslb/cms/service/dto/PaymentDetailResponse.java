package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

public class PaymentDetailResponse {
    private List<PaymentRecordDetailDto> paymentRecordDetailDtoList = new ArrayList<>();
    private PaymentRecordDto paymentRecordDto;

    public PaymentDetailResponse(){}

    public PaymentDetailResponse(List<PaymentRecordDetailDto> paymentRecordDetailDtoList,
                                 PaymentRecordDto paymentRecordDto) {
        this.paymentRecordDetailDtoList = paymentRecordDetailDtoList;
        this.paymentRecordDto = paymentRecordDto;
    }

    public List<PaymentRecordDetailDto> getPaymentRecordDetailDtoList() {
        return paymentRecordDetailDtoList;
    }

    public void setPaymentRecordDetailDtoList(List<PaymentRecordDetailDto> paymentRecordDetailDtoList) {
        this.paymentRecordDetailDtoList = paymentRecordDetailDtoList;
    }

    public PaymentRecordDto getPaymentRecordDto() {
        return paymentRecordDto;
    }

    public void setPaymentRecordDto(PaymentRecordDto paymentRecordDto) {
        this.paymentRecordDto = paymentRecordDto;
    }
}
