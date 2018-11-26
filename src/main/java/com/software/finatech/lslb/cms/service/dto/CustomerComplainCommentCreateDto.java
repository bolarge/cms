package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class CustomerComplainCommentCreateDto {
    @NotEmpty
    private String complaintId;
    @NotEmpty
    private String comment;

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
