package com.software.finatech.lslb.cms.service.dto;

public class AIPCheckDto {
    protected String licenseStatusId;
    protected String gameType;
    protected String licensedId;

    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getLicensedId() {
        return licensedId;
    }

    public void setLicensedId(String licensedId) {
        this.licensedId = licensedId;
    }
}
