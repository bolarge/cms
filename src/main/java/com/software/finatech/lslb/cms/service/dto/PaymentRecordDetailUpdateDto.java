package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;

public class PaymentRecordDetailUpdateDto {
    private String invoiceNumber;
    private String tellerNumber;
    private LocalDate tellerPaymentDate;
    private String bankName;
    private double amount;
    private double amountToBePaid;
    private String paymentRecordId;
    private String paymentConfirmationApprovalRequestType;
    @NotEmpty(message = "Please provide payment status id")
    private String paymentStatusId;
    @NotEmpty(message = "please provide the id of the entity")
    private String id;
    private String vigipayReference;

    public PaymentRecordDetailUpdateDto() {
    }

    public static PaymentRecordDetailUpdateDto fromIdAndPaymentStatus(String id, String paymentStatusId) {
        PaymentRecordDetailUpdateDto detailUpdateDto = new PaymentRecordDetailUpdateDto();
        detailUpdateDto.setPaymentStatusId(paymentStatusId);
        detailUpdateDto.setId(id);
        return detailUpdateDto;
    }

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

    public boolean isSuccessFulPayment() {
        return StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, this.paymentStatusId);
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

    public double getAmountToBePaid() {
        return amountToBePaid;
    }

    public void setAmountToBePaid(double amountToBePaid) {
        this.amountToBePaid = amountToBePaid;
    }

    public String getTellerNumber() { return tellerNumber; }

    public void setTellerNumber(String tellerNumber) { this.tellerNumber = tellerNumber; }

    public String getBankName() { return bankName; }

    public void setBankName(String bankName) { this.bankName = bankName; }

    public LocalDate getTellerPaymentDate() { return tellerPaymentDate; }

    public void setTellerPaymentDate(LocalDate tellerPaymentDate) { this.tellerPaymentDate = tellerPaymentDate; }

    public String getPaymentConfirmationApprovalRequestType() { return paymentConfirmationApprovalRequestType; }

    public void setPaymentConfirmationApprovalRequestType(String paymentConfirmationApprovalRequestType) {
        this.paymentConfirmationApprovalRequestType = paymentConfirmationApprovalRequestType; }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public double getAmount() { return amount; }

    public void setAmount(double amount) { this.amount = amount; }
}
