package com.software.finatech.lslb.cms.service.model.migrations;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adeyi.adebolu
 * created on 20/06/2019
 */
public class MigratedInstitutionUpload extends BaseInstitutionUpload {
    private String institutionName;
    private String emailAddress;
    private List<String> phoneNumbers = new ArrayList<>();
    private String address;
    private String description;
    private String gameTypeId;
    private String tradeName;
    private String firstCommencementDate;
    private String licenseStartDate;
    private String licenseEndDate;
    private List<String> directors = new ArrayList<>();
    private String licenseDate;
    private boolean useInstitutionId;
    private String institutionId;
    private String licenseStatusId;

    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getFirstCommencementDate() {
        return firstCommencementDate;
    }

    public void setFirstCommencementDate(String firstCommencementDate) {
        this.firstCommencementDate = firstCommencementDate;
    }

    public String getLicenseStartDate() {
        return licenseStartDate;
    }

    public void setLicenseStartDate(String licenseStartDate) {
        this.licenseStartDate = licenseStartDate;
    }

    public String getLicenseEndDate() {
        return licenseEndDate;
    }

    public void setLicenseEndDate(String licenseEndDate) {
        this.licenseEndDate = licenseEndDate;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }

    public String getLicenseDate() {
        return licenseDate;
    }

    public void setLicenseDate(String licenseDate) {
        this.licenseDate = licenseDate;
    }

    public boolean isUseInstitutionId() {
        return useInstitutionId;
    }

    public void setUseInstitutionId(boolean useInstitutionId) {
        this.useInstitutionId = useInstitutionId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }
}
