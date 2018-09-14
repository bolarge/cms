package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateApplicantAuthInfoDto {
    @NotNull(message = "First Name field can not be empty")
    protected String firstName;
    @NotNull(message = "Last Name field can not be empty")
    protected String lastName;
    @NotNull(message = "Phone Number field can not be empty")
    @Size(min = 7)
    @NotNull(message = "Phone Number field can not be empty")
    protected String phoneNumber;
    @NotNull(message = "Email Address field can not be empty")
    protected String emailAddress;
    @NotNull(message = "Title field can not be empty")
    protected String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
