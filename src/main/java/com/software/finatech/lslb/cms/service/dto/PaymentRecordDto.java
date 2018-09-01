package com.software.finatech.lslb.cms.service.dto;

public class PaymentRecordDto {
    private String institutionName;
    private String institutionId;
    private String approverName;
    private EnumeratedFactDto paymentStatus;
    private FeeDto fee;
    private String id;
    private String parentLicenseId;
    private String agentId;
    private String gamingMachineId;


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


    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getParentLicenseId() {
        return parentLicenseId;
    }

    public void setParentLicenseId(String parentLicenseId) {
        this.parentLicenseId = parentLicenseId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public FeeDto getFee() {
        return fee;
    }

    public void setFee(FeeDto fee) {
        this.fee = fee;
    }
}
