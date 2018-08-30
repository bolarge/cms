package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class RenewalFormUpdateDto {
    @NotEmpty(message = "Please enter renewal Form Id")
    protected String id;
    @NotEmpty(message = "Please enter payment Record")
    protected String paymentRecordId;
    @NotEmpty(message = "Please enter checkStakeHoldersChange")
    protected Boolean checkStakeHoldersChange;
    protected String stakeHoldersChange;
    @NotEmpty(message = "Please enter checkSharesAquisition")
    protected Boolean checkSharesAquisition;
    protected String sharesAquisition;
    @NotEmpty(message = "Please enter checkConvictedCrime")
    protected Boolean checkConvictedCrime;
    protected String convictedCrime;
    @NotEmpty(message = "Please enter checkPoliticalOffice")
    protected Boolean checkPoliticalOffice;
    protected String politicalOffice;
    @NotEmpty(message = "Please enter checkPoliticalParty")
    protected Boolean checkPoliticalParty;
    protected String politicalParty;
    @NotEmpty(message = "Please enter checkTechnicalPartner")
    protected Boolean checkTechnicalPartner;
    protected String technicalPartner;
    @NotEmpty(message = "Please enter checkChangeInGamingMachines")
    protected Boolean checkChangeInGamingMachines;
    protected String changeInGamingMachines;
    @NotEmpty(message = "Please enter checkNewInvestors")
    protected Boolean checkNewInvestors;
    protected String newInvestors;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public Boolean getCheckStakeHoldersChange() {
        return checkStakeHoldersChange;
    }

    public void setCheckStakeHoldersChange(Boolean checkStakeHoldersChange) {
        this.checkStakeHoldersChange = checkStakeHoldersChange;
    }

    public String getStakeHoldersChange() {
        return stakeHoldersChange;
    }

    public void setStakeHoldersChange(String stakeHoldersChange) {
        this.stakeHoldersChange = stakeHoldersChange;
    }

    public Boolean getCheckSharesAquisition() {
        return checkSharesAquisition;
    }

    public void setCheckSharesAquisition(Boolean checkSharesAquisition) {
        this.checkSharesAquisition = checkSharesAquisition;
    }

    public String getSharesAquisition() {
        return sharesAquisition;
    }

    public void setSharesAquisition(String sharesAquisition) {
        this.sharesAquisition = sharesAquisition;
    }

    public Boolean getCheckConvictedCrime() {
        return checkConvictedCrime;
    }

    public void setCheckConvictedCrime(Boolean checkConvictedCrime) {
        this.checkConvictedCrime = checkConvictedCrime;
    }

    public String getConvictedCrime() {
        return convictedCrime;
    }

    public void setConvictedCrime(String convictedCrime) {
        this.convictedCrime = convictedCrime;
    }

    public Boolean getCheckPoliticalOffice() {
        return checkPoliticalOffice;
    }

    public void setCheckPoliticalOffice(Boolean checkPoliticalOffice) {
        this.checkPoliticalOffice = checkPoliticalOffice;
    }

    public String getPoliticalOffice() {
        return politicalOffice;
    }

    public void setPoliticalOffice(String politicalOffice) {
        this.politicalOffice = politicalOffice;
    }

    public Boolean getCheckPoliticalParty() {
        return checkPoliticalParty;
    }

    public void setCheckPoliticalParty(Boolean checkPoliticalParty) {
        this.checkPoliticalParty = checkPoliticalParty;
    }

    public String getPoliticalParty() {
        return politicalParty;
    }

    public void setPoliticalParty(String politicalParty) {
        this.politicalParty = politicalParty;
    }

    public Boolean getCheckTechnicalPartner() {
        return checkTechnicalPartner;
    }

    public void setCheckTechnicalPartner(Boolean checkTechnicalPartner) {
        this.checkTechnicalPartner = checkTechnicalPartner;
    }

    public String getTechnicalPartner() {
        return technicalPartner;
    }

    public void setTechnicalPartner(String technicalPartner) {
        this.technicalPartner = technicalPartner;
    }

    public Boolean getCheckChangeInGamingMachines() {
        return checkChangeInGamingMachines;
    }

    public void setCheckChangeInGamingMachines(Boolean checkChangeInGamingMachines) {
        this.checkChangeInGamingMachines = checkChangeInGamingMachines;
    }

    public String getChangeInGamingMachines() {
        return changeInGamingMachines;
    }

    public void setChangeInGamingMachines(String changeInGamingMachines) {
        this.changeInGamingMachines = changeInGamingMachines;
    }

    public Boolean getCheckNewInvestors() {
        return checkNewInvestors;
    }

    public void setCheckNewInvestors(Boolean checkNewInvestors) {
        this.checkNewInvestors = checkNewInvestors;
    }

    public String getNewInvestors() {
        return newInvestors;
    }

    public void setNewInvestors(String newInvestors) {
        this.newInvestors = newInvestors;
    }
}
