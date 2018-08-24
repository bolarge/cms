package com.software.finatech.lslb.cms.service.dto;

public class PaymentRecordDto {
    private String approverName;
    private String institutionId;
    private EnumeratedFactDto paymentStatus;
    private FeeDto fee;
    private EnumeratedFactDto feePaymentType;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
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
