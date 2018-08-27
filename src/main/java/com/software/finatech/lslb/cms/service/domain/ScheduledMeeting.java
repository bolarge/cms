package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "ScheduledMeetings")
public class ScheduledMeeting extends AbstractFact {

    private String creatorId;
    private DateTime meetingDate;
    private String additionalNotes;
    private String meetingTitle;
    private String institutionId;
    private String scheduledMeetingStatusId;
    private String venue;
    private String cancelerId;

    public String getCancelerId() {
        return cancelerId;
    }

    public void setCancelerId(String cancelerId) {
        this.cancelerId = cancelerId;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getScheduledMeetingStatusId() {
        return scheduledMeetingStatusId;
    }

    public void setScheduledMeetingStatusId(String scheduledMeetingStatusId) {
        this.scheduledMeetingStatusId = scheduledMeetingStatusId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public DateTime getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(DateTime meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public Institution getInstitution() {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    private String getCreatorFullName() {
        AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(creatorId, AuthInfo.class).block();
        if (authInfo == null) {
            return null;
        } else {
            return authInfo.getFullName();
        }
    }

    private ScheduledMeetingStatus getMeetingStatus() {
        Map scheduledMeetingStatusMap = Mapstore.STORE.get("ScheduledMeetingStatus");
        ScheduledMeetingStatus scheduledMeetingStatus = null;
        if (scheduledMeetingStatusMap != null) {
            scheduledMeetingStatus = (ScheduledMeetingStatus) scheduledMeetingStatusMap.get(scheduledMeetingStatusId);
        }
        if (scheduledMeetingStatus == null) {
            scheduledMeetingStatus = (ScheduledMeetingStatus) mongoRepositoryReactive.findById(scheduledMeetingStatusId, ScheduledMeetingStatus.class).block();
            if (scheduledMeetingStatus != null && scheduledMeetingStatusMap != null) {
                scheduledMeetingStatusMap.put(scheduledMeetingStatusId, scheduledMeetingStatus);
            }
        }
        return scheduledMeetingStatus;
    }

    public ScheduledMeetingDto convertToDto() {
        ScheduledMeetingDto scheduledMeetingDto = new ScheduledMeetingDto();
        scheduledMeetingDto.setId(getId());
        scheduledMeetingDto.setMeetingTitle(getMeetingTitle());
        scheduledMeetingDto.setAdditionalNotes(getAdditionalNotes());
        scheduledMeetingDto.setMeetingDate(getMeetingDate().toString("dd/MM/yyyy HH:mm:ss"));
        ScheduledMeetingStatus scheduledMeetingStatus = getMeetingStatus();
        if (scheduledMeetingStatus != null) {
            scheduledMeetingDto.setMeetingStatus(scheduledMeetingStatus.convertToDto());
        }
        Institution institution = getInstitution();
        if (institution != null) {
            scheduledMeetingDto.setInstitutionName(institution.getInstitutionName());
        }
        scheduledMeetingDto.setCreatorFullName(getCreatorFullName());
        scheduledMeetingDto.setVenue(getVenue());
        return scheduledMeetingDto;
    }

    @Override
    public String getFactName() {
        return "ScheduledMeetings";
    }
}
