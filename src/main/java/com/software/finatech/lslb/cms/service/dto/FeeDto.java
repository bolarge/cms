package com.software.finatech.lslb.cms.service.dto;

public class FeeDto {
    protected double amount;
    protected GameTypeDto gameType;
    protected EnumeratedFactDto feePaymentType;
    protected String revenueName;
    protected String id;
    protected int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public GameTypeDto getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeDto gameType) {
        this.gameType = gameType;
    }

    public EnumeratedFactDto getFeePaymentType() {
        return feePaymentType;
    }

    public void setFeePaymentType(EnumeratedFactDto feePaymentType) {
        this.feePaymentType = feePaymentType;
    }

    public String getRevenueName() {
        return revenueName;
    }

    public void setRevenueName(String revenueName) {
        this.revenueName = revenueName;
    }
}
