package com.software.finatech.lslb.cms.service.domain;

import org.joda.time.LocalDateTime;

public class CustomerComplainAction {
    private LocalDateTime actionTime;
    private String userId;
    private String complainStatusId;

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComplainStatusId() {
        return complainStatusId;
    }

    public void setComplainStatusId(String complainStatusId) {
        this.complainStatusId = complainStatusId;
    }
}
