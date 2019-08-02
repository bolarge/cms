package com.software.finatech.lslb.cms.service.model.applicantDetails;

public class OutsideLagosStateLicense {
    private String nameOfLicense;
    private String nameOfIssuingBody;
    private String typeOfLicense;
    private String licenseNumber;
    private String stateAndCountry;
    private String dateOfIssue;
    private String dateOfExpiry;
    private String reasonLicenseIsNoLongerHeld;

    public String getNameOfLicense() {
        return nameOfLicense;
    }

    public void setNameOfLicense(String nameOfLicense) {
        this.nameOfLicense = nameOfLicense;
    }

    public String getNameOfIssuingBody() {
        return nameOfIssuingBody;
    }

    public void setNameOfIssuingBody(String nameOfIssuingBody) {
        this.nameOfIssuingBody = nameOfIssuingBody;
    }

    public String getTypeOfLicense() {
        return typeOfLicense;
    }

    public void setTypeOfLicense(String typeOfLicense) {
        this.typeOfLicense = typeOfLicense;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getStateAndCountry() {
        return stateAndCountry;
    }

    public void setStateAndCountry(String stateAndCountry) {
        this.stateAndCountry = stateAndCountry;
    }

    public String getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public String getDateOfExpiry() {
        return dateOfExpiry;
    }

    public void setDateOfExpiry(String dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    public String getReasonLicenseIsNoLongerHeld() {
        return reasonLicenseIsNoLongerHeld;
    }

    public void setReasonLicenseIsNoLongerHeld(String reasonLicenseIsNoLongerHeld) {
        this.reasonLicenseIsNoLongerHeld = reasonLicenseIsNoLongerHeld;
    }
}
