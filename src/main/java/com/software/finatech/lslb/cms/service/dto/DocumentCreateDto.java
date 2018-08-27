package com.software.finatech.lslb.cms.service.dto;


import javax.validation.constraints.NotEmpty;

public class DocumentCreateDto {
    protected String description;
    @NotEmpty(message = "Role ID field can not be empty")
    protected String filename;
    protected String entryDate;
    @NotEmpty(message = "ValidFrom field can not be empty")
    protected String validFrom;
    @NotEmpty(message = "ValidTo field can not be empty")
    protected String validTo;
    @NotEmpty(message = "DocumentType ID field can not be empty")
    protected String documentTypeId;
    @NotEmpty(message = "Entity field can not be empty")
    protected String entity;
    protected String applicationFormId;


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

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
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

    public String getApplicationFormId() {
        return applicationFormId;
    }

    public void setApplicationFormId(String applicationFormId) {
        this.applicationFormId = applicationFormId;
    }

}
