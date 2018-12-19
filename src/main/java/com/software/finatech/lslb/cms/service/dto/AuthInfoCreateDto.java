package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AuthInfoCreateDto {
    @NotEmpty(message = "First Name field can not be empty")
    protected String firstName;
    @NotEmpty(message = "Last Name field can not be empty")
    protected String lastName;
    @NotEmpty(message = "Phone Number field can not be empty")
    @Size(min = 9, message = "Phone number should not be less than 9 characters")
    protected String phoneNumber;
    @NotEmpty(message = "Email Address field can not be empty")
    protected String emailAddress;
    @NotEmpty(message = "Role ID field can not be empty")
    protected String authRoleId;
    @NotEmpty(message = "Title field can not be empty")
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

    public boolean isCreateGamingOperatorUser(){
        return StringUtils.equals(LSLBAuthRoleReferenceData.GAMING_OPERATOR_ROLE_ID, this.authRoleId);
    }
}
