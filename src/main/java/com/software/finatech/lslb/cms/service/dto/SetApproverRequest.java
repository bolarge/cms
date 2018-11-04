package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class SetApproverRequest {
    @NotEmpty(message = "Please provide document type id")
    private String documentTypeId;
    @NotEmpty(message = "Please provide approver id")
    private String approverId;

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }
}
