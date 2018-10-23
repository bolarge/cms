package com.software.finatech.lslb.cms.service.dto;

public class DocumentApprovalRequestDto extends AbstractApprovalRequestDto {

    private DocumentTypeDto pendingDocumentType;
    private String newApproverName;
    private String newApproverId;
    private String documentTypeName;

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public DocumentTypeDto getPendingDocumentType() {
        return pendingDocumentType;
    }

    public void setPendingDocumentType(DocumentTypeDto pendingDocumentType) {
        this.pendingDocumentType = pendingDocumentType;
    }

    public String getNewApproverName() {
        return newApproverName;
    }

    public void setNewApproverName(String newApproverName) {
        this.newApproverName = newApproverName;
    }

    public String getNewApproverId() {
        return newApproverId;
    }

    public void setNewApproverId(String newApproverId) {
        this.newApproverId = newApproverId;
    }
}
