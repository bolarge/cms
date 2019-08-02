package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class ApplicationFormCreateDto{

    @NotEmpty(message = "please supply game type Id")
    protected String gameTypeId;
    @NotEmpty(message = "please provide institution Id")
    protected String institutionId;
    protected String formName;

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

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }
}
