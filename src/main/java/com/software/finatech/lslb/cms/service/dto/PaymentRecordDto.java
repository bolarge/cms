package com.software.finatech.lslb.cms.service.dto;

public class PaymentRecordDto {
    private String approverName;
    private EnumeratedFactDto paymentStatus;
    private FeeDto fee;
    private EnumeratedFactDto feePaymentType;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EnumeratedFactDto getFeePaymentType() {
        return feePaymentType;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public EnumeratedFactDto getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(EnumeratedFactDto paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setFeePaymentType(EnumeratedFactDto feePaymentType) {
        this.feePaymentType = feePaymentType;
    }

    public FeeDto getFee() {
        return fee;
    }

    public void setFee(FeeDto fee) {
        this.fee = fee;
    }
}
