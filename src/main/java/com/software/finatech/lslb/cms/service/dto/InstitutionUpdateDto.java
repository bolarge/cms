package com.software.finatech.lslb.cms.service.dto;

import org.springframework.data.annotation.Transient;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

public class InstitutionUpdateDto  {

    @NotEmpty(message = "Please provide id")
    protected String id;
    @NotEmpty(message = "Please provide Institution Name")
    protected String institutionName;
    @NotEmpty(message = "Please email Address")
    @Email(message = "Email should be valid")
    protected String emailAddress;
    protected String description;
    @Size(min = 9, message = "Phone number should not be less than 9 characters")
    @NotEmpty
    protected String phoneNumber;
    protected Set<String> gameTypeIds = new java.util.HashSet<>();

    public Set<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(Set<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
