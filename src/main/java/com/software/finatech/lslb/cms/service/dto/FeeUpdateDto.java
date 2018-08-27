package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class FeeUpdateDto {
    @NotEmpty(message = "Please provide amount")
    protected String amount;
    @NotEmpty(message = "Please provide gameTypeId")
    protected String gameTyeId;
    @NotEmpty(message = "Please provide fee payment type id")
    protected String feePaymentTypeId;
    protected String revenueName;
    protected String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
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
