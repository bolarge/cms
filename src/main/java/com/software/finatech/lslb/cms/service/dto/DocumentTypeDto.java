package com.software.finatech.lslb.cms.service.dto;


import java.util.HashSet;
import java.util.Set;

public class DocumentTypeDto extends EnumeratedFactDto {

    protected String documentPurposeId;
    protected DocumentPurposeDto documentPurpose;
    protected boolean active;
    protected boolean required;
    protected Set<GameTypeDto> gameTypeDtos = new HashSet<>();
    private String approverName;
    private String approverId;

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public Set<GameTypeDto> getGameTypeDtos() {
        return gameTypeDtos;
    }

    public void setGameTypeDtos(Set<GameTypeDto> gameTypeDtos) {
        this.gameTypeDtos = gameTypeDtos;
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
