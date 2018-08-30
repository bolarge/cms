package com.software.finatech.lslb.cms.service.domain;

public class LslbAdminComment {
    private String userId;
    private String comment;

    public LslbAdminComment(String userId, String comment) {
        this.userId = userId;
        this.comment = comment;
    }

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
