package com.software.finatech.lslb.cms.service.dto;


import javax.validation.constraints.NotEmpty;

public class DocumentCreateDto {
    protected String description;
    @NotEmpty(message = "Role ID field can not be empty")
    protected String filename;
    //@NotEmpty(message = "ValidFrom field can not be empty")
    protected String validFrom;
    //@NotEmpty(message = "ValidTo field can not be empty")
    protected String validTo;
    @NotEmpty(message = "DocumentType ID field can not be empty")
    protected String documentTypeId;
    @NotEmpty(message = "Entity field can not be empty")
    protected String entity;
    @NotEmpty(message = "Entity field ID can not be empty")
    protected String entityId;
    protected String previousDocumentId;
     protected String institutionId;
     protected String agentId;
     @NotEmpty(message = "GameType ID can not be empty")
    protected String gameTypeId;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getPreviousDocumentId() {
        return previousDocumentId;
    }

    public void setPreviousDocumentId(String previousDocumentId) {
        this.previousDocumentId = previousDocumentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
