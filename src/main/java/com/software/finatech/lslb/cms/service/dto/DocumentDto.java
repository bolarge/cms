package com.software.finatech.lslb.cms.service.dto;


public class DocumentDto {
    protected String id;
    protected String description;
    protected String filename;
    protected String mimeType;

    protected String entryDate;
    protected String validFrom;
    protected String validTo;
    protected Boolean isCurrent;
    protected String documentTypeId;
    protected DocumentTypeDto documentType;
    protected String entity;
    protected String entityId;
    protected String previousDocument;
    protected String originalFilename;

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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public DocumentTypeDto getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentTypeDto documentType) {
        this.documentType = documentType;
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

    public String getPreviousDocument() {
        return previousDocument;
    }

    public void setPreviousDocument(String previousDocument) {
        this.previousDocument = previousDocument;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
}
