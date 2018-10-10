package com.software.finatech.lslb.cms.service.dto;

import io.advantageous.boon.core.Str;

import java.util.HashSet;
import java.util.Set;

public class BatchPaymentDto {
    private String id;
    private String ownerName;
    private String agentId;
    private String institutionId;
    private double amountTotal;
    private Set<PaymentRecordDto> paymentRecords = new HashSet<>();


    public double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(double amountTotal) {
        this.amountTotal = amountTotal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public Set<PaymentRecordDto> getPaymentRecords() {
        return paymentRecords;
    }

    public void setPaymentRecords(Set<PaymentRecordDto> paymentRecords) {
        this.paymentRecords = paymentRecords;
    }
}
