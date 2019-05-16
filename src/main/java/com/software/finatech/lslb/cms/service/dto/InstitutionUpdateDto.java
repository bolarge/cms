package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class InstitutionUpdateDto {

    @NotEmpty(message = "Please provide id")
    protected String id;
    @NotEmpty(message = "Please provide Institution Name")
    protected String institutionName;
    @NotEmpty(message = "Please email Address")
    @Email(message = "Email should be valid")
    protected String emailAddress;
    protected String description;
    @NotEmpty
    protected Set<String> phoneNumbers = new HashSet<>();
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

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
