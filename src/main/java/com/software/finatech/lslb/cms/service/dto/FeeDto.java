package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.RevenueName;

public class FeeDto {
    protected double amount;
    protected GameTypeDto gameType;
    protected EnumeratedFactDto feePaymentType;
    protected String id;
    protected boolean active;
    protected EnumeratedFactDto revenueName;

    public EnumeratedFactDto getRevenueName() {
        return revenueName;
    }

    public void setRevenueName(EnumeratedFactDto revenueName) {
        this.revenueName = revenueName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    }
