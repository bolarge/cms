package com.software.finatech.lslb.cms.service.dto;


import com.software.finatech.lslb.cms.service.domain.AgentInstitution;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

public class AgentCreateDto {
    @NotEmpty(message = "please provide agent first name")
    private String firstName;
    @NotEmpty(message = "please provide agent last name")
    private String lastName;
    private String fullName;
    @Email
    @NotEmpty(message = "please provide agent email address")
    private String emailAddress;
    @Size(min = 9,  message= "Phone number should be at least 9 characaters")
    @NotEmpty(message = "please provide agent phone number")
    private String phoneNumber;
    @NotEmpty(message = "please provide agent date of birth")
    private String dateOfBirth;
    @NotEmpty(message = "please provide agent residential address")
    private String residentialAddress;
    @NotEmpty(message = "please provide agent business addresses")
    private Set<String> businessAddresses = new HashSet<>();
    @NotEmpty(message = "please provide agent means of id")
    private String meansOfId;
    @NotEmpty(message = "please provide agent id number")
    private String idNumber;
    @NotEmpty(message = "please provide agent BVN")
    private String bvn;
    @NotEmpty(message = "please provide agent institutions")
    private Set<AgentInstitution> agentInstitutions = new HashSet<>();
    private String passportId;

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public Set<String> getBusinessAddresses() {
        return businessAddresses;
    }

    public void setBusinessAddresses(Set<String> businessAddresses) {
        this.businessAddresses = businessAddresses;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getMeansOfId() {
        return meansOfId;
    }

    public void setMeansOfId(String meansOfId) {
        this.meansOfId = meansOfId;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public Set<AgentInstitution> getAgentInstitutions() {
        return agentInstitutions;
    }

    public void setAgentInstitutions(Set<AgentInstitution> agentInstitutions) {
        this.agentInstitutions = agentInstitutions;
    }
}
