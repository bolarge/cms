package com.software.finatech.lslb.cms.service.model.migrations;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author adeyi.adebolu
 * created on 27/05/2019
 */
public class NewMigratedAgent {
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String emailAddress;
    @NotEmpty
    private String bvn;
    @NotEmpty
    private String meansOfIdentification;
    @NotEmpty
    private String phoneNumber;
    @NotEmpty
    private Set<String> phoneNumbers = new HashSet<>();
    @NotEmpty
    private String dateOfBirth;
    @NotEmpty
    private String residentialAddress;
    @NotEmpty
    private String genderId;
    @NotEmpty
    private String title;
    @NotEmpty
    private String idNumber;
    @NotEmpty
    private List<NewMigratedAgentInstitution> newMigratedAgentInstitutions = new ArrayList<>();


    public String getGenderId() {
        return genderId;
    }

    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getMeansOfIdentification() {
        return meansOfIdentification;
    }

    public void setMeansOfIdentification(String meansOfIdentification) {
        this.meansOfIdentification = meansOfIdentification;
    }

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
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
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NewMigratedAgentInstitution> getNewMigratedAgentInstitutions() {
        return newMigratedAgentInstitutions;
    }

    public void setNewMigratedAgentInstitutions(List<NewMigratedAgentInstitution> newMigratedAgentInstitutions) {
        this.newMigratedAgentInstitutions = newMigratedAgentInstitutions;
    }
}
