package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LoggedCaseCommentCreateDto {

    @NotEmpty(message = "please provide logged case id")
    private String caseId;
    @NotEmpty(message = "please provide comment")
    private String comment;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
