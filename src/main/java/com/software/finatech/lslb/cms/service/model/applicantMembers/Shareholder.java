package com.software.finatech.lslb.cms.service.model.applicantMembers;

public class Shareholder {
    private String firstName;
    private String surname;
    private String address;
    private String numberOfShares;
    private String nationality;

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumberOfShares() {
        return numberOfShares;
    }

    public void setNumberOfShares(String numberOfShares) {
        this.numberOfShares = numberOfShares;
    }
}
