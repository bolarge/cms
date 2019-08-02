package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class UserRoleUpdateDto {

    @NotEmpty(message = "Please provide the user id ")
    private String userId;
    @NotEmpty(message = "Please provide new role id ")
    private String newRoleId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getNewRoleId() {
        return newRoleId;
    }

    public void setNewRoleId(String newRoleId) {
        this.newRoleId = newRoleId;
    }
}
