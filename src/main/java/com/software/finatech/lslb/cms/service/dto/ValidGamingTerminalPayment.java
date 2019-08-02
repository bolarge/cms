package com.software.finatech.lslb.cms.service.dto;

public class ValidGamingTerminalPayment {
    private String gamingTerminalId;
    private double amount;
    private String gameTypeName;
    private String serialNumber;
    private String feePaymentTypeName;


    public String getFeePaymentTypeName() {
        return feePaymentTypeName;
    }

    public void setFeePaymentTypeName(String feePaymentTypeName) {
        this.feePaymentTypeName = feePaymentTypeName;
    }

    public String getGamingTerminalId() {
        return gamingTerminalId;
    }

    public void setGamingTerminalId(String gamingTerminalId) {
        this.gamingTerminalId = gamingTerminalId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
