package com.software.finatech.lslb.cms.service.dto;

/**
 * @author adeyi.adebolu
 * created on 21/06/2019
 */
public class PaymentInvoiceResponse {
    private String gameTypeName;
    private double amount;
    private String ownerName;
    private String feePaymentTypeName;
    private String creationDate;
    private String invoiceNumber;
    private String revenueName;
    private String modeOfPaymentName;
    private String paymentType;

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getModeOfPaymentName() {
        return modeOfPaymentName;
    }

    public void setModeOfPaymentName(String modeOfPaymentName) {
        this.modeOfPaymentName = modeOfPaymentName;
    }

    public String getRevenueName() {
        return revenueName;
    }

    public void setRevenueName(String revenueName) {
        this.revenueName = revenueName;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getFeePaymentTypeName() {
        return feePaymentTypeName;
    }

    public void setFeePaymentTypeName(String feePaymentTypeName) {
        this.feePaymentTypeName = feePaymentTypeName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
