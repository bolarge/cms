package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class PaymentRecordDetailUpdateDto {
    private String invoiceNumber;
    @NotEmpty(message = "Please provide payment status id")
    private String paymentStatusId;
    @NotEmpty(message = "please provide the id of the entity")
    private String id;
    private String vigipayReference;

    public String getVigipayReference() {
        return vigipayReference;
    }

    public void setVigipayReference(String vigipayReference) {
        this.vigipayReference = vigipayReference;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
