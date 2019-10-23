package com.software.finatech.lslb.cms.service.dto;

public class PartialPaymentConfirmationRequest {
    private String paymentRecordId;
    private double amount;
    private String lastTellerNumber;
    private String bankName;
    private String tellerDate;
    //private String paymentConfirmationApprovalRequestType;

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

    public String getTellerDate() { return tellerDate; }

    public void setTellerDate(String tellerDate) { this.tellerDate = tellerDate; }

    /*public String getPaymentConfirmationApprovalRequestType() { return paymentConfirmationApprovalRequestType; }

    public void setPaymentConfirmationApprovalRequestType(String paymentConfirmationApprovalRequestType) {
        this.paymentConfirmationApprovalRequestType = paymentConfirmationApprovalRequestType;
    }*/
}
