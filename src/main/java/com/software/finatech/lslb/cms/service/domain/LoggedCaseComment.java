package com.software.finatech.lslb.cms.service.domain;

import org.joda.time.LocalDateTime;

public class LoggedCaseComment {
    private String userId;
    private String comment;
    private LocalDateTime commentTime;

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

    public LocalDateTime getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(LocalDateTime commentTime) {
        this.commentTime = commentTime;
    }
}
