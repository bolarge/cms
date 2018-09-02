package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.GamingMachine;

public class PaymentRecordDto {
    private String institutionName;
    private String institutionId;
    private String approverName;
    private EnumeratedFactDto paymentStatus;
    private FeeDto fee;
    private String id;
    private String parentLicenseId;
    private Agent agent;
    private GamingMachine gamingMachine;


    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public GamingMachine getGamingMachine() {
        return gamingMachine;
    }

    public void setGamingMachine(GamingMachine gamingMachine) {
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
