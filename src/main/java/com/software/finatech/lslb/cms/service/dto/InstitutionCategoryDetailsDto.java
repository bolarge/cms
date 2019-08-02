package com.software.finatech.lslb.cms.service.dto;

import java.util.HashSet;
import java.util.Set;

public class InstitutionCategoryDetailsDto {
    private String firstCommencementDate;
    private String gameTypeName;
    private String tradeName;
    private Set<String> directorsNames = new HashSet<>();

    public Set<String> getDirectorsNames() {
        return directorsNames;
    }

    public void setDirectorsNames(Set<String> directorsNames) {
        this.directorsNames = directorsNames;
    }

    public String getFirstCommencementDate() {
        return firstCommencementDate;
    }

    public void setFirstCommencementDate(String firstCommencementDate) {
        this.firstCommencementDate = firstCommencementDate;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }
}
