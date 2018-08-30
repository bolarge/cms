package com.software.finatech.lslb.cms.service.dto;

public class GameTypeDto extends EnumeratedFactDto {

    protected String aipDuration;
    protected String licenseDuration;

    public String getAipDuration() {
        return aipDuration;
    }

    public void setAipDuration(String aipDuration) {
        this.aipDuration = aipDuration;
    }

    public String getLicenseDuration() {
        return licenseDuration;
    }

    public void setLicenseDuration(String licenseDuration) {
        this.licenseDuration = licenseDuration;
    }
}
