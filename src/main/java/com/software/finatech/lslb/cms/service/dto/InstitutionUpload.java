package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

public class InstitutionUpload {
    private String institutionName;
    private String emailAddress;
    private String phoneNumber;
    private String address;
    private String description;
    private String line;
    private List<InstitutionLoadDetails> institutionLoadDetails = new ArrayList<>();

    public List<InstitutionLoadDetails> getInstitutionLoadDetails() {
        return institutionLoadDetails;
    }

    public void setInstitutionLoadDetails(List<InstitutionLoadDetails> institutionLoadDetails) {
        this.institutionLoadDetails = institutionLoadDetails;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
