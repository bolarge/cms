package com.software.finatech.lslb.cms.service.dto;


import javax.validation.constraints.NotEmpty;

public class DocumentTypeCreateDto extends EnumeratedFactDto{
    @NotEmpty(message = "Enter a valid document purpose ID")
    protected String documentPurposeId;
    protected boolean active;
    protected boolean required;
    private String approverId;

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
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

}
