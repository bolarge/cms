package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class GameTypeCreateDto  {
    @NotEmpty(message = "Please enter AIP Duration")
    protected String aipDuration;
    @NotEmpty(message = "Please enter License Duration")
    protected String licenseDuration;
    @NotEmpty(message = "Please enter GameType Name")
    protected String name;
    @NotEmpty(message = "Please enter GameType ID")
    protected String id;

    protected String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
