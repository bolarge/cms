package com.software.finatech.lslb.cms.service.dto;

public class LicenseRecordDto {
    private PaymentRecordDto paymentRecord;
    private String institutionName;
    private String institutionId;
    private String startDate;
    private String endDate;
    private String parentLicenseId;
    private GameTypeDto gameType;


    public PaymentRecordDto getPaymentRecord() {
        return paymentRecord;
    }

    public void setPaymentRecord(PaymentRecordDto paymentRecord) {
        this.paymentRecord = paymentRecord;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
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

    public String getParentLicenseId() {
        return parentLicenseId;
    }

    public void setParentLicenseId(String parentLicenseId) {
        this.parentLicenseId = parentLicenseId;
    }

    public GameTypeDto getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeDto gameType) {
        this.gameType = gameType;
    }
}
