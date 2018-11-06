package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class FeeCreateDto {
    @NotEmpty(message = "Please provide amount")
    protected String amount;
    @NotEmpty(message = "Please provide gameTypeId")
    protected String gameTypeId;
    @NotEmpty(message = "Please provide fee payment type id")
    protected String feePaymentTypeId;
    @NotEmpty(message = "Please provide a revenue id")
    protected String revenueNameId;
    @NotEmpty(message = "Please provide start date")
    private String startDate;
    private String endDate;

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getRevenueNameId() {
        return revenueNameId;
    }

    public void setRevenueNameId(String revenueNameId) {
        this.revenueNameId = revenueNameId;
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
