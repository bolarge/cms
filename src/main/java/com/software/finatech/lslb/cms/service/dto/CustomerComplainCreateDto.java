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
    @Size(min = 9, message = "Phone number should be at least 9 characters")
    @NotEmpty(message = "please provide agent phone number")
    private String phoneNumber;
    private String dateOfIncident;
    private String timeOfIncident;
    private String address;
    @NotEmpty(message = "Please provide state of residence")
    private String stateOfResidence;
    @NotEmpty(message = "Please provide name of operator")
    private String nameOfOperator;
    @NotEmpty(message = "Please provide complain subject")
    private String complainSubject;
    @NotEmpty(message = "Please provide complain details")
    private String complainDetail;

    public String getComplainSubject() {
        return complainSubject;
    }

    public void setComplainSubject(String complainSubject) {
        this.complainSubject = complainSubject;
    }

    public String getComplainDetail() {
        return complainDetail;
    }

    public void setComplainDetail(String complainDetails) {
        this.complainDetail = complainDetails;
    }

    public String getDateOfIncident() {
        return dateOfIncident;
    }

    public void setDateOfIncident(String dateOfIncident) {
        this.dateOfIncident = dateOfIncident;
    }

    public String getTimeOfIncident() {
        return timeOfIncident;
    }

    public void setTimeOfIncident(String timeOfIncident) {
        this.timeOfIncident = timeOfIncident;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStateOfResidence() {
        return stateOfResidence;
    }

    public void setStateOfResidence(String stateOfResidence) {
        this.stateOfResidence = stateOfResidence;
    }

    public String getNameOfOperator() {
        return nameOfOperator;
    }

    public void setNameOfOperator(String nameOfOperator) {
        this.nameOfOperator = nameOfOperator;
    }

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
}