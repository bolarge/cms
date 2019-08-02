package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.CommentDetail;
import com.software.finatech.lslb.cms.service.dto.RenewalFormDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "RenewalForms")
public class RenewalForm extends AbstractFact {
    protected String paymentRecordId;
    protected String institutionId;
    protected String gameTypeId;
    protected String licensedId;
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
    protected String formStatusId;
    protected List<FormComment> formComments = new ArrayList<>();
    protected String approverId;
    protected String rejectorId;
    protected String reasonForRejection;
    protected LocalDate submissionDate;
    protected Boolean readyForApproval;

    public Boolean getReadyForApproval() {
        return readyForApproval;
    }

    public void setReadyForApproval(Boolean readyForApproval) {
        this.readyForApproval = readyForApproval;
    }

    public List<FormComment> getFormComments() {
        return formComments;
    }

    public void setFormComments(List<FormComment> formComments) {
        this.formComments = formComments;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getReasonForRejection() {
        return reasonForRejection;
    }

    public void setReasonForRejection(String reasonForRejection) {
        this.reasonForRejection = reasonForRejection;
    }

    public String getRejectorId() {
        return rejectorId;
    }

    public void setRejectorId(String rejectorId) {
        this.rejectorId = rejectorId;
    }


    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public Institution getInstitution() {
        if (StringUtils.isEmpty(institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public String getInstitutionName() {
        Institution institution = getInstitution();
        if (institution != null) {
            return institution.getInstitutionName();
        }
        return "";
    }

    public String getLicensedId() {
        return licensedId;
    }

    public void setLicensedId(String licensedId) {
        this.licensedId = licensedId;
    }

    public String getFormStatusId() {
        return formStatusId;
    }

    public void setFormStatusId(String formStatusId) {
        this.formStatusId = formStatusId;
    }

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

    public AuthInfo getAuthInfo(String authInfoId) {
        if (StringUtils.isEmpty(authInfoId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(authInfoId, AuthInfo.class).block();
    }

    public GameType getGameType() {
        if (StringUtils.isEmpty(this.gameTypeId)) {
            return null;
        }
        return (GameType) mongoRepositoryReactive.findById(this.gameTypeId, GameType.class).block();
    }

    public String getGameTypeName() {
        GameType gameType = getGameType();
        if (gameType == null) {
            return null;
        } else {
            return gameType.getName();
        }
    }

    public RenewalFormDto convertToDto() {
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
        renewalFormDto.setLicenseId(getLicensedId());
        renewalFormDto.setRejectionReason(getReasonForRejection());
        List<CommentDetail> comments = getComments();
        Collections.reverse(comments);
        renewalFormDto.setComments(comments);
        Query query = new Query();
        query.addCriteria(Criteria.where("paymentRecordId").is(paymentRecordId));
        License license = (License) mongoRepositoryReactive.find(query, License.class).block();
        if (license != null) {
            renewalFormDto.setLicenseStatus(license.getLicenseStatus().convertToDto());

        }
        AuthInfo approver = getAuthInfo(approverId);
        if (approver != null) {
            renewalFormDto.setApproverId(approverId);
            renewalFormDto.setApproverName(approver.getFullName());
        }
        AuthInfo rejector = getAuthInfo(rejectorId);
        if (rejector != null) {
            renewalFormDto.setRejectorId(rejectorId);
            renewalFormDto.setRejectorName(rejector.getFullName());
        }
        PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.findById(getPaymentRecordId(), PaymentRecord.class).block();
        if (paymentRecord != null) {
            renewalFormDto.setPaymentRecord(paymentRecord.convertToDto());
        }
        if (!StringUtils.isEmpty(formStatusId)) {
            Map<String, FactObject> renewalFormStatusMap = Mapstore.STORE.get("RenewalFormStatus");

            RenewalFormStatus renewalFormStatus = null;
            if (renewalFormStatusMap != null) {

                renewalFormStatus = (RenewalFormStatus) renewalFormStatusMap.get(formStatusId);
            }
            if (renewalFormStatus == null) {
                renewalFormStatus = (RenewalFormStatus) mongoRepositoryReactive.findById(formStatusId, RenewalFormStatus.class).block();
                if (renewalFormStatus != null && renewalFormStatusMap != null) {
                    renewalFormStatusMap.put(formStatusId, renewalFormStatus);
                }
            }
            if (renewalFormStatus != null) {
                renewalFormDto.setRenewalFormStatus(renewalFormStatus.convertToDto());
            }

        }

        return renewalFormDto;

    }

    private List<CommentDetail> getComments() {
        List<CommentDetail> comments = new ArrayList<>();
        for (FormComment comment : this.formComments) {
            CommentDetail dto = new CommentDetail();
            dto.setComment(comment.getComment());
            dto.setUserFullName(comment.getUserFullName());
            dto.setCommentDate(comment.getTimeCreated().toString("dd-MM-yyyy"));
            dto.setCommentTime(comment.getTimeCreated().toString("HH:mm:ss a"));
            comments.add(dto);
        }
        return comments;
    }


    public License getLicense() {
        return (License) mongoRepositoryReactive.find(Query.query(Criteria.where("renewalFormId").is(this.id)), License.class).block();
    }

    @Override
    public String getFactName() {
        return "RenewalForm";
    }
}
