package com.software.finatech.lslb.cms.service.domain;


import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "Agents")
public class Agent extends AbstractFact {

    protected String passportId;
    protected String institutionId;
    protected String firstName;
    protected String lastName;
    protected String fullName;
    protected String emailAddress;
    protected String phoneNumber;
    protected String gameTypeId;

    @Override
    public String getFactName() {
        return "Agent";
    }
}
