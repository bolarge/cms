package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class InstitutionCreateDto {
    @NotEmpty(message = "Institution Name should not be empty")
    protected String institutionName;
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email should not be empty")
    protected String emailAddress;
    protected String description;
    @NotEmpty(message = "Phone Number should not be empty")
    protected String phoneNumber;

    protected Set<String> gameTypeIds = new HashSet<>();

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(Set<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }
}
