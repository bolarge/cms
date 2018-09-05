package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class PaymentRecordUpdateDto {
    private String approverId;
    @NotEmpty(message = "Please provide payment record Id")
    private String paymentRecordId;
    @NotEmpty(message = "Please provide payment Status Id")
    private String paymentStatusId;
    @NotEmpty(message = "Please provide start Year")
    private String startYear;
    @NotEmpty(message = "Please provide end Year")
    private String endYear;
    @NotEmpty(message = "Please provide fee Id")
    private String feeId;

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }


}
