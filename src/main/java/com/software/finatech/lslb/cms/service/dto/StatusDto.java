package com.software.finatech.lslb.cms.service.dto;

public class StatusDto extends EnumeratedFactDto {
    protected String nextStep;

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }
}
