package com.software.finatech.lslb.cms.service.dto.sso;

public class UserRegisterResponse {
    protected String UserId ;
    protected String EmailConfirmationToken ;
    protected String PasswordResetToken ;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getEmailConfirmationToken() {
        return EmailConfirmationToken;
    }

    public void setEmailConfirmationToken(String emailConfirmationToken) {
        EmailConfirmationToken = emailConfirmationToken;
    }

    public String getPasswordResetToken() {
        return PasswordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        PasswordResetToken = passwordResetToken;
    }
}
