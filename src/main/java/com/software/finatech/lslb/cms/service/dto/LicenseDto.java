package com.software.finatech.lslb.cms.service.dto;

public class LicenseDto {
    protected String licenseStatusName;
    protected String licenseStatusId;
    private String gameTypeId;
    private String gameTypeName;
    protected String id;
    protected String startDate;
    protected String endDate;
    protected String renewalStatus;
    protected String licenseTypeName;
    private String licenseTypeId;
    protected String gamingMachineId;
    private String paymentRecordId;
    private String licenseNumber;
    private String institutionId;
    protected String agentId;
    private double amountPaid;
    private String ownerName;
    private String licenseChangeReason;
    private String agentNumber;



    public String getLicenseChangeReason() {
        return licenseChangeReason;
    }

    public String getAgentNumber() {
        return agentNumber;
    }

    public void setAgentNumber(String agentNumber) {
        this.agentNumber = agentNumber;
    }

    public void setLicenseChangeReason(String licenseChangeReason) {
        this.licenseChangeReason = licenseChangeReason;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
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

    public void setRenewalStatus(String renewalStatus) {
        this.renewalStatus = renewalStatus;
    }

    public String getRenewalStatus() {
        return renewalStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLicenseStatusName() {
        return licenseStatusName;
    }

    public void setLicenseStatusName(String licenseStatusName) {
        this.licenseStatusName = licenseStatusName;
    }

    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getLicenseTypeName() {
        return licenseTypeName;
    }

    public void setLicenseTypeName(String licenseTypeName) {
        this.licenseTypeName = licenseTypeName;
    }

    public String getLicenseTypeId() {
        return licenseTypeId;
    }

    public void setLicenseTypeId(String licenseTypeId) {
        this.licenseTypeId = licenseTypeId;
    }
}
