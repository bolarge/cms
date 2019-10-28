package com.software.finatech.lslb.cms.service.dto;

public class PaymentRecordDetailDto {
    private Double amount;
    private double amountToBePaid;
    private String paymentStatus;
    private String creationDate;
    private String paymentDate;
    private String invoiceNumber;
    private String modeOfPaymentName;
    private String modeOfPaymentId;
    private String id;
    private String paymentRecordId;
    private String vigiPayReference;
    private String paymentStatusId;
    private String tellerNumber;
    private String bankName;
    private String paymentConfirmationApprovalRequest;
    private String tellerDate;

    public String getTellerDate() {
        return tellerDate;
    }

    public void setTellerDate(String tellerDate) {
        this.tellerDate = tellerDate;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getModeOfPaymentId() {
        return modeOfPaymentId;
    }

    public void setModeOfPaymentId(String modeOfPaymentId) {
        this.modeOfPaymentId = modeOfPaymentId;
    }

    public String getVigiPayReference() {
        return vigiPayReference;
    }

    public void setVigiPayReference(String vigiPayReference) {
        this.vigiPayReference = vigiPayReference;
    }

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

    public double getAmountToBePaid() {
        return amountToBePaid;
    }

    public void setAmountToBePaid(double amountToBePaid) {
        this.amountToBePaid = amountToBePaid;
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

    public String getPaymentConfirmationApprovalRequest() {
        return paymentConfirmationApprovalRequest;
    }

    public void setPaymentConfirmationApprovalRequest(String paymentConfirmationApprovalRequest) {
        this.paymentConfirmationApprovalRequest = paymentConfirmationApprovalRequest;
    }
}
