package com.software.finatech.lslb.cms.service.dto.sso;

public class SSOPasswordResetResponse {
    protected String UserId ;
    protected String PasswordToken ;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getPasswordToken() {
        return PasswordToken;
    }

    public void setPasswordToken(String passwordToken) {
        PasswordToken = passwordToken;
    }
}
