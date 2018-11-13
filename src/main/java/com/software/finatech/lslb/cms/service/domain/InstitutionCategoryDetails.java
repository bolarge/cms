package com.software.finatech.lslb.cms.service.domain;

import org.joda.time.LocalDate;

public class InstitutionCategoryDetails {
    private String gameTypeId;
    private String gameTypeName;
    private String tradeName;
    private LocalDate firstCommencementDate;

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
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
