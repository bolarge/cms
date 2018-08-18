package com.software.finatech.lslb.cms.service.dto.sso;


import com.software.finatech.lslb.cms.service.dto.AuthInfoDto;

public class SSOToken {

    protected String access_token ;
    protected String token_type ;
    protected int expires_in ;
    protected String refresh_token;
    protected String error ;
    protected AuthInfoDto authInfo;

    public AuthInfoDto getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(AuthInfoDto authInfo) {
        this.authInfo = authInfo;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
