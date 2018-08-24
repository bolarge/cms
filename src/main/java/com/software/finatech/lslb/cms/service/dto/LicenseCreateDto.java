package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LicenseCreateDto {

    private String licenseStatusId;
    private Boolean renewalCheck;
    @NotEmpty(message = "Please provide institutionId")
    private String institutionId;
    @NotEmpty(message = "Please provide startDate")
    private String startDate;
     @NotEmpty(message = "Please provide institutionId")
    private String gameTypeId;

    public String getGameTypeId() {
        return gameTypeId;
    }

    public Boolean getRenewalCheck() {
        return renewalCheck;
    }

    public void setRenewalCheck(Boolean renewalCheck) {
        this.renewalCheck = renewalCheck;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }


    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
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


}
