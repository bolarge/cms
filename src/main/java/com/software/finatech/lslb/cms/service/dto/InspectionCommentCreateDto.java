package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class InspectionCommentCreateDto {

    @NotEmpty(message = "please provide logged case inspectionFormId")
    private String inspectionFormId;
    @NotEmpty(message = "please provide comment")
    private String comment;
    @NotEmpty(message = "please provide userId")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInspectionFormId() {
        return inspectionFormId;
    }

    public void setInspectionFormId(String inspectionFormId) {
        this.inspectionFormId = inspectionFormId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
