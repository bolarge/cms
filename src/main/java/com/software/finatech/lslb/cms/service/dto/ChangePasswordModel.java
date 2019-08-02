package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class ChangePasswordModel {
    @NotEmpty(message = "Please provide UserId")
    protected String userId ;
    @NotEmpty(message = "Please provide CurrentPassword")
    protected String currentPassword ;
    @NotEmpty(message = "Please provide NewPassword")
    protected String newPassword ;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
