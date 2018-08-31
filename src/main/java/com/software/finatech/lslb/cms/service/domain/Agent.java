package com.software.finatech.lslb.cms.service.domain;


import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "Agents")
public class Agent extends AbstractFact {

    protected String passportId;
    protected String firstName;
    protected String lastName;
    protected String fullName;
    protected String emailAddress;
    protected String phoneNumber;
    protected Set<AgentInstitution> agentInstitutionList = new HashSet<>();


    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
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

    public Set<AgentInstitution> getAgentInstitutionList() {
        return agentInstitutionList;
    }

    public void setAgentInstitutionList(Set<AgentInstitution> agentInstitutionList) {
        this.agentInstitutionList = agentInstitutionList;
    }

    @Override
    public String getFactName() {
        return "Agent";
    }
}
