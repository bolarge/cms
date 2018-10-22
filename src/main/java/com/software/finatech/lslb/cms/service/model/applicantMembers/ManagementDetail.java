package com.software.finatech.lslb.cms.service.model.applicantMembers;

public class ManagementDetail {
    private String firstName;
    private String surname;
    private String nationality;
    private String address;
    private String educationalQualification;
    private String position;
    private Boolean managementHasGamingIndustryExperience;
    private int numberOfYears;
    private String detailsOfExperience;

    public Boolean getManagementHasGamingIndustryExperience() {
        return managementHasGamingIndustryExperience;
    }

    public void setManagementHasGamingIndustryExperience(Boolean managementHasGamingIndustryExperience) {
        this.managementHasGamingIndustryExperience = managementHasGamingIndustryExperience;
    }

    public int getNumberOfYears() {
        return numberOfYears;
    }

    public void setNumberOfYears(int numberOfYears) {
        this.numberOfYears = numberOfYears;
    }

    public String getDetailsOfExperience() {
        return detailsOfExperience;
    }

    public void setDetailsOfExperience(String detailsOfExperience) {
        this.detailsOfExperience = detailsOfExperience;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEducationalQualification() {
        return educationalQualification;
    }

    public void setEducationalQualification(String educationalQualification) {
        this.educationalQualification = educationalQualification;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
