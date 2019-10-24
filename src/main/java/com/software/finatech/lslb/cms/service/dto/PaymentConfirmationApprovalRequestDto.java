package com.software.finatech.lslb.cms.service.dto;

public class PaymentConfirmationApprovalRequestDto extends AbstractApprovalRequestDto {
    private PaymentRecordDto paymentRecord;
    private PaymentRecordDetailDto paymentRecordDetail;
    private String paymentOwnerName;
    /*
   Added to meet implementation logic of Offline Payment Processing
    */
    private String invoiceNumber;
    private String tellerNumber;
    private String bankName;

    public PaymentRecordDetailDto getPaymentRecordDetail() {
        return paymentRecordDetail;
    }

    public void setPaymentRecordDetail(PaymentRecordDetailDto paymentRecordDetail) {
        this.paymentRecordDetail = paymentRecordDetail;
    }

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

    public String getInvoiceNumber() { return invoiceNumber; }

    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getTellerNumber() { return tellerNumber; }

    public void setTellerNumber(String tellerNumber) { this.tellerNumber = tellerNumber; }

    public String getBankName() { return bankName; }

    public void setBankName(String bankName) { this.bankName = bankName; }

}