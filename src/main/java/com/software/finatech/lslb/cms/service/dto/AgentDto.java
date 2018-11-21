package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

public class AgentDto {
    private List<AgentInstitutionDto> agentInstitutions = new ArrayList<>();
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
    private List<InstitutionDto> institutions = new ArrayList<>();
    private String title;
    private String bvn;
    private String agentId;
    private List<MachineDto> gamingTerminals = new ArrayList<>();
    private List<String> businessAddresses = new ArrayList<>();
    private String agentStatusId;
    private String agentStatusName;
    private String genderId;
    private String genderName;

    public String getGenderId() {
        return genderId;
    }

    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public String getAgentStatusId() {
        return agentStatusId;
    }

    public void setAgentStatusId(String agentStatusId) {
        this.agentStatusId = agentStatusId;
    }

    public String getAgentStatusName() {
        return agentStatusName;
    }

    public void setAgentStatusName(String agentStatusName) {
        this.agentStatusName = agentStatusName;
    }

    public List<InstitutionDto> getInstitutions() {
        return institutions;
    }

    public List<MachineDto> getGamingTerminals() {
        return gamingTerminals;
    }

    public void setGamingTerminals(List<MachineDto> gamingTerminals) {
        this.gamingTerminals = gamingTerminals;
    }

    public void setInstitutions(List<InstitutionDto> institutions) {
        this.institutions = institutions;
    }

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
