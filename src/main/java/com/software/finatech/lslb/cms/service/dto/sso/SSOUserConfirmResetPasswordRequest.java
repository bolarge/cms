package com.software.finatech.lslb.cms.service.dto.sso;

public class SSOUserConfirmResetPasswordRequest {
    protected String Token;
    protected String UserId;

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
