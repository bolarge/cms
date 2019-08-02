package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

public class PaymentReceiptResponse {
    private String paymentReference;
    private String ownerName;
    private String startDate;
    private String endDate;
    private double amount;
    private String paymentDate;
    private String paymentTime;
    private String feePaymentTypeName;
    private String gameTypeName;
    private String lastModeOfPayment;
    private String revenueName;
    private String licenseStatus;
    private List<MachineDto> gamingMachines = new ArrayList<>();
    private List<MachineDto> gamingTerminals = new ArrayList<>();
    private String licenseNumber;


    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(String licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public List<MachineDto> getGamingMachines() {
        return gamingMachines;
    }

    public void setGamingMachines(List<MachineDto> gamingMachines) {
        this.gamingMachines = gamingMachines;
    }

    public List<MachineDto> getGamingTerminals() {
        return gamingTerminals;
    }

    public void setGamingTerminals(List<MachineDto> gamingTerminals) {
        this.gamingTerminals = gamingTerminals;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getFeePaymentTypeName() {
        return feePaymentTypeName;
    }

    public void setFeePaymentTypeName(String feePaymentTypeName) {
        this.feePaymentTypeName = feePaymentTypeName;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getLastModeOfPayment() {
        return lastModeOfPayment;
    }

    public void setLastModeOfPayment(String lastModeOfPayment) {
        this.lastModeOfPayment = lastModeOfPayment;
    }

    public String getRevenueName() {
        return revenueName;
    }

    public void setRevenueName(String revenueName) {
        this.revenueName = revenueName;
    }
}
