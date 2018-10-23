package com.software.finatech.lslb.cms.service.dto;

public class InvalidGamingTerminalPayment {
    private String gamingTerminalId;
    private String serialNumber;
    private String gameTypeName;
    private String reason;
    private String feePaymentTypeName;

    public InvalidGamingTerminalPayment() {
    }

    public InvalidGamingTerminalPayment(String gamingTerminalId,
                                        String serialNumber,
                                        String reason) {
        this.gamingTerminalId = gamingTerminalId;
        this.serialNumber = serialNumber;
        this.reason = reason;
    }

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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
