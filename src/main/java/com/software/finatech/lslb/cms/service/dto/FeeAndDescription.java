package com.software.finatech.lslb.cms.service.dto;

public class FeeAndDescription {
    private String feeDescription;
    private double amount;

    public String getFeeDescription() {
        return feeDescription;
    }

    public void setFeeDescription(String feeDescription) {
        this.feeDescription = feeDescription;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
