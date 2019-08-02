package com.software.finatech.lslb.cms.service.dto;

/**
 * @author adeyi.adebolu
 * created on 19/06/2019
 */
public class MigrateCategoryDto {
    String institutionId;
    String oldGameTypeId;
    String newGameTypeId;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getOldGameTypeId() {
        return oldGameTypeId;
    }

    public void setOldGameTypeId(String oldGameTypeId) {
        this.oldGameTypeId = oldGameTypeId;
    }

    public String getNewGameTypeId() {
        return newGameTypeId;
    }

    public void setNewGameTypeId(String newGameTypeId) {
        this.newGameTypeId = newGameTypeId;
    }
}
