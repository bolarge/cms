package com.software.finatech.lslb.cms.service.util.adapters.model;

import java.util.ArrayList;
import java.util.List;

public class DeviceMagicAgent {
    private String title;
    private String submittingDevice;
    private String deviceUser;
    private String formCompletedDate;
    private String uploadCompletedTime;
    private String submissionId;
    private String submissionIdGeneratedByDevice;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String phoneNumber1;
    private String phoneNumber2;
    private String residentialAddressStreet;
    private String residentialAddressCity;
    private String residentialAddressState;
    private String email;
    private String bvn;
    private String meansOfId;
    private String idNumber;
    private String imageBase64;
    private String imageFileName;
    private String gender;
    private List<DeviceMagicAgentInstitutionCategoryDetails> institutionCategoryDetailsList = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubmittingDevice() {
        return submittingDevice;
    }

    public void setSubmittingDevice(String submittingDevice) {
        this.submittingDevice = submittingDevice;
    }

    public String getDeviceUser() {
        return deviceUser;
    }

    public void setDeviceUser(String deviceUser) {
        this.deviceUser = deviceUser;
    }

    public String getFormCompletedDate() {
        return formCompletedDate;
    }

    public void setFormCompletedDate(String formCompletedDate) {
        this.formCompletedDate = formCompletedDate;
    }

    public String getUploadCompletedTime() {
        return uploadCompletedTime;
    }

    public void setUploadCompletedTime(String uploadCompletedTime) {
        this.uploadCompletedTime = uploadCompletedTime;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getSubmissionIdGeneratedByDevice() {
        return submissionIdGeneratedByDevice;
    }

    public void setSubmissionIdGeneratedByDevice(String submissionIdGeneratedByDevice) {
        this.submissionIdGeneratedByDevice = submissionIdGeneratedByDevice;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber1() {
        return formattedPhone(phoneNumber1);
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return formattedPhone(phoneNumber2);
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getResidentialAddressStreet() {
        return residentialAddressStreet;
    }

    public void setResidentialAddressStreet(String residentialAddressStreet) {
        this.residentialAddressStreet = residentialAddressStreet;
    }

    public String getResidentialAddressCity() {
        return residentialAddressCity;
    }

    public void setResidentialAddressCity(String residentialAddressCity) {
        this.residentialAddressCity = residentialAddressCity;
    }

    public String getResidentialAddressState() {
        return residentialAddressState;
    }

    public void setResidentialAddressState(String residentialAddressState) {
        this.residentialAddressState = residentialAddressState;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
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

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<DeviceMagicAgentInstitutionCategoryDetails> getInstitutionCategoryDetailsList() {
        return institutionCategoryDetailsList;
    }

    public void setInstitutionCategoryDetailsList(List<DeviceMagicAgentInstitutionCategoryDetails> institutionCategoryDetailsList) {
        this.institutionCategoryDetailsList = institutionCategoryDetailsList;
    }

    private String formattedPhone(String phoneNumber) {
        if (phoneNumber.startsWith("234")) {
            return phoneNumber.replace("234", "0");
        }

        if (phoneNumber.startsWith("23")) {
            return phoneNumber.replace("23", "0");
        }
        return "0" + phoneNumber;
    }
}
