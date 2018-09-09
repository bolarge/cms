package com.software.finatech.lslb.cms.service.dto;

public class PaymentRecordDetailDto {
    private Double amount;
    private String paymentStatus;
    private String creationDate;
    private String paymentDate;
    private String invoiceNumber;
    private String modeOfPaymentName;
    private String id;
    private String paymentRecordId;


    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getModeOfPaymentName() {
        return modeOfPaymentName;
    }

    public void setModeOfPaymentName(String modeOfPaymentName) {
        this.modeOfPaymentName = modeOfPaymentName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
