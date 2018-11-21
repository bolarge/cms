package com.software.finatech.lslb.cms.service.dto;

public class AIPCheckDto {
    protected String licenseStatusId;
    protected GameTypeDto gameType;
    protected String licensedId;
    protected String aipFormId;
    protected String institutionId;
    protected String institutionName;

    public String getAipFormId() {
        return aipFormId;
    }

    public void setAipFormId(String aipFormId) {
        this.aipFormId = aipFormId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }

    public GameTypeDto getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeDto gameType) {
        this.gameType = gameType;
    }

    public String getLicensedId() {
        return licensedId;
    }

    public void setLicensedId(String licensedId) {
        this.licensedId = licensedId;
    }
}
