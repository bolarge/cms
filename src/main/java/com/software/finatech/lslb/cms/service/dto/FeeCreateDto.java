package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class FeeCreateDto {
    @NotEmpty(message = "Please provide amount")
    protected String amount;
    @NotEmpty(message = "Please provide gameTypeId")
    protected String gameTypeId;
    @NotEmpty(message = "Please provide fee payment type id")
    protected String feePaymentTypeId;
    @NotEmpty(message = "Please provide payment type duration")
    protected String duration;

    protected String revenueName;

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

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
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
