package com.software.finatech.lslb.cms.service.model.outletInformation;

public class ApplicantOutletInformation {
    private String businessName;
    private int numberOfOutlets;

    public int getNumberOfOutlets() {
        return numberOfOutlets;
    }

    public void setNumberOfOutlets(int numberOfOutlets) {
        this.numberOfOutlets = numberOfOutlets;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
