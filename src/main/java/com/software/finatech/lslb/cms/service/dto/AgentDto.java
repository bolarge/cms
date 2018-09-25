package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.Institution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgentDto {
    private List<AgentInstitutionDto> agentInstitutions =new ArrayList<>();
    private String firstName;
    private String lastName;
    private String residentialAddress;
    private String fullName;
    private String emailAddress;
    private String phoneNumber;
    private String dateOfBirth;
    private String meansOfId;
    private String passportId;
    private String idNumber;
    private String id;
    private Boolean enabled;
    private List<EnumeratedFactDto> gameTypes = new ArrayList<>();
    private List<Institution> institutions = new ArrayList<>();
    private String title;
    private String bvn;
    private String agentId;
    private List<String> businessAddresses = new ArrayList<>();

    public List<AgentInstitutionDto> getAgentInstitutions() {
        return agentInstitutions;
    }

    public void setAgentInstitutions(List<AgentInstitutionDto> agentInstitutions) {
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

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
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

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<EnumeratedFactDto> getGameTypes() {
        return gameTypes;
    }

    public void setGameTypes(List<EnumeratedFactDto> gameTypes) {
        this.gameTypes = gameTypes;
    }

    public List<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public List<String> getBusinessAddresses() {
        return businessAddresses;
    }

    public void setBusinessAddresses(List<String> businessAddresses) {
        this.businessAddresses = businessAddresses;
    }
}
