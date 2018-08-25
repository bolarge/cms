package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LicenseCreateDto {

    protected String licenseStatusId;
    @NotEmpty(message = "Please provide renewalCheckStatus")
    protected String renewalCheck;
    @NotEmpty(message = "Please provide institutionId")
    protected String institutionId;
     @NotEmpty(message = "Please provide institutionId")
     protected String gameTypeId;
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

    public String getGameTypeId() {
        return gameTypeId;
    }

    public String getRenewalCheck() {
        return renewalCheck;
    }

    public void setRenewalCheck(String renewalCheck) {
        this.renewalCheck = renewalCheck;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }


    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }



}
