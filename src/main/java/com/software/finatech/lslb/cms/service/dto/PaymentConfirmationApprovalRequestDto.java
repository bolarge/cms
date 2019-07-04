package com.software.finatech.lslb.cms.service.dto;

public class PaymentConfirmationApprovalRequestDto extends AbstractApprovalRequestDto {
    private PaymentRecordDto paymentRecord;
    private String paymentOwnerName;

    public String getPaymentOwnerName() {
        return paymentOwnerName;
    }

    public void setPaymentOwnerName(String paymentOwnerName) {
        this.paymentOwnerName = paymentOwnerName;
    }

    public PaymentRecordDto getPaymentRecord() {
        return paymentRecord;
    }

    public void setPaymentRecord(PaymentRecordDto paymentRecord) {
        this.paymentRecord = paymentRecord;
    }
}