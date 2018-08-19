package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.StatusDto;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "Status")
public class Status extends EnumeratedFact {
    protected String nextStep;

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public StatusDto convertToDto(){
        StatusDto statusDto = new StatusDto();
        statusDto.setName(getName());
        statusDto.setId(getId());
        statusDto.setDescription(getDescription());
        statusDto.setNextStep(getNextStep());
        return statusDto;
    }

    @Override
    public String getFactName() {
        return "Status";
    }
}
