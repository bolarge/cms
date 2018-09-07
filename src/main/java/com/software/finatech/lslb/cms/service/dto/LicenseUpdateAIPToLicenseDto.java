package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LicenseUpdateAIPToLicenseDto {
    @NotEmpty(message = "Provide institutionId")
    protected String institutionId;
    @NotEmpty(message = "Provide gameTypeId")
    protected String gameTypeId;
    @NotEmpty(message = "Provide startDate")
    protected String startDate;

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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
