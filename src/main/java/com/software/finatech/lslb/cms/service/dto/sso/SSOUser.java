package com.software.finatech.lslb.cms.service.dto.sso;

import io.advantageous.boon.json.annotations.SerializedName;

import java.util.Set;

public class SSOUser {

    @SerializedName("PhoneNumber")
    protected String phoneNumber ;
    @SerializedName("UserName")
    protected String userName ;
    @SerializedName("ConfirmEmail")
    protected Boolean confirmEmail ;
    @SerializedName("Email")
    protected String email ;
    @SerializedName("FirstName")
    protected String firstName ;
    @SerializedName("LastName")
    protected String lastName ;
    @SerializedName("Password")
    protected String password ;
    @SerializedName("Claims")
    protected Set<SSOClaim> claims = new java.util.HashSet<>();

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getConfirmEmail() {
        return confirmEmail;
    }

    public void setConfirmEmail(Boolean confirmEmail) {
        this.confirmEmail = confirmEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<SSOClaim> getClaims() {
        return claims;
    }

    public void setClaims(Set<SSOClaim> claims) {
        this.claims = claims;
    }
}
