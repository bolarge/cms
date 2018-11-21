package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.domain.AbstractFact;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Agents")
public class FakeAgent extends AbstractFact {
    private String dateOfBirth;
    private String firstName;

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getFactName() {
        return "Fake Agent";
    }
}
