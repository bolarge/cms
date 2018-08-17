package com.software.finatech.lslb.cms.userservice.dto;

import javax.validation.constraints.NotEmpty;

public class PasswordResetModel {
    @NotEmpty(message = "Please provide UserId")
    protected String userId ;
    @NotEmpty(message = "Please provide Token")
    protected String token ;
    @NotEmpty(message = "Please provide NewPassword")
    protected String newPassword ;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
