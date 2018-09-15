package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class ApplicationFormRejectDto {
    @NotEmpty(message = "Please provider rejecting user id")
    private String userId;
    @NotEmpty(message = "Please provide rejection reason")
    private String reason;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
