package com.software.finatech.lslb.cms.service.dto;


import java.util.Set;

public class DocumentTypeDto extends EnumeratedFactDto{

    protected String documentPurposeId;
    protected DocumentPurposeDto documentPurpose;
    protected boolean active;
    protected boolean required;
    protected Set<String> gameTypeNames;

    public Set<String> getGameTypeNames() {
        return gameTypeNames;
    }

    public void setGameTypeNames(Set<String> gameTypeNames) {
        this.gameTypeNames = gameTypeNames;
    }

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
