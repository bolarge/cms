package com.software.finatech.lslb.cms.service.model.criminalityDetails;

public class MemberCriminalityDetail {
    private String fullName;
    private String dateOfOffence;
    private String detailOfOffence;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDateOfOffence() {
        return dateOfOffence;
    }

    public void setDateOfOffence(String dateOfOffence) {
        this.dateOfOffence = dateOfOffence;
    }

    public String getDetailOfOffence() {
        return detailOfOffence;
    }

    public void setDetailOfOffence(String detailOfOffence) {
        this.detailOfOffence = detailOfOffence;
    }
}
