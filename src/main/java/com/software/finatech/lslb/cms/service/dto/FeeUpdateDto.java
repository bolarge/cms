package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class FeeUpdateDto {
    @NotEmpty(message = "Please provide amount")
    protected String amount;
    @NotEmpty(message = "Please provide gameTypeId")
    protected String gameTypeId;
    @NotEmpty(message = "Please provide fee payment type id")
    protected String feePaymentTypeId;
    protected String duration;
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

   }
