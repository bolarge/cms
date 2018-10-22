package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class ScheduledMeetingCreateDto {
    @NotEmpty(message = "Please provide creator Id")
    private String creatorId;
    @NotEmpty(message = "Please provide institution Id")
    private String institutionId;
    private String meetingTitle;
    private String additionalNotes;
    private String venue;
    @NotEmpty(message = "Please provide meeting date")
    private String meetingDate;
    @NotEmpty(message = "Please provide application form id")
    private String applicationFormId;
    private Set<String> recipients = new HashSet<>();

    public Set<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<String> recipients) {
        this.recipients = recipients;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getApplicationFormId() {
        return applicationFormId;
    }

    public void setApplicationFormId(String applicationFormId) {
        this.applicationFormId = applicationFormId;
    }
}
