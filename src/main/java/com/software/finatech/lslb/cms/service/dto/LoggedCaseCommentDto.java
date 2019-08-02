package com.software.finatech.lslb.cms.service.dto;

public class LoggedCaseCommentDto {
    private String commentTime;
    private String userFulName;
    private String comment;

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getUserFulName() {
        return userFulName;
    }

    public void setUserFulName(String userFulName) {
        this.userFulName = userFulName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
