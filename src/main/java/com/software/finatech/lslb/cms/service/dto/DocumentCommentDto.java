package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class DocumentCommentDto {
   @NotEmpty(message = "please provide document id")
    private String documentId;
   @NotEmpty(message = "please provide comment")
    private String comment;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
