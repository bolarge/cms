package com.software.finatech.lslb.cms.service.dto;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    @Size(min = 9, message = "Phone number should be at least 9 characters")
    @NotEmpty(message = "please provide agent phone number")
    private String phoneNumber;
    @NotEmpty(message = "please provide agent date of birth")
    private String dateOfBirth;
    @NotEmpty(message = "please provide agent residential address")
    private String residentialAddress;
    @NotEmpty(message = "please provide agent means of id")
    private String meansOfId;
    @NotEmpty(message = "please provide agent id number")
    private String idNumber;
    @NotEmpty(message = "please provide agent BVN")
    private String bvn;
    private String passportId;
    @NotEmpty(message = "please provide agent business addresses")
    private Set<String> businessAddressList = new HashSet<>();
    @NotEmpty(message = "please provide institutionId")
    private String institutionId;
    @NotEmpty(message = "please provide gameTypeId")
    private String gameTypeId;
    private String middleName;

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public Set<String> getBusinessAddressList() {
        return businessAddressList;
    }

    public void setBusinessAddressList(Set<String> businessAddressList) {
        this.businessAddressList = businessAddressList;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

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

}
