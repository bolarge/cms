package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.Institution;
public class LicenseDto {
    protected LicenseStatusDto licenseStatus;
    protected String id;
    protected PaymentRecordDto paymentRecord;
    protected Fee fee;
    protected String startDate;
    protected String endDate;
    protected String renewalStatus;
    protected EnumeratedFactDto licenseType;
    protected String agentId;
    protected String gamingMachineId;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
    }
    public EnumeratedFactDto getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(EnumeratedFactDto licenseType) {
        this.licenseType = licenseType;
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
