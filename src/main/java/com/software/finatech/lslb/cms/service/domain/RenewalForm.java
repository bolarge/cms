package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.RenewalFormDto;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "RenewalForms")
public class RenewalForm extends AbstractFact {
    protected String paymentRecordId;
    protected String institutionId;
    protected String gameTypeId;
    protected String checkStakeHoldersChange;
    protected String stakeHoldersChange;
    protected String checkSharesAquisition;
    protected String sharesAquisition;
    protected String checkConvictedCrime;
    protected String convictedCrime;
    protected String checkPoliticalOffice;
    protected String politicalOffice;
    protected String checkPoliticalParty;
    protected String politicalParty;
    protected String checkTechnicalPartner;
    protected String technicalPartner;
    protected String checkChangeInGamingMachines;
    protected String changeInGamingMachines;
    protected String checkNewInvestors;
    protected String newInvestors;


    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getCheckStakeHoldersChange() {
        return checkStakeHoldersChange;
    }

    public void setCheckStakeHoldersChange(String checkStakeHoldersChange) {
        this.checkStakeHoldersChange = checkStakeHoldersChange;
    }

    public String getStakeHoldersChange() {
        return stakeHoldersChange;
    }

    public void setStakeHoldersChange(String stakeHoldersChange) {
        this.stakeHoldersChange = stakeHoldersChange;
    }

    public String getCheckSharesAquisition() {
        return checkSharesAquisition;
    }

    public void setCheckSharesAquisition(String checkSharesAquisition) {
        this.checkSharesAquisition = checkSharesAquisition;
    }

    public String getSharesAquisition() {
        return sharesAquisition;
    }

    public void setSharesAquisition(String sharesAquisition) {
        this.sharesAquisition = sharesAquisition;
    }

    public String getCheckConvictedCrime() {
        return checkConvictedCrime;
    }

    public void setCheckConvictedCrime(String checkConvictedCrime) {
        this.checkConvictedCrime = checkConvictedCrime;
    }

    public String getConvictedCrime() {
        return convictedCrime;
    }

    public void setConvictedCrime(String convictedCrime) {
        this.convictedCrime = convictedCrime;
    }

    public String getCheckPoliticalOffice() {
        return checkPoliticalOffice;
    }

    public void setCheckPoliticalOffice(String checkPoliticalOffice) {
        this.checkPoliticalOffice = checkPoliticalOffice;
    }

    public String getPoliticalOffice() {
        return politicalOffice;
    }

    public void setPoliticalOffice(String politicalOffice) {
        this.politicalOffice = politicalOffice;
    }

    public String getCheckPoliticalParty() {
        return checkPoliticalParty;
    }

    public void setCheckPoliticalParty(String checkPoliticalParty) {
        this.checkPoliticalParty = checkPoliticalParty;
    }

    public String getPoliticalParty() {
        return politicalParty;
    }

    public void setPoliticalParty(String politicalParty) {
        this.politicalParty = politicalParty;
    }

    public String getCheckTechnicalPartner() {
        return checkTechnicalPartner;
    }

    public void setCheckTechnicalPartner(String checkTechnicalPartner) {
        this.checkTechnicalPartner = checkTechnicalPartner;
    }

    public String getTechnicalPartner() {
        return technicalPartner;
    }

    public void setTechnicalPartner(String technicalPartner) {
        this.technicalPartner = technicalPartner;
    }

    public String getCheckChangeInGamingMachines() {
        return checkChangeInGamingMachines;
    }

    public void setCheckChangeInGamingMachines(String checkChangeInGamingMachines) {
        this.checkChangeInGamingMachines = checkChangeInGamingMachines;
    }

    public String getChangeInGamingMachines() {
        return changeInGamingMachines;
    }

    public void setChangeInGamingMachines(String changeInGamingMachines) {
        this.changeInGamingMachines = changeInGamingMachines;
    }

    public String getCheckNewInvestors() {
        return checkNewInvestors;
    }

    public void setCheckNewInvestors(String checkNewInvestors) {
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
        renewalFormDto.setRenewalFormId(getId());
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
        renewalFormDto.setPaymentRecord(paymentRecord.convertToDto()== null ? null :paymentRecord.convertToDto() );


        return renewalFormDto;

    }

    @Override
    public String getFactName() {
        return "RenewalForm";
    }
}
