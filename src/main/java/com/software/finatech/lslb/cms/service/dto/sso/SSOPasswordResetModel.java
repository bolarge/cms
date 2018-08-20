package com.software.finatech.lslb.cms.service.dto.sso;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.advantageous.boon.json.annotations.SerializedName;

import javax.validation.constraints.NotEmpty;

public class SSOPasswordResetModel {
    @NotEmpty(message = "Please provide UserId")
    @SerializedName("UserId")
    @JsonProperty("UserId")
    protected String userId ;
    //@NotEmpty(message = "Please provide Token")
    @SerializedName("Token")
    @JsonProperty("Token")
    protected String token ;
    @NotEmpty(message = "Please provide NewPassword")
    @SerializedName("NewPassword")
    @JsonProperty("NewPassword")
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
