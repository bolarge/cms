package com.software.finatech.lslb.cms.service.dto;

import org.apache.commons.lang3.StringUtils;

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
    @NotEmpty(message = "Please provide entity id")
    private String entityId;
    private Set<String> recipients = new HashSet<>();
    @NotEmpty(message = "please provide meeting purpose id")
    private String meetingPurposeId;

    public String getMeetingPurposeId() {
        return meetingPurposeId;
    }

    public void setMeetingPurposeId(String meetingPurposeId) {
        this.meetingPurposeId = meetingPurposeId;
    }

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


    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
