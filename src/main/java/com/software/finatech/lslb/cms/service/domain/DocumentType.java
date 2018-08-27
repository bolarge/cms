package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.DocumentTypeDto;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


@Document(collection = "DocumentTypes")
public class DocumentType extends EnumeratedFact{

    protected String documentPurposeId;
    @Transient
    protected DocumentPurpose documentPurpose;

    public String getDocumentPurposeId() {
        return documentPurposeId;
    }

    public void setDocumentPurposeId(String documentPurposeId) {
        this.documentPurposeId = documentPurposeId;
    }

    public DocumentPurpose getDocumentPurpose() {
        return documentPurpose;
    }

    public void setDocumentPurpose(DocumentPurpose documentPurpose) {
        this.documentPurpose = documentPurpose;
    }

    @Override
    public String getFactName() {
        return "DocumentType";
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setAssociatedProperties() throws FactNotFoundException {
        if (documentPurposeId != null) {
            DocumentPurpose DocumentPurpose = (DocumentPurpose) Mapstore.STORE.get("DocumentPurpose").get(documentPurposeId);
            if (DocumentPurpose == null) {
                DocumentPurpose = (DocumentPurpose) mongoRepositoryReactive.findById(documentPurposeId, DocumentPurpose.class).block();
                if (DocumentPurpose == null) {
                    throw new FactNotFoundException("DocumentPurpose", documentPurposeId);
                } else {
                    Mapstore.STORE.get("DocumentPurpose").put(DocumentPurpose.getId(), DocumentPurpose);
                }
            }
            setDocumentPurpose(DocumentPurpose);
        }
    }

    public DocumentTypeDto convertToDto() {
        DocumentTypeDto dto = new DocumentTypeDto();
        dto.setName(getName());
        dto.setId(getId());
        dto.setDocumentPurposeId(getDocumentPurposeId());
        dto.setDescription(getDescription());
        dto.setDocumentPurpose(getDocumentPurpose()==null?null:getDocumentPurpose().convertToDto());
        return dto;
    }
}
