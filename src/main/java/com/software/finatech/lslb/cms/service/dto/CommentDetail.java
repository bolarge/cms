package com.software.finatech.lslb.cms.service.dto;


import org.joda.time.LocalDateTime;

public class CommentDetail {
    private String commentTime;
    private String commentDate;
    private String userFullName;
    private String comment;

    public CommentDetail() {
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static CommentDetail fromCommentAndUser(String comment, String userFullName) {
        CommentDetail dto = new CommentDetail();
        dto.setCommentDate(LocalDateTime.now().toString("dd-MM-yyyy"));
        dto.setCommentTime(LocalDateTime.now().toString("hh:mm a"));
        dto.setComment(comment);
        dto.setUserFullName(userFullName);
        return dto;
    }
}
