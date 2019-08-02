package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.model.migrations.BaseInstitutionUpload;

public class InstitutionUpload extends BaseInstitutionUpload {
    private String institutionName;
    private String phoneNumber;
    private String address;
    private String description;
    private String line;
    private InstitutionLoadDetails loadDetails;

    public InstitutionLoadDetails getLoadDetails() {
        return loadDetails;
    }

    public void setLoadDetails(InstitutionLoadDetails loadDetails) {
        this.loadDetails = loadDetails;
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
