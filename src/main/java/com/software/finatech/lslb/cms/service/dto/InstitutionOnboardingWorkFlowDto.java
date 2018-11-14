package com.software.finatech.lslb.cms.service.dto;

public class InstitutionOnboardingWorkFlowDto {
    private String institutionId;
    private Boolean createdInstitution;
    private Boolean paidApplicationFees;
    private Boolean createdApplicationForm;
    private Boolean submittedApplicationForm;
    private Boolean hasScheduledPresentation;
    private Boolean hasApprovedApplicationForm;
    private String meetingDate;
    private String meetingTime;
    private String meetingVenue;
    private String meetingStatus;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public Boolean getCreatedInstitution() {
        return createdInstitution;
    }

    public void setCreatedInstitution(Boolean createdInstitution) {
        this.createdInstitution = createdInstitution;
    }

    public Boolean getPaidApplicationFees() {
        return paidApplicationFees;
    }

    public void setPaidApplicationFees(Boolean paidApplicationFees) {
        this.paidApplicationFees = paidApplicationFees;
    }

    public Boolean getCreatedApplicationForm() {
        return createdApplicationForm;
    }

    public void setCreatedApplicationForm(Boolean createdApplicationForm) {
        this.createdApplicationForm = createdApplicationForm;
    }

    public Boolean getSubmittedApplicationForm() {
        return submittedApplicationForm;
    }

    public void setSubmittedApplicationForm(Boolean submittedApplicationForm) {
        this.submittedApplicationForm = submittedApplicationForm;
    }

    public Boolean getHasScheduledPresentation() {
        return hasScheduledPresentation;
    }

    public void setHasScheduledPresentation(Boolean hasScheduledPresentation) {
        this.hasScheduledPresentation = hasScheduledPresentation;
    }

    public Boolean getHasApprovedApplicationForm() {
        return hasApprovedApplicationForm;
    }

    public void setHasApprovedApplicationForm(Boolean hasApprovedApplicationForm) {
        this.hasApprovedApplicationForm = hasApprovedApplicationForm;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getMeetingVenue() {
        return meetingVenue;
    }

    public void setMeetingVenue(String meetingVenue) {
        this.meetingVenue = meetingVenue;
    }

    public String getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(String meetingStatus) {
        this.meetingStatus = meetingStatus;
    }
}
