package com.software.finatech.lslb.cms.service.dto;


import javax.validation.constraints.NotEmpty;

public class DocumentTypeUpdateDto extends EnumeratedFactDto{
    @NotEmpty(message = "Enter a valid document purpose ID")
    protected String documentPurposeId;
    @NotEmpty(message = "Enter a valid status")
    protected boolean active;
    @NotEmpty(message = "Enter a required status")
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

}
