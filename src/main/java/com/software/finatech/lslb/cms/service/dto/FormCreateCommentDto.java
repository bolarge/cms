package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class FormCreateCommentDto {
    @NotEmpty(message = "please provider user id")
    private String userId;
    @NotEmpty(message = "please provider user comment")
    private String comment;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
