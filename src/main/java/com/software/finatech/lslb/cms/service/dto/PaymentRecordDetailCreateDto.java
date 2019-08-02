package com.software.finatech.lslb.cms.service.dto;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class PaymentRecordDetailCreateDto {
    @NotNull(message = "please provide amount")
    private double amount;
    private String paymentRecordId;
    private String feeId;
    private String institutionId;
    private String agentId;
    private Set<String> gamingMachineIds = new HashSet<>();
    private Set<String> gamingTerminalIds = new HashSet<>();
    private String licenseTransferId;

    public String getLicenseTransferId() {
        return licenseTransferId;
    }

    public void setLicenseTransferId(String licenseTransferId) {
        this.licenseTransferId = licenseTransferId;
    }

    public Set<String> getGamingMachineIds() {
        return gamingMachineIds;
    }

    public void setGamingMachineIds(Set<String> gamingMachineIds) {
        this.gamingMachineIds = gamingMachineIds;
    }

    public Set<String> getGamingTerminalIds() {
        return gamingTerminalIds;
    }

    public void setGamingTerminalIds(Set<String> gamingTerminalIds) {
        this.gamingTerminalIds = gamingTerminalIds;
    }

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

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
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
                && this.gamingMachineIds.isEmpty()
                && this.gamingTerminalIds.isEmpty()
                && !StringUtils.isEmpty(this.getInstitutionId());
    }

    public boolean isAgentPayment() {
        return !StringUtils.isEmpty(this.getAgentId())
                && this.gamingTerminalIds.isEmpty()
                && this.gamingMachineIds.isEmpty()
                && StringUtils.isEmpty(this.getInstitutionId());
    }

    public boolean isGamingMachinePayment() {
        return StringUtils.isEmpty(this.getAgentId())
                && !this.gamingMachineIds.isEmpty()
                && this.gamingTerminalIds.isEmpty()
                && !StringUtils.isEmpty(this.getInstitutionId())
                && StringUtils.isEmpty(this.licenseTransferId);
    }

    public boolean isGamingTerminalPayment() {
        return StringUtils.isEmpty(this.institutionId)
                && !StringUtils.isEmpty(this.agentId)
                && this.gamingMachineIds.isEmpty()
                && !this.gamingTerminalIds.isEmpty()
                && StringUtils.isEmpty(this.licenseTransferId);
    }

    public boolean isFirstPayment() {
        return StringUtils.isEmpty(this.paymentRecordId);
    }
}
