package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class PaymentRecordCreateDto {
    private String approverId;
    private String institutionId;
    @NotEmpty(message = "Please provide payment Status Id")
    private String paymentStatusId;
    @NotEmpty(message = "Please provide Fee ID")
    private String feeId;
    private String parentLicenseId;
    private String renewalCheck;
    private String agentId;
    private String gamingMachineId;
    @NotEmpty(message = "Please provide start Year")
    private String startYear;

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

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

    public String getRenewalCheck() {
        return renewalCheck;
    }

    public void setRenewalCheck(String renewalCheck) {
        this.renewalCheck = renewalCheck;
    }


    public String getParentLicenseId() {
        return parentLicenseId;
    }

    public void setParentLicenseId(String parentLicenseId) {
        this.parentLicenseId = parentLicenseId;
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


    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

}
