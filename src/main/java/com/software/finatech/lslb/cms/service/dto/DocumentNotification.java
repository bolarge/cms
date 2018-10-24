package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.Document;

public class DocumentNotification {
    private String approverEmail;
    private Document document;

    public String getApproverEmail() {
        return approverEmail;
    }

    public void setApproverEmail(String approverEmail) {
        this.approverEmail = approverEmail;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public static DocumentNotification fromApproverEmailAndDocument(String approverEmail, Document document) {
        DocumentNotification documentNotification = new DocumentNotification();
        documentNotification.setApproverEmail(approverEmail);
        documentNotification.setDocument(document);
        return documentNotification;
    }
}
