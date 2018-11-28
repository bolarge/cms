package com.software.finatech.lslb.cms.service.domain;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;


@SuppressWarnings("serial")
@Document(collection = "DocumentBinary")
public class DocumentBinary extends AbstractFact {


    protected Binary file;
    protected String documentId;

    public Binary getFile() {
        return file;
    }

    public void setFile(Binary file) {
        this.file = file;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public String getFactName() {
        return "DocumentBinary";
    }
}
