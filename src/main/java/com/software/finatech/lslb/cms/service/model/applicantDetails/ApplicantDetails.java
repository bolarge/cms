package com.software.finatech.lslb.cms.service.model.applicantDetails;

public class ApplicantDetails {

    private String applicantName;
    private String tradingName;
    private String registeredAddress;
    private String dateOfIncorporation;
    private String registrationNumber;
    private String shareCapital;
    private String emailAddress;
    private String phoneNumber;
    private  boolean applicantHasPreviousName;
    private ApplicantPreviousName applicantPreviousName;
    private boolean applicantHasPreviousApplication;
    private ApplicantPreviousApplication applicantPreviousApplication;
    private boolean applicantHasMemberPreviousApplication;
    private ApplicantMemberPreviousApplication applicantMemberPreviousApplication;
    private boolean applicantHasOutsideLagosStateLicense;
    private OutsideLagosStateLicense outsideLagosStateLicense;
    private String periodApplicantHasBeenInOperation;
    private boolean isApplicantMemberAffiliatedWithPoliticalParty;
    private String applicantMemberPoliticalPartyAffiliationDetails;
    private boolean isApplicantFundedByPoliticalParty;
    private String applicantPoliticalPartyFundingDetails;
    private boolean isApplicantMemberWithGamingExperience;
    private String applicantMemberGamingExperienceDetails;
    private boolean isApplicantIndigenous;
    private String applicantIndigenousDetails;


    public boolean isApplicantHasPreviousName() {
        return applicantHasPreviousName;
    }

    public void setApplicantHasPreviousName(boolean applicantHasPreviousName) {
        this.applicantHasPreviousName = applicantHasPreviousName;
    }

    public boolean isApplicantHasPreviousApplication() {
        return applicantHasPreviousApplication;
    }

    public void setApplicantHasPreviousApplication(boolean applicantHasPreviousApplication) {
        this.applicantHasPreviousApplication = applicantHasPreviousApplication;
    }

    public boolean isApplicantHasMemberPreviousApplication() {
        return applicantHasMemberPreviousApplication;
    }

    public void setApplicantHasMemberPreviousApplication(boolean applicantHasMemberPreviousApplication) {
        this.applicantHasMemberPreviousApplication = applicantHasMemberPreviousApplication;
    }

    public boolean isApplicantHasOutsideLagosStateLicense() {
        return applicantHasOutsideLagosStateLicense;
    }

    public void setApplicantHasOutsideLagosStateLicense(boolean applicantHasOutsideLagosStateLicense) {
        this.applicantHasOutsideLagosStateLicense = applicantHasOutsideLagosStateLicense;
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

    public ApplicantPreviousName getApplicantPreviousName() {
        return applicantPreviousName;
    }

    public void setApplicantPreviousName(ApplicantPreviousName applicantPreviousName) {
        this.applicantPreviousName = applicantPreviousName;
    }

    public ApplicantPreviousApplication getApplicantPreviousApplication() {
        return applicantPreviousApplication;
    }

    public void setApplicantPreviousApplication(ApplicantPreviousApplication applicantPreviousApplication) {
        this.applicantPreviousApplication = applicantPreviousApplication;
    }

    public ApplicantMemberPreviousApplication getApplicantMemberPreviousApplication() {
        return applicantMemberPreviousApplication;
    }

    public void setApplicantMemberPreviousApplication(ApplicantMemberPreviousApplication applicantMemberPreviousApplication) {
        this.applicantMemberPreviousApplication = applicantMemberPreviousApplication;
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

    public boolean isApplicantMemberAffiliatedWithPoliticalParty() {
        return isApplicantMemberAffiliatedWithPoliticalParty;
    }

    public void setApplicantMemberAffiliatedWithPoliticalParty(boolean applicantMemberAffiliatedWithPoliticalParty) {
        isApplicantMemberAffiliatedWithPoliticalParty = applicantMemberAffiliatedWithPoliticalParty;
    }

    public String getApplicantMemberPoliticalPartyAffiliationDetails() {
        return applicantMemberPoliticalPartyAffiliationDetails;
    }

    public void setApplicantMemberPoliticalPartyAffiliationDetails(String applicantMemberPoliticalPartyAffiliationDetails) {
        this.applicantMemberPoliticalPartyAffiliationDetails = applicantMemberPoliticalPartyAffiliationDetails;
    }

    public boolean isApplicantFundedByPoliticalParty() {
        return isApplicantFundedByPoliticalParty;
    }

    public void setApplicantFundedByPoliticalParty(boolean applicantFundedByPoliticalParty) {
        isApplicantFundedByPoliticalParty = applicantFundedByPoliticalParty;
    }

    public String getApplicantPoliticalPartyFundingDetails() {
        return applicantPoliticalPartyFundingDetails;
    }

    public void setApplicantPoliticalPartyFundingDetails(String applicantPoliticalPartyFundingDetails) {
        this.applicantPoliticalPartyFundingDetails = applicantPoliticalPartyFundingDetails;
    }

    public boolean isApplicantMemberWithGamingExperience() {
        return isApplicantMemberWithGamingExperience;
    }

    public void setApplicantMemberWithGamingExperience(boolean applicantMemberWithGamingExperience) {
        isApplicantMemberWithGamingExperience = applicantMemberWithGamingExperience;
    }

    public String getApplicantMemberGamingExperienceDetails() {
        return applicantMemberGamingExperienceDetails;
    }

    public void setApplicantMemberGamingExperienceDetails(String applicantMemberGamingExperienceDetails) {
        this.applicantMemberGamingExperienceDetails = applicantMemberGamingExperienceDetails;
    }

    public boolean isApplicantIndigenous() {
        return isApplicantIndigenous;
    }

    public void setApplicantIndigenous(boolean applicantIndigenous) {
        isApplicantIndigenous = applicantIndigenous;
    }

    public String getApplicantIndigenousDetails() {
        return applicantIndigenousDetails;
    }

    public void setApplicantIndigenousDetails(String applicantIndigenousDetails) {
        this.applicantIndigenousDetails = applicantIndigenousDetails;
    }
}
