package com.software.finatech.lslb.cms.service.util.adapters;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AgentInstitution;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.UUID;

public class DeviceMagicAgent {
    private String title;
    private String submittingDevice;
    private String deviceUser;
    private String formCompletedDate;
    private String uploadCompletedTime;
    private String submissionId;
    private String submissionIdGeneratedByDevice;
    private String operatorId;
    private String gamingCategopry;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String phoneNumber1;
    private String phoneNumber2;
    private String residentialAddressStreet;
    private String resindetialAddressCity;
    private String residentialAddressState;
    private String businessAddressStreet1;
    private String businessAddressCity1;
    private String businessAddressState1;
    private String businessAddressStreet2;
    private String businessAddressCity2;
    private String businessAddressState2;
    private String businessAddressStreet3;
    private String businessAddressCity3;
    private String businessAddressState3;
    private String businessAddressStreet4;
    private String businessAddressCity4;
    private String businessAddressState4;
    private String email;
    private String bvn;
    private String meansOfId;
    private String idNumber;
    private String imageBase64;
    private String imageFileName;
    private String gender;


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

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getGamingCategopry() {
        return gamingCategopry;
    }

    public void setGamingCategopry(String gamingCategopry) {
        this.gamingCategopry = gamingCategopry;
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
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
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

    public String getResindetialAddressCity() {
        return resindetialAddressCity;
    }

    public void setResindetialAddressCity(String resindetialAddressCity) {
        this.resindetialAddressCity = resindetialAddressCity;
    }

    public String getResidentialAddressState() {
        return residentialAddressState;
    }

    public void setResidentialAddressState(String residentialAddressState) {
        this.residentialAddressState = residentialAddressState;
    }

    public String getBusinessAddressStreet1() {
        return businessAddressStreet1;
    }

    public void setBusinessAddressStreet1(String businessAddressStreet1) {
        this.businessAddressStreet1 = businessAddressStreet1;
    }

    public String getBusinessAddressCity1() {
        return businessAddressCity1;
    }

    public void setBusinessAddressCity1(String businessAddressCity1) {
        this.businessAddressCity1 = businessAddressCity1;
    }

    public String getBusinessAddressState1() {
        return businessAddressState1;
    }

    public void setBusinessAddressState1(String businessAddressState1) {
        this.businessAddressState1 = businessAddressState1;
    }

    public String getBusinessAddressStreet2() {
        return businessAddressStreet2;
    }

    public void setBusinessAddressStreet2(String businessAddressStreet2) {
        this.businessAddressStreet2 = businessAddressStreet2;
    }

    public String getBusinessAddressCity2() {
        return businessAddressCity2;
    }

    public void setBusinessAddressCity2(String businessAddressCity2) {
        this.businessAddressCity2 = businessAddressCity2;
    }

    public String getBusinessAddressState2() {
        return businessAddressState2;
    }

    public void setBusinessAddressState2(String businessAddressState2) {
        this.businessAddressState2 = businessAddressState2;
    }

    public String getBusinessAddressStreet3() {
        return businessAddressStreet3;
    }

    public void setBusinessAddressStreet3(String businessAddressStreet3) {
        this.businessAddressStreet3 = businessAddressStreet3;
    }

    public String getBusinessAddressCity3() {
        return businessAddressCity3;
    }

    public void setBusinessAddressCity3(String businessAddressCity3) {
        this.businessAddressCity3 = businessAddressCity3;
    }

    public String getBusinessAddressState3() {
        return businessAddressState3;
    }

    public void setBusinessAddressState3(String businessAddressState3) {
        this.businessAddressState3 = businessAddressState3;
    }

    public String getBusinessAddressStreet4() {
        return businessAddressStreet4;
    }

    public void setBusinessAddressStreet4(String businessAddressStreet4) {
        this.businessAddressStreet4 = businessAddressStreet4;
    }

    public String getBusinessAddressCity4() {
        return businessAddressCity4;
    }

    public void setBusinessAddressCity4(String businessAddressCity4) {
        this.businessAddressCity4 = businessAddressCity4;
    }

    public String getBusinessAddressState4() {
        return businessAddressState4;
    }

    public void setBusinessAddressState4(String businessAddressState4) {
        this.businessAddressState4 = businessAddressState4;
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

    @Override
    public String toString() {
        return "DeviceMagicAgent{" +
                "title='" + title + '\'' +
                ", submittingDevice='" + submittingDevice + '\'' +
                ", deviceUser='" + deviceUser + '\'' +
                ", formCompletedDate='" + formCompletedDate + '\'' +
                ", uploadCompletedTime='" + uploadCompletedTime + '\'' +
                ", submissionId='" + submissionId + '\'' +
                ", submissionIdGeneratedByDevice='" + submissionIdGeneratedByDevice + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", gamingCategopry='" + gamingCategopry + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", phoneNumber1='" + phoneNumber1 + '\'' +
                ", phoneNumber2='" + phoneNumber2 + '\'' +
                ", residentialAddressStreet='" + residentialAddressStreet + '\'' +
                ", resindetialAddressCity='" + resindetialAddressCity + '\'' +
                ", residentialAddressState='" + residentialAddressState + '\'' +
                ", businessAddressStreet1='" + businessAddressStreet1 + '\'' +
                ", businessAddressCity1='" + businessAddressCity1 + '\'' +
                ", businessAddressState1='" + businessAddressState1 + '\'' +
                ", businessAddressStreet2='" + businessAddressStreet2 + '\'' +
                ", businessAddressCity2='" + businessAddressCity2 + '\'' +
                ", businessAddressState2='" + businessAddressState2 + '\'' +
                ", businessAddressStreet3='" + businessAddressStreet3 + '\'' +
                ", businessAddressCity3='" + businessAddressCity3 + '\'' +
                ", businessAddressState3='" + businessAddressState3 + '\'' +
                ", businessAddressStreet4='" + businessAddressStreet4 + '\'' +
                ", businessAddressCity4='" + businessAddressCity4 + '\'' +
                ", businessAddressState4='" + businessAddressState4 + '\'' +
                ", email='" + email + '\'' +
                ", bvn='" + bvn + '\'' +
                ", meansOfId='" + meansOfId + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", imageBase64='" + imageBase64 + '\'' +
                ", imageFileName='" + imageFileName + '\'' +
                '}';
    }
}
