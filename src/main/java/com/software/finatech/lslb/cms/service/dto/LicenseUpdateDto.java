package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LicenseUpdateDto {

    @NotEmpty(message = "Please provide License Status Id")
    private String licenseStatusId;
    @NotEmpty(message = "Please provide institutionId")
    private String institutionId;
    private String startDate;
    private String gameTypeId;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }


    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }





}
