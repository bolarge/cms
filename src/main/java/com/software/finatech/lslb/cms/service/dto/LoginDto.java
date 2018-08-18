package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class LoginDto {

    @NotEmpty(message = "Please provide userName")
    protected String userName ;
    @NotEmpty(message = "Please provide password")
    protected String password ;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
