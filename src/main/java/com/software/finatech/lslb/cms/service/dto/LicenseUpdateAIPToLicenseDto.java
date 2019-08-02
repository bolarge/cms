package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LicenseUpdateAIPToLicenseDto {
    @NotEmpty(message = "Provide institutionId")
    protected String institutionId;
    @NotEmpty(message = "Provide gameTypeId")
    protected String gameTypeId;

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

    }
