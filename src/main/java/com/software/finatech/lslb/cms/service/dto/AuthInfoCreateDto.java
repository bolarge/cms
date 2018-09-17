package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AuthInfoCreateDto {
    @NotNull(message = "First Name field can not be empty")
    protected String firstName;
    @NotNull(message = "Last Name field can not be empty")
    protected String lastName;
    @NotNull(message = "Phone Number field can not be empty")
    @Size(min = 9, message = "Phone number should not be less than 9 characters")
    protected String phoneNumber;
    @NotNull(message = "Email Address field can not be empty")
    protected String emailAddress;
    @NotNull(message = "Role ID field can not be empty")
    protected String authRoleId;
    @NotNull(message = "Title field can not be empty")
    protected String title;
    protected String institutionId;
    private String agentId;


    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
