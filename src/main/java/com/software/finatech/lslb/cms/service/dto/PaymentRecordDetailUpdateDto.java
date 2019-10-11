package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;

public class PaymentRecordDetailUpdateDto {
    private String invoiceNumber;
    private String tellerNumber;
    private String tellerPaymentDate;
    private String bankName;
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

    public String getTellerNumber() { return tellerNumber; }

    public void setTellerNumber(String tellerNumber) { this.tellerNumber = tellerNumber; }

    public String getBankName() { return bankName; }

    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getTellerPaymentDate() { return tellerPaymentDate; }

    public void setTellerPaymentDate(String tellerPaymentDate) { this.tellerPaymentDate = tellerPaymentDate; }

    public String getPaymentConfirmationApprovalRequestType() { return paymentConfirmationApprovalRequestType; }

    public void setPaymentConfirmationApprovalRequestType(String paymentConfirmationApprovalRequestType) {
        this.paymentConfirmationApprovalRequestType = paymentConfirmationApprovalRequestType; }
}
