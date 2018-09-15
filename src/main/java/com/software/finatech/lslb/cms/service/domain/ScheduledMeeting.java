package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "ScheduledMeetings")
public class ScheduledMeeting extends AbstractFact {

    private String creatorId;
    private DateTime meetingDate;
    private String meetingDescription;
    private String meetingSubject;
    private String institutionId;
    private String scheduledMeetingStatusId;
    private String venue;
    private String cancelerId;
    private boolean expirationEmailSent;
    private String entityId;
    private boolean firstReminderMailSent;
    private boolean secondReminderMailSent;
    private String applicationFormId;

    public String getApplicationFormId() {
        return applicationFormId;
    }

    public void setApplicationFormId(String applicationFormId) {
        this.applicationFormId = applicationFormId;
    }

    public boolean isFirstReminderMailSent() {
        return firstReminderMailSent;
    }

    public void setFirstReminderMailSent(boolean firstReminderMailSent) {
        this.firstReminderMailSent = firstReminderMailSent;
    }

    public boolean isSecondReminderMailSent() {
        return secondReminderMailSent;
    }

    public void setSecondReminderMailSent(boolean secondReminderMailSent) {
        this.secondReminderMailSent = secondReminderMailSent;
    }

    public boolean isExpirationEmailSent() {
        return expirationEmailSent;
    }

    public void setExpirationEmailSent(boolean expirationEmailSent) {
        this.expirationEmailSent = expirationEmailSent;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

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

    public String getMeetingDescription() {
        return meetingDescription;
    }

    public void setMeetingDescription(String meetingDescription) {
        this.meetingDescription = meetingDescription;
    }

    public String getMeetingSubject() {
        return meetingSubject;
    }

    public void setMeetingSubject(String meetingSubject) {
        this.meetingSubject = meetingSubject;
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
        if (StringUtils.isEmpty(this.creatorId)) {
            return null;
        }
        AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(creatorId, AuthInfo.class).block();
        if (authInfo == null) {
            return null;
        } else {
            return authInfo.getFullName();
        }
    }

    private ScheduledMeetingStatus getMeetingStatus() {
        if (StringUtils.isEmpty(this.scheduledMeetingStatusId)) {
            return null;
        }
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
        scheduledMeetingDto.setMeetingTitle(getMeetingSubject());
        scheduledMeetingDto.setAdditionalNotes(getMeetingDescription());
        scheduledMeetingDto.setMeetingDate(getMeetingDate().toString("dd/MM/yyyy HH:mm:ss"));
        ScheduledMeetingStatus scheduledMeetingStatus = getMeetingStatus();
        if (scheduledMeetingStatus != null) {
            scheduledMeetingDto.setMeetingStatusId(getScheduledMeetingStatusId());
            scheduledMeetingDto.setMeetingStatusName(scheduledMeetingStatus.getName());
        }
        Institution institution = getInstitution();
        if (institution != null) {
            scheduledMeetingDto.setInstitutionName(institution.getInstitutionName());
            scheduledMeetingDto.setInstitutionId(getInstitutionId());
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
