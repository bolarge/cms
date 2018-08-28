package com.software.finatech.lslb.cms.service.dto;


public class DocumentTypeDto extends EnumeratedFactDto{

    protected String documentPurposeId;
    protected DocumentPurposeDto documentPurpose;
    protected boolean active;
    protected boolean required;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    public String getDocumentPurposeId() {
        return documentPurposeId;
    }

    public void setDocumentPurposeId(String documentPurposeId) {
        this.documentPurposeId = documentPurposeId;
    }


    public DocumentPurposeDto getDocumentPurpose() {
        return documentPurpose;
    }

    public void setDocumentPurpose(DocumentPurposeDto documentPurpose) {
        this.documentPurpose = documentPurpose;
    }
}
