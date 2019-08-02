package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class AddCommentDto {
    @NotEmpty
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
