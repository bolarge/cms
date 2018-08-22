package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "ApplicationFormStatus")
public class ApplicationFormStatus extends EnumeratedFact {
    protected String nextStep;

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    @Override
    public String getFactName() {
        return "ApplicationFormStatus";
    }
}
