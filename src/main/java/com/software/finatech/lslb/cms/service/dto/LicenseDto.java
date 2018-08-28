package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.*;
public class LicenseDto {
    protected LicenseStatusDto licenseStatus;
    protected Institution institution;
    protected String id;
    protected PaymentRecordDto paymentRecord;
    protected Fee fee;
    protected String startDate;
    protected String endDate;
    protected String renewalStatus;

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

    public void setRenewalStatus(String renewalStatus) {
        this.renewalStatus = renewalStatus;
    }

    public String getRenewalStatus() {
        return renewalStatus;
    }

    public LicenseStatusDto getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(LicenseStatusDto licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentRecordDto getPaymentRecord() {
        return paymentRecord;
    }

    public void setPaymentRecord(PaymentRecordDto paymentRecord) {
        this.paymentRecord = paymentRecord;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

}
