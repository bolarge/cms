package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author adeyi.adebolu
 * created on 19/06/2019
 */

@Document(collection = "OperatorCategoryChangeHistory")
public class OperatorCategoryChangeHistory extends AbstractFact {

    private String institutionId;
    private String oldGameTypeId;
    private String newGameTypeId;
    private String auditorName;
private String affectedEntities;

    public String getAffectedEntities() {
        return affectedEntities;
    }

    public void setAffectedEntities(String affectedEntities) {
        this.affectedEntities = affectedEntities;
    }

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

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    @Override
    public String getFactName() {
        return "--";
    }

}
