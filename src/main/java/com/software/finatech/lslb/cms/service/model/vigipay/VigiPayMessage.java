package com.software.finatech.lslb.cms.service.model.vigipay;

public class VigiPayMessage {
    private String PaymentId;
    private String PaymentReference;
    private String InvoiceNumber;
    private double InvoiceAmount;
    private double AmountPaid;
    private boolean SettlementStatus;
    private String ProcessingDate;
    private String PaymentChannel;
    private String PaymentStatus;
    private String RevenueCode;
    private String InvoiceReference;

    public String getPaymentId() {
        return PaymentId;
    }

    public void setPaymentId(String paymentId) {
        PaymentId = paymentId;
    }

    public String getPaymentReference() {
        return PaymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        PaymentReference = paymentReference;
    }

    public String getInvoiceNumber() {
        return InvoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        InvoiceNumber = invoiceNumber;
    }

    public double getInvoiceAmount() {
        return InvoiceAmount;
    }

    public void setInvoiceAmount(double invoiceAmount) {
        InvoiceAmount = invoiceAmount;
    }

    public double getAmountPaid() {
        return AmountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        AmountPaid = amountPaid;
    }

    public boolean isSettlementStatus() {
        return SettlementStatus;
    }

    public void setSettlementStatus(boolean settlementStatus) {
        SettlementStatus = settlementStatus;
    }

    public String getProcessingDate() {
        return ProcessingDate;
    }

    public void setProcessingDate(String processingDate) {
        ProcessingDate = processingDate;
    }

    public String getPaymentChannel() {
        return PaymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        PaymentChannel = paymentChannel;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getRevenueCode() {
        return RevenueCode;
    }

    public void setRevenueCode(String revenueCode) {
        RevenueCode = revenueCode;
    }

    public String getInvoiceReference() {
        return InvoiceReference;
    }

    public void setInvoiceReference(String invoiceReference) {
        InvoiceReference = invoiceReference;
    }
}
