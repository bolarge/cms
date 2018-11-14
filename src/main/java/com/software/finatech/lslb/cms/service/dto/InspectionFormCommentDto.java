package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;

public class InspectionFormCommentDto {
    protected String inspectionFormId;
    protected String comment;
    protected String commenter;
    protected String createdAt;


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

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
