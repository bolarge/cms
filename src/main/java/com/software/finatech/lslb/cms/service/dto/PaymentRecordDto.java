package com.software.finatech.lslb.cms.service.dto;

public class PaymentRecordDto {
    private String institutionName;
    private String institutionId;
    private String approverName;
    private EnumeratedFactDto paymentStatus;
    private FeeDto fee;
    private String id;
    private String parentLicenseId;
    private AgentDto agent;
    private GamingMachineDto gamingMachine;


    public AgentDto getAgent() {
        return agent;
    }

    public void setAgent(AgentDto agent) {
        this.agent = agent;
    }

    public GamingMachineDto getGamingMachine() {
        return gamingMachine;
    }

    public void setGamingMachine(GamingMachineDto gamingMachine) {
        this.gamingMachine = gamingMachine;
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
