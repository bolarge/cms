package com.software.finatech.lslb.cms.service.model.applicantDetails;

import com.software.finatech.lslb.cms.service.dto.CommentDetail;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class ApplicantDetails {

    private String applicantName;
    @NotEmpty
    private String tradingName;
    private String registeredAddress;
    private String dateOfIncorporation;
    private String registrationNumber;
    private String shareCapital;
    private String emailAddress;
    private String phoneNumber;
    private Boolean applicantHasPreviousName;
    private ApplicantPreviousName applicantPreviousName;
    private Boolean applicantHasPreviousApplication;
    private ApplicantPreviousApplication applicantPreviousApplication;
    private Boolean applicantHasMemberPreviousApplication;
    private ApplicantMemberPreviousApplication applicantMemberPreviousApplication;
    private Boolean applicantHasOutsideLagosStateLicense;
    private OutsideLagosStateLicense outsideLagosStateLicense;
    private String periodApplicantHasBeenInOperation;
    private Boolean applicantMemberAffiliatedWithPoliticalParty;
    private String applicantMemberPoliticalPartyAffiliationDetails;
    private Boolean applicantFundedByPoliticalParty;
    private String applicantPoliticalPartyFundingDetails;
    private Boolean applicantMemberWithGamingExperience;
    private String applicantMemberGamingExperienceDetails;
    private Boolean applicantIndigenous;
    private String applicantIndigenousDetails;
    private List<CommentDetail> comments = new ArrayList<>();

    public List<CommentDetail> getComments() {
        return comments;
    }

    public void setComments(List<CommentDetail> comments) {
        this.comments = comments;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getTradingName() {
        return tradingName;
    }

    public void setTradingName(String tradingName) {
        this.tradingName = tradingName;
    }

    public String getRegisteredAddress() {
        return registeredAddress;
    }

    public void setRegisteredAddress(String registeredAddress) {
        this.registeredAddress = registeredAddress;
    }

    public String getDateOfIncorporation() {
        return dateOfIncorporation;
    }

    public void setDateOfIncorporation(String dateOfIncorporation) {
        this.dateOfIncorporation = dateOfIncorporation;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getShareCapital() {
        return shareCapital;
    }

    public void setShareCapital(String shareCapital) {
        this.shareCapital = shareCapital;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getApplicantHasPreviousName() {
        return applicantHasPreviousName;
    }

    public void setApplicantHasPreviousName(Boolean applicantHasPreviousName) {
        this.applicantHasPreviousName = applicantHasPreviousName;
    }

    public ApplicantPreviousName getApplicantPreviousName() {
        return applicantPreviousName;
    }

    public void setApplicantPreviousName(ApplicantPreviousName applicantPreviousName) {
        this.applicantPreviousName = applicantPreviousName;
    }

    public Boolean getApplicantHasPreviousApplication() {
        return applicantHasPreviousApplication;
    }

    public void setApplicantHasPreviousApplication(Boolean applicantHasPreviousApplication) {
        this.applicantHasPreviousApplication = applicantHasPreviousApplication;
    }

    public ApplicantPreviousApplication getApplicantPreviousApplication() {
        return applicantPreviousApplication;
    }

    public void setApplicantPreviousApplication(ApplicantPreviousApplication applicantPreviousApplication) {
        this.applicantPreviousApplication = applicantPreviousApplication;
    }

    public Boolean getApplicantHasMemberPreviousApplication() {
        return applicantHasMemberPreviousApplication;
    }

    public void setApplicantHasMemberPreviousApplication(Boolean applicantHasMemberPreviousApplication) {
        this.applicantHasMemberPreviousApplication = applicantHasMemberPreviousApplication;
    }

    public ApplicantMemberPreviousApplication getApplicantMemberPreviousApplication() {
        return applicantMemberPreviousApplication;
    }

    public void setApplicantMemberPreviousApplication(ApplicantMemberPreviousApplication applicantMemberPreviousApplication) {
        this.applicantMemberPreviousApplication = applicantMemberPreviousApplication;
    }

    public Boolean getApplicantHasOutsideLagosStateLicense() {
        return applicantHasOutsideLagosStateLicense;
    }

    public void setApplicantHasOutsideLagosStateLicense(Boolean applicantHasOutsideLagosStateLicense) {
        this.applicantHasOutsideLagosStateLicense = applicantHasOutsideLagosStateLicense;
    }

    public OutsideLagosStateLicense getOutsideLagosStateLicense() {
        return outsideLagosStateLicense;
    }

    public void setOutsideLagosStateLicense(OutsideLagosStateLicense outsideLagosStateLicense) {
        this.outsideLagosStateLicense = outsideLagosStateLicense;
    }

    public String getPeriodApplicantHasBeenInOperation() {
        return periodApplicantHasBeenInOperation;
    }

    public void setPeriodApplicantHasBeenInOperation(String periodApplicantHasBeenInOperation) {
        this.periodApplicantHasBeenInOperation = periodApplicantHasBeenInOperation;
    }

    public Boolean getApplicantMemberAffiliatedWithPoliticalParty() {
        return applicantMemberAffiliatedWithPoliticalParty;
    }

    public void setApplicantMemberAffiliatedWithPoliticalParty(Boolean applicantMemberAffiliatedWithPoliticalParty) {
        this.applicantMemberAffiliatedWithPoliticalParty = applicantMemberAffiliatedWithPoliticalParty;
    }

    public String getApplicantMemberPoliticalPartyAffiliationDetails() {
        return applicantMemberPoliticalPartyAffiliationDetails;
    }

    public void setApplicantMemberPoliticalPartyAffiliationDetails(String applicantMemberPoliticalPartyAffiliationDetails) {
        this.applicantMemberPoliticalPartyAffiliationDetails = applicantMemberPoliticalPartyAffiliationDetails;
    }

    public Boolean getApplicantFundedByPoliticalParty() {
        return applicantFundedByPoliticalParty;
    }

    public void setApplicantFundedByPoliticalParty(Boolean applicantFundedByPoliticalParty) {
        this.applicantFundedByPoliticalParty = applicantFundedByPoliticalParty;
    }

    public String getApplicantPoliticalPartyFundingDetails() {
        return applicantPoliticalPartyFundingDetails;
    }

    public void setApplicantPoliticalPartyFundingDetails(String applicantPoliticalPartyFundingDetails) {
        this.applicantPoliticalPartyFundingDetails = applicantPoliticalPartyFundingDetails;
    }

    public Boolean getApplicantMemberWithGamingExperience() {
        return applicantMemberWithGamingExperience;
    }

    public void setApplicantMemberWithGamingExperience(Boolean applicantMemberWithGamingExperience) {
        this.applicantMemberWithGamingExperience = applicantMemberWithGamingExperience;
    }

    public String getApplicantMemberGamingExperienceDetails() {
        return applicantMemberGamingExperienceDetails;
    }

    public void setApplicantMemberGamingExperienceDetails(String applicantMemberGamingExperienceDetails) {
        this.applicantMemberGamingExperienceDetails = applicantMemberGamingExperienceDetails;
    }

    public Boolean getApplicantIndigenous() {
        return applicantIndigenous;
    }

    public void setApplicantIndigenous(Boolean applicantIndigenous) {
        this.applicantIndigenous = applicantIndigenous;
    }

    public String getApplicantIndigenousDetails() {
        return applicantIndigenousDetails;
    }

    public void setApplicantIndigenousDetails(String applicantIndigenousDetails) {
        this.applicantIndigenousDetails = applicantIndigenousDetails;
    }
}
