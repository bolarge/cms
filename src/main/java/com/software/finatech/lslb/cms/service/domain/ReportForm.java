package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.RenewalFormDto;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "RenewalForm")
public class ReportForm extends AbstractFact {
    protected String institutionId;
    protected String gameTypeId;
    protected String comment;
    protected String repoterId;
    protected LocalDate reportedDate;


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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRepoterId() {
        return repoterId;
    }

    public void setRepoterId(String repoterId) {
        this.repoterId = repoterId;
    }

    public LocalDate getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(LocalDate reportedDate) {
        this.reportedDate = reportedDate;
    }

   /* public RenewalFormDto convertToDto(){
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
        renewalFormDto.setPaymentRecord(paymentRecord.convertToDto());

        return renewalFormDto;

    }*/


    @Override
    public String getFactName() {
        return "RenewalForm";
    }
}
