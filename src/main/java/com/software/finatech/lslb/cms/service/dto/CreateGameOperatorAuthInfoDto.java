package com.software.finatech.lslb.cms.userservice.dto;

import javax.validation.constraints.NotNull;

public class CreateGameOperatorAuthInfoDto {
    @NotNull(message = "Name field can not be empty")
    protected String name ;
    @NotNull(message = "Phone Number field can not be empty")
    protected String phoneNumber ;
    @NotNull(message = "Email Address field can not be empty")
    protected String emailAddress;
    @NotNull(message = "Role ID field can not be empty")
    protected String authRoleId;
    @NotNull(message = "Game Type ID field can not be empty")
    protected  String gameTypeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAuthRoleId() {
        return authRoleId;
    }

    public void setAuthRoleId(String authRoleId) {
        this.authRoleId = authRoleId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }
}
