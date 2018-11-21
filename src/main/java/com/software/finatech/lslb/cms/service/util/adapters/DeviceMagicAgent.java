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
    private String imageurl;
    private String imageFileName;


    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagebase64() {
        return this.imageBase64;
    }

    public void setImagebase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }


    public String getImageurl() {
        return this.imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


    public String getSubmittingdevice() {
        return this.submittingDevice;
    }

    public void setSubmittingdevice(String submittingDevice) {
        this.submittingDevice = submittingDevice;
    }

    public String getDeviceuser() {
        return this.deviceUser;
    }

    public void setDeviceuser(String deviceUser) {
        this.deviceUser = deviceUser;
    }

    public String getFormcompleteddate() {
        return this.formCompletedDate;
    }

    public void setFormcompleteddate(String formCompletedDate) {
        this.formCompletedDate = formCompletedDate;
    }

    public String getUploadcompletedtime() {
        return this.uploadCompletedTime;
    }

    public void setUploadcompletedtime(String uploadCompletedTime) {
        this.uploadCompletedTime = uploadCompletedTime;
    }

    public String getSubmissionid() {
        return this.submissionId;
    }

    public void setSubmissionid(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getSubmissionidgeneratedbydevice() {
        return this.submissionIdGeneratedByDevice;
    }

    public void setSubmissionidgeneratedbydevice(String submissionIdGeneratedByDevice) {
        this.submissionIdGeneratedByDevice = submissionIdGeneratedByDevice;
    }

    public String getOperatorid() {
        return this.operatorId;
    }

    public void setOperatorid(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getGamingcategopry() {
        return this.gamingCategopry;
    }

    public void setGamingcategopry(String gamingCategopry) {
        this.gamingCategopry = gamingCategopry;
    }

    public String getFirstname() {
        return this.firstName;
    }

    public void setFirstname(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return this.lastName;
    }

    public void setLastname(String lastName) {
        this.lastName = lastName;
    }

    public String getDateofbirth() {
        return this.dateOfBirth;
    }

    public void setDateofbirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhonenumber1() {
        return this.phoneNumber1;
    }

    public void setPhonenumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhonenumber2() {
        return this.phoneNumber2;
    }

    public void setPhonenumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getResidentialaddressstreet() {
        return this.residentialAddressStreet;
    }

    public void setResidentialaddressstreet(String residentialAddressStreet) {
        this.residentialAddressStreet = residentialAddressStreet;
    }

    public String getResindetialaddresscity() {
        return this.resindetialAddressCity;
    }

    public void setResindetialaddresscity(String resindetialAddressCity) {
        this.resindetialAddressCity = resindetialAddressCity;
    }

    public String getResidentialaddressstate() {
        return this.residentialAddressState;
    }

    public void setResidentialaddressstate(String residentialAddressState) {
        this.residentialAddressState = residentialAddressState;
    }

    public String getBusinessaddressstreet1() {
        return this.businessAddressStreet1;
    }

    public void setBusinessaddressstreet1(String businessAddressStreet1) {
        this.businessAddressStreet1 = businessAddressStreet1;
    }

    public String getBusinessaddresscity1() {
        return this.businessAddressCity1;
    }

    public void setBusinessaddresscity1(String businessAddressCity1) {
        this.businessAddressCity1 = businessAddressCity1;
    }

    public String getBusinessaddressstate1() {
        return this.businessAddressState1;
    }

    public void setBusinessaddressstate1(String businessAddressState1) {
        this.businessAddressState1 = businessAddressState1;
    }

    public String getBusinessaddressstreet2() {
        return this.businessAddressStreet2;
    }

    public void setBusinessaddressstreet2(String businessAddressStreet2) {
        this.businessAddressStreet2 = businessAddressStreet2;
    }

    public String getBusinessaddresscity2() {
        return this.businessAddressCity2;
    }

    public void setBusinessaddresscity2(String businessAddressCity2) {
        this.businessAddressCity2 = businessAddressCity2;
    }

    public String getBusinessaddressstate2() {
        return this.businessAddressState2;
    }

    public void setBusinessaddressstate2(String businessAddressState2) {
        this.businessAddressState2 = businessAddressState2;
    }

    public String getBusinessaddressstreet3() {
        return this.businessAddressStreet3;
    }

    public void setBusinessaddressstreet3(String businessAddressStreet3) {
        this.businessAddressStreet3 = businessAddressStreet3;
    }

    public String getBusinessaddresscity3() {
        return this.businessAddressCity3;
    }

    public void setBusinessaddresscity3(String businessAddressCity3) {
        this.businessAddressCity3 = businessAddressCity3;
    }

    public String getBusinessaddressstate3() {
        return this.businessAddressState3;
    }

    public void setBusinessaddressstate3(String businessAddressState3) {
        this.businessAddressState3 = businessAddressState3;
    }

    public String getBusinessaddressstreet4() {
        return this.businessAddressStreet4;
    }

    public void setBusinessaddressstreet4(String businessAddressStreet4) {
        this.businessAddressStreet4 = businessAddressStreet4;
    }

    public String getBusinessaddresscity4() {
        return this.businessAddressCity4;
    }

    public void setBusinessaddresscity4(String businessAddressCity4) {
        this.businessAddressCity4 = businessAddressCity4;
    }

    public String getBusinessaddressstate4() {
        return this.businessAddressState4;
    }

    public void setBusinessaddressstate4(String businessAddressState4) {
        this.businessAddressState4 = businessAddressState4;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBvn() {
        return this.bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getMeansofid() {
        return this.meansOfId;
    }

    public void setMeansofid(String meansOfId) {
        this.meansOfId = meansOfId;
    }

    public String getIdnumber() {
        return this.idNumber;
    }

    public void setIdnumber(String idNumber) {
        this.idNumber = idNumber;
    }

    private Agent toAgent(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        Agent agent = new Agent();
        agent.setId(UUID.randomUUID().toString());
        agent.setFullName(String.format("%s %s", this.firstName, this.lastName));
        agent.setFirstName(this.firstName);
        agent.setLastName(this.lastName);
        agent.setEmailAddress(this.email);
        agent.setPhoneNumber(this.phoneNumber1);
        agent.getPhoneNumbers().add(this.phoneNumber1);
        agent.getPhoneNumbers().add(this.phoneNumber2);
        agent.setMeansOfId(this.meansOfId);
        agent.setIdNumber(this.idNumber);
        String address = buildAddress(this.residentialAddressStreet, this.resindetialAddressCity, this.residentialAddressState);
        if (!StringUtils.isEmpty(address)) {
            agent.setResidentialAddress(address);
        }
        address = buildAddress(this.businessAddressStreet1, this.businessAddressCity1, this.businessAddressState1);
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(this.businessAddressStreet2, this.businessAddressCity2, this.businessAddressState2);
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(this.businessAddressStreet3, this.businessAddressCity3, this.businessAddressState3);
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(this.businessAddressStreet4, this.businessAddressCity4, this.businessAddressState4);
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        Institution institution = findInstitutionByName(this.operatorId, mongoRepositoryReactive);
        if (institution != null) {
            agent.getInstitutionIds().add(institution.getId());
            AgentInstitution agentInstitution = new AgentInstitution();
            //  agentInstitution.setGameTypeId();
        }
        return agent;
    }

    private String buildAddress(String streetAddress, String city, String state) {
        if (!StringUtils.isEmpty(streetAddress) && !StringUtils.isEmpty(city)
                && !StringUtils.isEmpty(state)) {
            return String.format("%s, %s, %s", streetAddress.replace(".", ""), city, state);
        }
        return null;
    }

    private Institution findInstitutionByName(String institutionName, MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        if (StringUtils.isEmpty(institutionName)) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionName").regex(institutionName, "i"));
        return (Institution) mongoRepositoryReactive.find(query,
                Institution.class).block();
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
                ", imageurl='" + imageurl + '\'' +
                ", imageFileName='" + imageFileName + '\'' +
                '}';
    }
}
