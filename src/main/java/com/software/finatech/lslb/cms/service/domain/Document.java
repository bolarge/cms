package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.DocumentDto;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.bson.types.Binary;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.data.annotation.Transient;


@SuppressWarnings("serial")
@org.springframework.data.mongodb.core.mapping.Document(collection = "Documents")
public class Document extends  AbstractFact {

    protected String description;
    protected String filename;
    protected String mimeType;
    protected Binary file;
    protected LocalDateTime entryDate;
    protected LocalDate validFrom;
    protected LocalDate validTo;
    protected Boolean isCurrent;
    protected String documentTypeId;
    @Transient
    protected DocumentType documentType;
    protected String entity;
    protected String entityId;
    protected String previousDocument;
    protected String originalFilename;


    public Binary getFile() {
        return file;
    }

    public void setFile(Binary file) {
        this.file = file;
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

    public DocumentType getDocumentType() {
        return documentType;
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

    public void setAssociatedProperties() throws FactNotFoundException {
        if (documentTypeId != null) {
            DocumentType DocumentType = (DocumentType) Mapstore.STORE.get("DocumentType").get(documentTypeId);
            if (DocumentType == null) {
                DocumentType = (DocumentType) mongoRepositoryReactive.findById(documentTypeId, DocumentType.class).block();
                if (DocumentType == null) {
                    throw new FactNotFoundException("DocumentType", documentTypeId);
                } else {
                    Mapstore.STORE.get("DocumentType").put(DocumentType.getId(), DocumentType);
                }
            }
            setDocumentType(DocumentType);
        }
    }

    public DocumentDto convertToDto() {
        DocumentDto dto = new DocumentDto();
        dto.setEntityId(getEntityId());
        dto.setId(getId());
        dto.setCurrent(getCurrent());
        dto.setDescription(getDescription());
        dto.setDocumentTypeId(getDocumentTypeId());
        dto.setDocumentType(getDocumentType()==null?null:getDocumentType().convertToDto());
        dto.setEntity(getEntity());
        dto.setEntryDate(getEntryDate()==null?null:getEntryDate().toString(DateTimeFormat.longDateTime()));
        dto.setFilename(getFilename());
        dto.setOriginalFilename(getOriginalFilename());
        dto.setMimeType(getMimeType());
        dto.setPreviousDocument(getPreviousDocument());
        dto.setValidFrom(getValidFrom()==null?null:getValidFrom().toString("dd-MM-yyyy"));
        dto.setValidTo(getValidTo()==null?null:getValidTo().toString("dd-MM-yyyy"));

        return dto;
    }
}
