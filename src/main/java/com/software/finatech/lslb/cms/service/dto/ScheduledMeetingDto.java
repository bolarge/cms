package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private String meetingTime;
    private Set<AuthInfoDto> recipients = new HashSet<>();
    private List<CommentDto> comment = new ArrayList<>();
    private String meetingPurposeName;
    private String meetingPurposeId;

    public String getMeetingPurposeName() {
        return meetingPurposeName;
    }

    public void setMeetingPurposeName(String meetingPurposeName) {
        this.meetingPurposeName = meetingPurposeName;
    }

    public String getMeetingPurposeId() {
        return meetingPurposeId;
    }

    public void setMeetingPurposeId(String meetingPurposeId) {
        this.meetingPurposeId = meetingPurposeId;
    }

    public List<CommentDto> getComment() {
        return comment;
    }

    public void setComment(List<CommentDto> comment) {
        this.comment = comment;
    }

    public Set<AuthInfoDto> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<AuthInfoDto> recipients) {
        this.recipients = recipients;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

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
