package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

public class PaymentRecordDto {
    private String institutionId;
    private String approverName;
    private String apprcverId;
    private String paymentStatusId;
    private String paymentStatusName;
    private String gameTypeName;
    private String gameTypeId;
    private String feeId;
    private double amountPaid;
    private double amountOutstanding;
    private double amount;
    private String id;
    private String parentLicenseId;
    private String agentId;
    private String ownerName;
    private String startYear;
    private String endYear;
    private String feePaymentTypeName;
    private String feePaymentTypeId;
    private String revenueName;
    private String revenueNameId;
    private List<MachineDto> gamingMachines = new ArrayList<>();
    private List<MachineDto> gamingTerminals = new ArrayList<>();
    private String paymentReference;
    private MachineMultiplePayment machineMultiplePayment;
    private String creationDate;
    private String completionDate;
    private Boolean forOutsideSystemPayment;


    public Boolean getForOutsideSystemPayment() {
        return forOutsideSystemPayment;
    }

    public void setForOutsideSystemPayment(Boolean forOutsideSystemPayment) {
        this.forOutsideSystemPayment = forOutsideSystemPayment;
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public MachineMultiplePayment getMachineMultiplePayment() {
        return machineMultiplePayment;
    }

    public void setMachineMultiplePayment(MachineMultiplePayment machineMultiplePayment) {
        this.machineMultiplePayment = machineMultiplePayment;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public List<MachineDto> getGamingMachines() {
        return gamingMachines;
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

    public String getFeePaymentTypeId() {
        return feePaymentTypeId;
    }

    public void setFeePaymentTypeId(String feePaymentTypeId) {
        this.feePaymentTypeId = feePaymentTypeId;
    }

    public String getRevenueNameId() {
        return revenueNameId;
    }

    public void setRevenueNameId(String revenueNameId) {
        this.revenueNameId = revenueNameId;
    }

    public String getFeePaymentTypeName() {
        return feePaymentTypeName;
    }

    public void setFeePaymentTypeName(String feePaymentTypeName) {
        this.feePaymentTypeName = feePaymentTypeName;
    }

    public String getRevenueName() {
        return revenueName;
    }

    public void setRevenueName(String revenueName) {
        this.revenueName = revenueName;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getParentLicenseId() {
        return parentLicenseId;
    }

    public void setParentLicenseId(String parentLicenseId) {
        this.parentLicenseId = parentLicenseId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getApprcverId() {
        return apprcverId;
    }

    public void setApprcverId(String apprcverId) {
        this.apprcverId = apprcverId;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getPaymentStatusName() {
        return paymentStatusName;
    }

    public void setPaymentStatusName(String paymentStatusName) {
        this.paymentStatusName = paymentStatusName;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getAmountOutstanding() {
        return amountOutstanding;
    }

    public void setAmountOutstanding(double amountOutstanding) {
        this.amountOutstanding = amountOutstanding;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getEndYear() {
        return endYear;
    }
}
