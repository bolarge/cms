package com.software.finatech.lslb.cms.service.dto;

public class BatchPaymentUpdateDto {
    private String batchPaymentId;
    private String paymentStatusId;
    private String invoiceNumber;
    private String vigipayTransactionReference;

    public String getBatchPaymentId() {
        return batchPaymentId;
    }

    public void setBatchPaymentId(String batchPaymentId) {
        this.batchPaymentId = batchPaymentId;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getVigipayTransactionReference() {
        return vigipayTransactionReference;
    }

    public void setVigipayTransactionReference(String vigipayTransactionReference) {
        this.vigipayTransactionReference = vigipayTransactionReference;
    }
}
