package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class RemoveDocumentTypeApproverRequest {

    @NotEmpty
    private String documentTypeId;

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }
}
