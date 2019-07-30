package com.software.finatech.lslb.cms.service.dto;

public class PartialPaymentConfirmationRequest {
    private String paymentRecordId;
    private double amount;

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
