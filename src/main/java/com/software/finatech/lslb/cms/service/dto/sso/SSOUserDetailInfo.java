package com.software.finatech.lslb.cms.service.dto.sso;

import cucumber.api.java.eo.Se;
import io.advantageous.boon.json.annotations.SerializedName;

import java.util.Set;

public class SSOUserDetailInfo {

    @SerializedName("Id")
    protected String id ;

    @SerializedName("UserName")
    protected String userName ;

    @SerializedName("EmailConfirmed")
    protected Boolean emailConfirmed ;

    @SerializedName("Email")
    protected String email ;

    @SerializedName("Paging")
    protected Object paging ;

    @SerializedName("Claims")
    protected Set<SSOClaim>claims = new java.util.HashSet<>();

    @SerializedName("PhoneNumber")
    protected String phoneNumber;

    @SerializedName("LockoutEnabled")
    public boolean locked;

    @SerializedName("FirstName")
    public String firstName;

    @SerializedName("LastName")
    public String lastName;


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(Boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Object getPaging() {
        return paging;
    }

    public void setPaging(Object paging) {
        this.paging = paging;
    }

    public Set<SSOClaim> getClaims() {
        return claims;
    }

    public void setClaims(Set<SSOClaim> claims) {
        this.claims = claims;
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
}
