package com.software.finatech.lslb.cms.service.dto;

public class InvalidGamingMachinePayment {
    private String gamingMachineId;
    private String machineNumber;
    private String gameTypeName;
    private String reason;
    private String feePaymentTypeName;

    public InvalidGamingMachinePayment() {
    }

    public InvalidGamingMachinePayment(String gamingMachineId,
                                       String machineNumber,
                                       String reason) {
        this.gamingMachineId = gamingMachineId;
        this.machineNumber = machineNumber;
        this.reason = reason;
    }

    public String getFeePaymentTypeName() {
        return feePaymentTypeName;
    }

    public void setFeePaymentTypeName(String feePaymentTypeName) {
        this.feePaymentTypeName = feePaymentTypeName;
    }

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
    }

    public String getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
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
