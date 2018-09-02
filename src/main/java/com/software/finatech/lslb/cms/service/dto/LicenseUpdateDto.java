package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LicenseUpdateDto {

    @NotEmpty(message = "Please provide License Status Id")
    private String licenseStatusId;
    private String startDate;
    @NotEmpty(message = "Please provide Payment Record Id")
    private String paymentRecordId;
    @NotEmpty(message = "Please provide license Type")
    private String licenseType;
//    protected String agentId;
//    protected String gamingMachineId;
//
//    public String getAgentId() {
//        return agentId;
//    }
//
//    public void setAgentId(String agentId) {
//        this.agentId = agentId;
//    }
//
//    public String getGamingMachineId() {
//        return gamingMachineId;
//    }
//
//    public void setGamingMachineId(String gamingMachineId) {
//        this.gamingMachineId = gamingMachineId;
//    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }




}
