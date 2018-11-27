package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.DocumentApprovalRequestDto;
import com.software.finatech.lslb.cms.service.dto.DocumentTypeDto;
import com.software.finatech.lslb.cms.service.referencedata.DocumentApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "DocumentApprovalRequest")
public class DocumentApprovalRequest extends AbstractApprovalRequest {
    private String documentApprovalRequestTypeId;
    private String documentTypeId;
    private String pendingDocumentTypeId;
    private String newApproverId;

    public String getDocumentApprovalRequestTypeId() {
        return documentApprovalRequestTypeId;
    }

    public void setDocumentApprovalRequestTypeId(String documentApprovalRequestTypeId) {
        this.documentApprovalRequestTypeId = documentApprovalRequestTypeId;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getPendingDocumentTypeId() {
        return pendingDocumentTypeId;
    }

    public void setPendingDocumentTypeId(String pendingDocumentTypeId) {
        this.pendingDocumentTypeId = pendingDocumentTypeId;
    }

    public String getNewApproverId() {
        return newApproverId;
    }

    public void setNewApproverId(String newApproverId) {
        this.newApproverId = newApproverId;
    }

    public DocumentApprovalRequestType getDocumentApprovalRequestType() {
        if (StringUtils.isEmpty(this.documentApprovalRequestTypeId)) {
            return null;
        }
        Map documentApprovalRequestTypeMap = Mapstore.STORE.get("DocumentApprovalRequestType");
        DocumentApprovalRequestType documentApprovalRequestType = null;
        if (documentApprovalRequestTypeMap != null) {
            documentApprovalRequestType = (DocumentApprovalRequestType) documentApprovalRequestTypeMap.get(this.documentApprovalRequestTypeId);
        }
        if (documentApprovalRequestType == null) {
            documentApprovalRequestType = (DocumentApprovalRequestType) mongoRepositoryReactive.findById(this.documentApprovalRequestTypeId, AgentApprovalRequestType.class).block();
            if (documentApprovalRequestType != null && documentApprovalRequestType != null) {
                documentApprovalRequestTypeMap.put(this.documentApprovalRequestTypeId, documentApprovalRequestType);
            }
        }
        return documentApprovalRequestType;
    }

    @Override
    public String getFactName() {
        return "DocumentApprovalRequest";
    }

    public DocumentApprovalRequestDto convertToHalfDto() {
        DocumentApprovalRequestDto dto = new DocumentApprovalRequestDto();
        dto.setId(getId());
        dto.setDateCreated(getDateCreatedString());
        dto.setRequestTypeId(this.documentApprovalRequestTypeId);
        DocumentApprovalRequestType approvalRequestType = getDocumentApprovalRequestType();
        if (approvalRequestType != null) {
            dto.setRequestTypeName(String.valueOf(approvalRequestType));
        }
        dto.setRequestStatusId(this.approvalRequestStatusId);
        ApprovalRequestStatus status = getApprovalRequestStatus();
        if (status != null) {
            dto.setRequestStatusName(String.valueOf(status));
        }
        DocumentType pendingDoc = getSubjectDocumentType();
        if (pendingDoc != null) {
            dto.setDocumentTypeName(pendingDoc.toString());
        }
        AuthInfo initiator = getInitiator();
        if (initiator != null) {
            dto.setInitiatorId(this.initiatorId);
            dto.setInitiatorName(initiator.getFullName());
        }
        return dto;
    }

    public DocumentApprovalRequestDto convertToFullDto() {
        DocumentApprovalRequestDto dto = convertToHalfDto();
        dto.setPendingDocumentType(getDocumentTypeDto());
        AuthInfo rejector = getRejector();
        if (rejector != null) {
            dto.setRejectorId(this.rejectorId);
            dto.setRejectorName(rejector.getFullName());
        }
        AuthInfo approver = getApprover();
        if (approver != null) {
            dto.setApproverId(this.approverId);
            dto.setApproverName(approver.getFullName());
        }

        AuthInfo newApprover = getAuthInfo(this.newApproverId);
        if (newApprover != null) {
            dto.setNewApproverId(this.newApproverId);
            dto.setNewApproverName(newApprover.getFullName());
        }
        return dto;
    }

    public DocumentTypeDto getDocumentTypeDto() {
        PendingDocumentType pendingDocumentType = getPendingDocumentType();
        if (pendingDocumentType != null) {
            return pendingDocumentType.convertToDto();
        }
        DocumentType documentType = getDocumentType();
        if (documentType != null) {
            return documentType.convertToDto();
        }
        return null;
    }

    public PendingDocumentType getPendingDocumentType() {
        if (StringUtils.isEmpty(this.pendingDocumentTypeId)) {
            return null;
        }
        return (PendingDocumentType) mongoRepositoryReactive.findById(this.pendingDocumentTypeId, PendingDocumentType.class).block();
    }

    public DocumentType getDocumentType() {
        if (StringUtils.isEmpty(this.documentTypeId)) {
            return null;
        }
        return (DocumentType) mongoRepositoryReactive.findById(this.documentTypeId, DocumentType.class).block();
    }

    public DocumentType getSubjectDocumentType() {
        DocumentType documentType = getDocumentType();
        if (documentType != null) {
            return documentType;
        }
        PendingDocumentType pendingDocumentType = getPendingDocumentType();
        if (pendingDocumentType != null) {
            return pendingDocumentType;
        }
        return new DocumentType();
    }

    public boolean isCreateDocumentType() {
        return StringUtils.equals(DocumentApprovalRequestTypeReferenceData.CREATE_DOCUMENT_TYPE_ID, this.documentApprovalRequestTypeId);
    }

    public boolean isSetApprover() {
        return StringUtils.equals(DocumentApprovalRequestTypeReferenceData.SET_APPROVER_ID, this.documentApprovalRequestTypeId);
    }

    public boolean isRemoveApprover() {
        return StringUtils.equals(DocumentApprovalRequestTypeReferenceData.REMOVE_APPROVER_ID, this.documentApprovalRequestTypeId);
    }
}
