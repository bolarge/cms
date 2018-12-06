package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AuthInfoUpdateDto {
    @NotEmpty(message = "Please provide id")
    protected String id;
    @NotNull(message = "First Name field can not be empty")
    protected String firstName ;
    @NotNull(message = "Last Name field can not be empty")
    protected String lastName ;
    @NotNull(message = "Phone Number field can not be empty")
    @Size(min = 9, message = "Phone number should not be less than 9 characters")
    protected String phoneNumber ;
    protected String attachmentId;
    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
