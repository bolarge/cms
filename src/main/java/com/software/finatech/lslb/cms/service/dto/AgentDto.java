package com.software.finatech.lslb.cms.service.dto;

import java.util.HashSet;
import java.util.Set;

public class AgentDto {
    private Set<AgentInstitutionDto> agentInstitutions = new HashSet<>();
    private String firstName;
    private String lastName;
    private Set<String> businessAddresses;
    private String residentialAddress;
    private String emailAddress;
    private String phoneNumber;
    private String dateOfBirth;
    private String meansOfId;
    private String passportId;
    private String fullName;
    private String idNumber;
    private String id;
    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<AgentInstitutionDto> getAgentInstitutions() {
        return agentInstitutions;
    }


    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setAgentInstitutions(Set<AgentInstitutionDto> agentInstitutions) {
        this.agentInstitutions = agentInstitutions;
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

    public Set<String> getBusinessAddresses() {
        return businessAddresses;
    }

    public void setBusinessAddresses(Set<String> businessAddresses) {
        this.businessAddresses = businessAddresses;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
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

    public String getMeansOfId() {
        return meansOfId;
    }

    public void setMeansOfId(String meansOfId) {
        this.meansOfId = meansOfId;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
