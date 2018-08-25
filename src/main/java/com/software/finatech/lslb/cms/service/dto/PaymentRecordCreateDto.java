package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class PaymentRecordCreateDto {
    private String approverId;
    @NotEmpty(message = "Please provide institutionId")
    private String institutionId;
    @NotEmpty(message = "Please provide payment Status Id")
    private String paymentStatusId;
    @NotEmpty(message = "Please provide Fee ID")
    private String feeId;
    @NotEmpty(message = "Please provide Fee Payment TypeId")
    private String feePaymentTypeId;
    @NotEmpty(message = "Please a start Date")
     private String startDate;
    private String endDate;
    private String parentLicenseId;
    @NotEmpty(message = "Please provide Game Type")
    private String gameTypeId;
    private String renewalCheck;

    public String getRenewalCheck() {
        return renewalCheck;
    }

    public void setRenewalCheck(String renewalCheck) {
        this.renewalCheck = renewalCheck;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    public String getParentLicenseId() {
        return parentLicenseId;
    }

    public void setParentLicenseId(String parentLicenseId) {
        this.parentLicenseId = parentLicenseId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
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

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public String getFeePaymentTypeId() {
        return feePaymentTypeId;
    }

    public void setFeePaymentTypeId(String feePaymentTypeId) {
        this.feePaymentTypeId = feePaymentTypeId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

}
