package com.software.finatech.lslb.cms.userservice.dto;

public class AuthInfoCompleteDto {

    protected String token ;
    protected String password ;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
