package com.software.finatech.lslb.cms.service.dto;

public class ScheduledMeetingDto {
    private String creatorFullName;
    private String venue;
    private String meetingTitle;
    private String additionalNotes;
    private String meetingDate;
    private String id;
    private String institutionName;
    private String institutionId;
    private String meetingStatusName;
    private String meetingStatusId;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getMeetingStatusName() {
        return meetingStatusName;
    }

    public void setMeetingStatusName(String meetingStatusName) {
        this.meetingStatusName = meetingStatusName;
    }

    public String getMeetingStatusId() {
        return meetingStatusId;
    }

    public void setMeetingStatusId(String meetingStatusId) {
        this.meetingStatusId = meetingStatusId;
    }

    public String getCreatorFullName() {
        return creatorFullName;
    }

    public void setCreatorFullName(String creatorFullName) {
        this.creatorFullName = creatorFullName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
}
