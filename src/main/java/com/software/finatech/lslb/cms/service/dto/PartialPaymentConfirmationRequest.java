package com.software.finatech.lslb.cms.service.dto;

public class PartialPaymentConfirmationRequest {
    private String paymentRecordId;
    private double amount;
    private String lastTellerNumber;
    private String bankName;

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

    public String getLastTellerNumber() {
        return lastTellerNumber;
    }

    public void setLastTellerNumber(String lastTellerNumber) {
        this.lastTellerNumber = lastTellerNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
