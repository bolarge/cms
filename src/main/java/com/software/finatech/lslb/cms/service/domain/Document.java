package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.CommentDetail;
import com.software.finatech.lslb.cms.service.dto.DocumentDto;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")
@org.springframework.data.mongodb.core.mapping.Document(collection = "Documents")
public class Document extends AbstractFact {

    protected String description;
    protected String filename;
    protected String mimeType;
    protected LocalDateTime entryDate;
    protected LocalDate validFrom;
    protected LocalDate validTo;
    protected boolean isCurrent;
    protected String documentTypeId;
    @Transient
    protected DocumentType documentType;
    protected String entity;
    protected String entityId;
    protected String unLinkedEntityId;
    protected String unLinkedInstitutionId;
    protected String previousDocumentId;
    protected String originalFilename;
    protected String institutionId;
    protected String gameTypeId;
    protected String agentId;
    protected Boolean notificationSent = false;
    protected String approvalRequestStatusId;
    protected LocalDate nextReminderDate;
    protected List<CommentDetail> comments = new ArrayList<>();
    //AWS Key of document in AWS
    private String awsObjectKey;

    public String getAwsObjectKey() {
        return awsObjectKey;
    }

    public void setAwsObjectKey(String awsObjectKey) {
        this.awsObjectKey = awsObjectKey;
    }

    public List<CommentDetail> getComments() {
        return comments;
    }

    public void setComments(List<CommentDetail> comments) {
        this.comments = comments;
    }

    public String getUnLinkedEntityId() {
        return unLinkedEntityId;
    }

    public void setUnLinkedEntityId(String unLinkedEntityId) {
        this.unLinkedEntityId = unLinkedEntityId;
    }

    public String getUnLinkedInstitutionId() {
        return unLinkedInstitutionId;
    }

    public void setUnLinkedInstitutionId(String unLinkedInstitutionId) {
        this.unLinkedInstitutionId = unLinkedInstitutionId;
    }

    public LocalDate getNextReminderDate() {
        return nextReminderDate;
    }

    public void setNextReminderDate(LocalDate nextReminderDate) {
        this.nextReminderDate = nextReminderDate;
    }

    public String getApprovalRequestStatusId() {
        if (approvalRequestStatusId == null) {
            return "";
        }
        return approvalRequestStatusId;
    }

    public void setApprovalRequestStatusId(String approvalRequestStatusId) {
        this.approvalRequestStatusId = approvalRequestStatusId;
    }

    public Boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    protected boolean archive;

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public String getPreviousDocumentId() {
        return previousDocumentId;
    }

    public void setPreviousDocumentId(String previousDocumentId) {
        this.previousDocumentId = previousDocumentId;
    }

    @Override
    public String getFactName() {
        return "Document";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

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

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDateTime entryDate) {
        this.entryDate = entryDate;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public void setDocumentType(DocumentType documentType) {
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

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public Institution getInstitution() {
        if (!StringUtils.isEmpty(institutionId)) {
            return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
        }
        return null;
    }

    public Agent getAgent() {
        if (!StringUtils.isEmpty(agentId)) {
            return (Agent) mongoRepositoryReactive.findById(agentId, Agent.class).block();
        }
        return null;
    }

    public String getOwner() {
        if (!StringUtils.isEmpty(agentId)) {
            return (getAgent() == null ? null : getAgent().getFullName());
        } else if (!StringUtils.isEmpty(institutionId)) {
            return (getInstitution() == null ? null : getInstitution().getInstitutionName());
        } else {
            return null;
        }
    }

    public DocumentDto convertToDto() {
        DocumentDto dto = new DocumentDto();
        dto.setEntityId(getEntityId());
        dto.setId(getId());
        dto.setArchive(isArchive());
        dto.setCurrent(getCurrent());
        dto.setDescription(getDescription());
        dto.setDocumentTypeId(getDocumentTypeId());
        DocumentType documentType = getDocumentType();
        if (documentType != null) {
            dto.setDocumentType(documentType.convertToDto());
            dto.setApproverId(documentType.getApproverId());
        }
        dto.setEntity(getEntity());
        dto.setEntryDate(getEntryDate() == null ? null : getEntryDate().toString("dd-MM-yyyy HH:mm:ss"));
        dto.setFilename(getFilename());
        dto.setInstitutionId(getInstitutionId());
        //dto.setInstitution(getInstitution() == null ? null : getInstitution().convertToDto());
        dto.setOriginalFilename(getOriginalFilename());
        dto.setMimeType(getMimeType());
        dto.setPreviousDocumentId(getPreviousDocumentId());
        dto.setValidFrom(getValidFrom() == null ? null : getValidFrom().toString("dd-MM-yyyy"));
        dto.setValidTo(getValidTo() == null ? null : getValidTo().toString("dd-MM-yyyy"));
        dto.setOwner(getOwner());
        dto.setGameTypeId(getGameTypeId());
        List<CommentDetail> commentDtos = getComments();
        Collections.reverse(commentDtos);
        dto.setComments(commentDtos);
        ApprovalRequestStatus status = getApprovalRequestStatus();
        if (status != null) {
            dto.setDocumentStatus(status.toString());
            dto.setDocumentStatusId(this.approvalRequestStatusId);
        }

        return dto;
    }


    public DocumentType getDocumentType() {
        if (StringUtils.isEmpty(this.documentTypeId)) {
            return null;
        }
        return (DocumentType) mongoRepositoryReactive.findById(this.documentTypeId, DocumentType.class).block();
    }

    public ApplicationForm getApplicationForm() {
        if (StringUtils.isEmpty(this.entityId)) {
            return null;
        }
        return (ApplicationForm) mongoRepositoryReactive.findById(this.entityId, ApplicationForm.class).block();
    }

    public RenewalForm getRenewalForm() {
        if (StringUtils.isEmpty(this.entityId)) {
            return null;
        }
        return (RenewalForm) mongoRepositoryReactive.findById(this.entityId, RenewalForm.class).block();
    }

    public AIPDocumentApproval getAIPForm() {
        if (StringUtils.isEmpty(this.entityId)) {
            return null;
        }
        return (AIPDocumentApproval) mongoRepositoryReactive.findById(this.entityId, AIPDocumentApproval.class).block();

    }

    public AuthInfo getApprover() {
        DocumentType documentType = getDocumentType();
        if (documentType != null) {
            return documentType.getApprover();
        }
        return null;
    }

    public ApprovalRequestStatus getApprovalRequestStatus() {
        if (StringUtils.isEmpty(this.approvalRequestStatusId)) {
            return null;
        }
        Map approvalRequestStatusMap = Mapstore.STORE.get("ApprovalRequestStatus");
        ApprovalRequestStatus approvalRequestStatus = null;
        if (approvalRequestStatusMap != null) {
            approvalRequestStatus = (ApprovalRequestStatus) approvalRequestStatusMap.get(this.approvalRequestStatusId);
        }
        if (approvalRequestStatus == null) {
            approvalRequestStatus = (ApprovalRequestStatus) mongoRepositoryReactive.findById(this.approvalRequestStatusId, ApprovalRequestStatus.class).block();
            if (approvalRequestStatus != null && approvalRequestStatusMap != null) {
                approvalRequestStatusMap.put(this.approvalRequestStatusId, approvalRequestStatus);
            }
        }
        return approvalRequestStatus;
    }

    public boolean requiresApproval() {
        DocumentType documentType = getDocumentType();
        if (documentType != null) {
            return !StringUtils.isEmpty(documentType.getApproverId());
        }
        return false;
    }
}
