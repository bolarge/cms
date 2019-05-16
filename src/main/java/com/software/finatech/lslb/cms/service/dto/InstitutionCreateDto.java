package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

public class InstitutionCreateDto {
    @NotEmpty(message = "Institution Name should not be empty")
    protected String institutionName;
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email should not be empty")
    protected String emailAddress;
    protected String description;
    @NotEmpty(message = "Phone Numbers should not be empty")
    protected Set<String> phoneNumbers = new HashSet<>();
    @NotEmpty(message = "Address should not be empty")
    protected String address;
    private String userId;
    protected Set<String> gameTypeIds = new HashSet<>();

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Set<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(Set<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }
}
