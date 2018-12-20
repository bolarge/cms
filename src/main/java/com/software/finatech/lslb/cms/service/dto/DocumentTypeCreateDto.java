package com.software.finatech.lslb.cms.service.dto;


import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class DocumentTypeCreateDto extends EnumeratedFactDto {
    @NotEmpty(message = "Enter a valid document purpose ID")
    protected String documentPurposeId;
    protected boolean active;
    protected boolean required;
    private Set<String> gameTypeIds =new HashSet<>();
    private String approverId;

    public Set<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(Set<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }

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
