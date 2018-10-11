package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AgentUpdateDto {
    @NotEmpty(message = "please provide agent id")
    private String id;
    @Size(min = 9, message = "Phone number should not be less than 9 characters")
    @NotEmpty(message = "please provide agent phone number")
    private String phoneNumber;
    @NotEmpty(message = "please provide agent residential address")
    private String residentialAddress;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }
}
