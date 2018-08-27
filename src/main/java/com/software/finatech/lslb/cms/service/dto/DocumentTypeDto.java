package com.software.finatech.lslb.cms.service.dto;


public class DocumentTypeDto extends EnumeratedFactDto{

    protected String documentPurposeId;
    protected DocumentPurposeDto documentPurpose;

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
