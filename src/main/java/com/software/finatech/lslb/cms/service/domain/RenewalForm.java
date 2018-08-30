package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.RenewalFormDto;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "RenewalForm")
public class RenewalForm extends AbstractFact {
    protected String paymentRecordId;
    protected Boolean checkStakeHoldersChange;
    protected String stakeHoldersChange;
    protected Boolean checkSharesAquisition;
    protected String sharesAquisition;
    protected Boolean checkConvictedCrime;
    protected String convictedCrime;
    protected Boolean checkPoliticalOffice;
    protected String politicalOffice;
    protected Boolean checkPoliticalParty;
    protected String politicalParty;
    protected Boolean checkTechnicalPartner;
    protected String technicalPartner;
    protected Boolean checkChangeInGamingMachines;
    protected String changeInGamingMachines;
    protected Boolean checkNewInvestors;
    protected String newInvestors;

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

    public RenewalFormDto convertToDto(){
        RenewalFormDto renewalFormDto = new RenewalFormDto();
        renewalFormDto.setCheckChangeInGamingMachines(getCheckChangeInGamingMachines());
        renewalFormDto.setCheckConvictedCrime(getCheckConvictedCrime());
        renewalFormDto.setCheckNewInvestors(getCheckNewInvestors());
        renewalFormDto.setCheckPoliticalOffice(getCheckPoliticalOffice());
        renewalFormDto.setCheckPoliticalParty(getCheckPoliticalParty());
        renewalFormDto.setCheckSharesAquisition(getCheckSharesAquisition());
        renewalFormDto.setCheckStakeHoldersChange(getCheckStakeHoldersChange());
        renewalFormDto.setCheckTechnicalPartner(getCheckTechnicalPartner());

        renewalFormDto.setChangeInGamingMachines(getChangeInGamingMachines());
        renewalFormDto.setNewInvestors(getNewInvestors());
        renewalFormDto.setPoliticalParty(getPoliticalParty());
        renewalFormDto.setPoliticalOffice(getPoliticalOffice());
        renewalFormDto.setConvictedCrime(getConvictedCrime());
        renewalFormDto.setSharesAquisition(getSharesAquisition());
        renewalFormDto.setStakeHoldersChange(getStakeHoldersChange());
        renewalFormDto.setTechnicalPartner(getTechnicalPartner());
        PaymentRecord paymentRecord= (PaymentRecord) mongoRepositoryReactive.findById(getPaymentRecordId(),PaymentRecord.class).block();
        renewalFormDto.setPaymentRecord(paymentRecord.convertToDto());

        return renewalFormDto;

    }

    @Override
    public String getFactName() {
        return "RenewalForm";
    }
}
