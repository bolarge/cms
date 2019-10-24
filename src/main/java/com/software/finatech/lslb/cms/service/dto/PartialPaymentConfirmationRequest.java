package com.software.finatech.lslb.cms.service.dto;

public class PartialPaymentConfirmationRequest {
    private String paymentRecordId;
    private double amount;
    private String tellerNumber;
    private String bankName;
    private String tellerDate;
    private String invoiceNumber;
    private String paymentConfirmationApprovalRequestType;

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

    public String getTellerNumber() {
        return tellerNumber;
    }

    public void setTellerNumber(String tellerNumber) {
        this.tellerNumber = tellerNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getTellerDate() { return tellerDate; }

    public void setTellerDate(String tellerDate) { this.tellerDate = tellerDate; }

    public String getInvoiceNumber() { return invoiceNumber; }

    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getPaymentConfirmationApprovalRequestType() { return paymentConfirmationApprovalRequestType; }

    public void setPaymentConfirmationApprovalRequestType(String paymentConfirmationApprovalRequestType) {
        this.paymentConfirmationApprovalRequestType = paymentConfirmationApprovalRequestType;
    }
}
