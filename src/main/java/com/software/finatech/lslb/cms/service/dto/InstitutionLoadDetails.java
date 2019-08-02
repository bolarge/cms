package com.software.finatech.lslb.cms.service.dto;

import org.joda.time.LocalDate;

public class InstitutionLoadDetails {
    private String gameTypeId;
    private String tradeName;
    private LocalDate firstCommencementDate;
    private LocalDate licenseStartDate;
    private LocalDate licenseEndDate;
    private String director;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }
    public LocalDate getLicenseStartDate() {
        return licenseStartDate;
    }

    public void setLicenseStartDate(LocalDate licenseStartDate) {
        this.licenseStartDate = licenseStartDate;
    }

    public LocalDate getLicenseEndDate() {
        return licenseEndDate;
    }

    public void setLicenseEndDate(LocalDate licenseEndDate) {
        this.licenseEndDate = licenseEndDate;
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

    public LocalDate getFirstCommencementDate() {
        return firstCommencementDate;
    }

    public void setFirstCommencementDate(LocalDate firstCommencementDate) {
        this.firstCommencementDate = firstCommencementDate;
    }
}
