package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.AgentInstitution;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;

public class PaymentRecordDetailCreateDto {
    private String modeOfPaymentId;
    private double amount;
    private String paymentRecordId;
    @NotEmpty(message = "please provide feeId")
    private String feeId;
    private String institutionId;
    private String agentId;
    private String gamingMachineId;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
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

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public String getModeOfPaymentId() {
        return modeOfPaymentId;
    }

    public void setModeOfPaymentId(String modeOfPaymentId) {
        this.modeOfPaymentId = modeOfPaymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public boolean isInstitutionPayment() {
        return StringUtils.isEmpty(this.getAgentId())
                && StringUtils.isEmpty(this.getGamingMachineId())
                && !StringUtils.isEmpty(this.getInstitutionId());

    }

    public boolean isAgentPayment() {
        return !StringUtils.isEmpty(this.getAgentId())
                && StringUtils.isEmpty(this.getGamingMachineId())
                && StringUtils.isEmpty(this.getInstitutionId());

    }

    public boolean isGamingMachinePayment() {
        return StringUtils.isEmpty(this.getAgentId())
                && !StringUtils.isEmpty(this.getGamingMachineId())
                && StringUtils.isEmpty(this.getInstitutionId());

    }

    public boolean isFirstPayment() {
        return StringUtils.isEmpty(this.paymentRecordId);
    }
}
