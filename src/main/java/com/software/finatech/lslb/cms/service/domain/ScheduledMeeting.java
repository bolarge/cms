package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.AuthInfoDto;
import com.software.finatech.lslb.cms.service.dto.CommentDetail;
import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingDto;
import com.software.finatech.lslb.cms.service.referencedata.ScheduledMeetingPurposeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ScheduledMeetingStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@SuppressWarnings("serial")
@Document(collection = "ScheduledMeetings")
public class ScheduledMeeting extends AbstractFact {

    private String creatorId;
    private LocalDateTime meetingDate;
    private String meetingDescription;
    private String meetingSubject;
    private String institutionId;
    private String scheduledMeetingStatusId;
    private String venue;
    private Set<String> recipientIds = new HashSet<>();
    private String cancelerId;
    private String entityId;
    private LocalDateTime meetingReminderDate;
    private LocalDateTime nextPostMeetingReminderDate;
    private int reminderNotificationCount = 0;
    private boolean reminderSent;
    private String meetingPurposeId;
    private List<CommentDetail> comments = new ArrayList<>();

    public String getMeetingPurposeId() {
        return meetingPurposeId;
    }

    public void setMeetingPurposeId(String meetingPurposeId) {
        this.meetingPurposeId = meetingPurposeId;
    }

    public Set<String> getRecipientIds() {
        return recipientIds;
    }

    public List<CommentDetail> getComments() {
        return comments;
    }

    public void setComments(List<CommentDetail> comments) {
        this.comments = comments;
    }

    public void setRecipientIds(Set<String> recipientIds) {
        this.recipientIds = recipientIds;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public LocalDateTime getMeetingReminderDate() {
        return meetingReminderDate;
    }

    public void setMeetingReminderDate(LocalDateTime meetingReminderDate) {
        this.meetingReminderDate = meetingReminderDate;
    }

    public int getReminderNotificationCount() {
        return reminderNotificationCount;
    }

    public void setReminderNotificationCount(int reminderNotificationCount) {
        this.reminderNotificationCount = reminderNotificationCount;
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

    public LocalDateTime getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(LocalDateTime meetingDate) {
        this.meetingDate = meetingDate;
    }

    public LocalDateTime getNextPostMeetingReminderDate() {
        return nextPostMeetingReminderDate;
    }

    public void setNextPostMeetingReminderDate(LocalDateTime nextPostMeetingReminderDate) {
        this.nextPostMeetingReminderDate = nextPostMeetingReminderDate;
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
        if (StringUtils.isEmpty(this.institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public String getInstitutionName() {
        Institution institution = getInstitution();
        if (institution != null) {
            return institution.getInstitutionName();
        }
        return null;
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

    public String getMeetingStatusString() {
        ScheduledMeetingStatus scheduledMeetingStatus = getMeetingStatus();
        if (scheduledMeetingStatus != null) {
            return scheduledMeetingStatus.getName();
        }
        return null;
    }

    public ScheduledMeetingPurpose getMeetingPurpose() {
        if (StringUtils.isEmpty(this.meetingPurposeId)) {
            return null;
        }
        Map scheduledMeetingPurposeMap = Mapstore.STORE.get("MeetingPurpose");
        ScheduledMeetingPurpose scheduledMeetingPurpose = null;
        if (scheduledMeetingPurposeMap != null) {
            scheduledMeetingPurpose = (ScheduledMeetingPurpose) scheduledMeetingPurposeMap.get(this.meetingPurposeId);
        }
        if (scheduledMeetingPurpose == null) {
            scheduledMeetingPurpose = (ScheduledMeetingPurpose) mongoRepositoryReactive.findById(this.meetingPurposeId, ScheduledMeetingStatus.class).block();
            if (scheduledMeetingPurpose != null && scheduledMeetingPurposeMap != null) {
                scheduledMeetingPurposeMap.put(this.meetingPurposeId, scheduledMeetingPurpose);
            }
        }
        return scheduledMeetingPurpose;
    }


    public ScheduledMeetingDto convertToDto() {
        ScheduledMeetingDto scheduledMeetingDto = new ScheduledMeetingDto();
        scheduledMeetingDto.setId(getId());
        scheduledMeetingDto.setMeetingTitle(getMeetingSubject());
        scheduledMeetingDto.setAdditionalNotes(getMeetingDescription());
        scheduledMeetingDto.setMeetingDate(getMeetingDateString());
        scheduledMeetingDto.setMeetingTime(getMeetingTimeString());
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
        ScheduledMeetingPurpose meetingPurpose = getMeetingPurpose();
        if (meetingPurpose != null) {
            scheduledMeetingDto.setMeetingPurposeId(this.meetingPurposeId);
            scheduledMeetingDto.setMeetingPurposeName(meetingPurpose.getName());
        }
        return scheduledMeetingDto;
    }

    public ScheduledMeetingDto convertToFullDto() {
        ScheduledMeetingDto dto = convertToDto();
        dto.setRecipients(getRecipientDto());
        List<CommentDetail> comments = getComments();
        Collections.reverse(comments);
        dto.setComment(comments);
        return dto;
    }

    public String getGameTypeName() {
        ApplicationForm applicationForm = getApplicationForm();
        if (applicationForm != null) {
            return applicationForm.getGameTypeName();
        }
        return null;
    }

    public ApplicationForm getApplicationForm() {
        if (StringUtils.isEmpty(this.entityId)) {
            return null;
        }
        return (ApplicationForm) mongoRepositoryReactive.findById(this.entityId, ApplicationForm.class).block();
    }

    public LicenseTransfer getLicenseTransfer() {
        if (StringUtils.isEmpty(this.entityId)) {
            return null;
        }
        return (LicenseTransfer) mongoRepositoryReactive.findById(this.entityId, LicenseTransfer.class).block();
    }


    public String getMeetingDateTimeString() {
        LocalDateTime meetingDateTime = getMeetingDate();
        if (meetingDateTime != null) {
            return meetingDateTime.toString("dd-MM-yyyy HH:mm:ss");
        }
        return null;
    }

    public String getMeetingDateString() {
        LocalDateTime meetingDateTime = getMeetingDate();
        if (meetingDateTime != null) {
            return meetingDateTime.toString("yyyy-MM-dd");
        }
        return null;
    }

    public String getMeetingTimeString() {
        LocalDateTime meetingDateTime = getMeetingDate();
        if (meetingDateTime != null) {
            return meetingDateTime.toString("hh:mm a");
        }
        return null;
    }

    public AuthInfo getCreator() {
        return getUser(this.creatorId);
    }

    public AuthInfo getUser(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
    }

    public ArrayList<AuthInfo> getRecipients() {
        ArrayList<AuthInfo> authInfos = new ArrayList<>();
        for (String authInfoId : this.recipientIds) {
            AuthInfo recipient = getUser(authInfoId);
            if (recipient != null) {
                authInfos.add(recipient);
            }
        }
        return authInfos;
    }

    public Set<AuthInfoDto> getRecipientDto() {
        Set<AuthInfoDto> dtos = new HashSet<>();
        for (String authInfoId : this.recipientIds) {
            AuthInfo recipient = getUser(authInfoId);
            if (recipient != null) {
                AuthInfoDto dto = new AuthInfoDto();
                dto.setId(recipient.getId());
                dto.setFullName(recipient.getFullName());
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public ArrayList<String> getRecipientNames() {
        ArrayList<String> recipientNames = new ArrayList<>();
        for (AuthInfo user : getRecipients()) {
            recipientNames.add(user.getFullName());
        }
        return recipientNames;
    }

    public boolean isForLicenseApplicant() {
        return StringUtils.equals(ScheduledMeetingPurposeReferenceData.APPLICANT_ID, this.meetingPurposeId);
    }

    public boolean isForLicenseTransferror() {
        return StringUtils.equals(ScheduledMeetingPurposeReferenceData.TRANSFEROR_ID, this.meetingPurposeId);
    }

    public boolean isForLicenseTransferee() {
        return StringUtils.equals(ScheduledMeetingPurposeReferenceData.TRANSFEREE_ID, this.meetingPurposeId);
    }

    public boolean isCompleted() {
        return StringUtils.equals(ScheduledMeetingStatusReferenceData.COMPLETED_STATUS_ID, this.scheduledMeetingStatusId);
    }

    public boolean isCanceled() {
        return StringUtils.equals(ScheduledMeetingStatusReferenceData.CANCELED_STATUS_ID, this.scheduledMeetingStatusId);
    }

    @Override
    public String getFactName() {
        return "ScheduledMeetings";
    }
}
