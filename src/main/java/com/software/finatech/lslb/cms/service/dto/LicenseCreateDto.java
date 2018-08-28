package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LicenseCreateDto {

    protected String licenseStatusId;
    @NotEmpty(message = "Please provide renewalCheckStatus")
    protected String renewalCheck;
    protected String paymentRecordId;

    private String id;

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getRenewalCheck() {
        return renewalCheck;
    }

    public void setRenewalCheck(String renewalCheck) {
        this.renewalCheck = renewalCheck;
    }


    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }


}
