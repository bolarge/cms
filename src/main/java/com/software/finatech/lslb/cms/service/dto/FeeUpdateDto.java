package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class FeeUpdateDto {
    @NotEmpty(message = "Please provide amount")
    protected double amount;
    @NotEmpty(message = "Please provide gameTypeId")
    protected String gameTyeId;
    @NotEmpty(message = "Please provide fee payment type id")
    protected String feePaymentTypeId;
    protected String revenueName;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getGameTyeId() {
        return gameTyeId;
    }

    public void setGameTyeId(String gameTyeId) {
        this.gameTyeId = gameTyeId;
    }

    public String getFeePaymentTypeId() {
        return feePaymentTypeId;
    }

    public void setFeePaymentTypeId(String feePaymentTypeId) {
        this.feePaymentTypeId = feePaymentTypeId;
    }

    public String getRevenueName() {
        return revenueName;
    }

    public void setRevenueName(String revenueName) {
        this.revenueName = revenueName;
    }
}
