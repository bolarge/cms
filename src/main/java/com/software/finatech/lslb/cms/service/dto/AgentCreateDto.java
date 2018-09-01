package com.software.finatech.lslb.cms.service.dto;


import com.software.finatech.lslb.cms.service.domain.AgentInstitution;

import java.util.HashSet;
import java.util.Set;

public class AgentCreateDto {
    private String firstName;
    private String lastName;
    private String fullName;
    private String emailAddress;
    private String phoneNumber;
    private String dateOfBirth;
    private String residentialAddress;
    private Set<String> businessAddresses = new HashSet<>();
    private String meansOfId;
    private String idNumber;
    private String bvn;
    private Set<AgentInstitution> agentInstitutions = new HashSet<>();


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
