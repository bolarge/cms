package com.software.finatech.lslb.cms.service.dto;

public class PaymentUpdateConfirmationRequest {
    private String paymentRecordDetailId;
    private String tellerNumber;
    private String bankName;
    private String tellerDate;
    private String invoiceNumber;
    private String paymentConfirmationApprovalRequestType;



    public String getPaymentRecordDetailId(){
        return paymentRecordDetailId;
    }

    public void setPaymentRecordDetailId(String paymentRecordDetailId) {
        this.paymentRecordDetailId = paymentRecordDetailId;
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
