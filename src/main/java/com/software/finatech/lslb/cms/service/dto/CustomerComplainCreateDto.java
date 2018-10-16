package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CustomerComplainCreateDto {
    @NotEmpty(message = "Please provide full name")
    private String fullName;
    @Email
    @NotEmpty(message = "please provide agent email address")
    private String emailAddress;
   // @Size(min = 9, message = "Phone number should be at least 9 characters")
    @NotEmpty(message = "please provide agent phone number")
    private String phoneNumber;
    @NotEmpty(message = "Please provide complain subject")
    private String complainSubject;
    @NotEmpty(message = "Please provide complain details")
    private String complainDetail;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getComplainSubject() {
        return complainSubject;
    }

    public void setComplainSubject(String complainSubject) {
        this.complainSubject = complainSubject;
    }

    public String getComplainDetail() {
        return complainDetail;
    }

    public void setComplainDetail(String complainDetail) {
        this.complainDetail = complainDetail;
    }
}
