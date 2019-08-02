package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adeyi.adebolu
 * created on 19/06/2019
 */
public class DirectorsUpdateDto {
    private List<String> names = new ArrayList<>();
    private String institutionId;
    private String gameTypeId;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

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
