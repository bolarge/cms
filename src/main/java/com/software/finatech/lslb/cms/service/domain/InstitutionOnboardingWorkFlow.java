package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.InstitutionOnboardingWorkFlowDto;
import com.software.finatech.lslb.cms.service.referencedata.ScheduledMeetingPurposeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ScheduledMeetingStatusReferenceData;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@SuppressWarnings("serial")
@Document(collection = "InstitutionOnboardinWorkFlows")
public class InstitutionOnboardingWorkFlow extends AbstractFact {
    private String institutionId;
    private boolean createdInstitution;
    private boolean paidApplicationFees;
    private boolean createdApplicationForm;
    private boolean submittedApplicationForm;
    private boolean hasScheduledPresentation;
    private boolean hasApprovedApplicationForm;

    public boolean isHasApprovedApplicationForm() {
        return hasApprovedApplicationForm;
    }

    public void setHasApprovedApplicationForm(boolean hasApprovedApplicationForm) {
        this.hasApprovedApplicationForm = hasApprovedApplicationForm;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public boolean isCreatedInstitution() {
        return createdInstitution;
    }

    public void setCreatedInstitution(boolean createdInstitution) {
        this.createdInstitution = createdInstitution;
    }

    public boolean isPaidApplicationFees() {
        return paidApplicationFees;
    }

    public void setPaidApplicationFees(boolean paidApplicationFees) {
        this.paidApplicationFees = paidApplicationFees;
    }

    public boolean isCreatedApplicationForm() {
        return createdApplicationForm;
    }

    public void setCreatedApplicationForm(boolean createdApplicationForm) {
        this.createdApplicationForm = createdApplicationForm;
    }

    public boolean isSubmittedApplicationForm() {
        return submittedApplicationForm;
    }

    public void setSubmittedApplicationForm(boolean submittedApplicationForm) {
        this.submittedApplicationForm = submittedApplicationForm;
    }

    public boolean isHasScheduledPresentation() {
        return hasScheduledPresentation;
    }

    public void setHasScheduledPresentation(boolean hasScheduledPresentation) {
        this.hasScheduledPresentation = hasScheduledPresentation;
    }

    public InstitutionOnboardingWorkFlowDto convertToDto() {
        InstitutionOnboardingWorkFlowDto dto = new InstitutionOnboardingWorkFlowDto();
        dto.setInstitutionId(getInstitutionId());
        dto.setCreatedApplicationForm(isCreatedApplicationForm());
        dto.setCreatedInstitution(isCreatedInstitution());
        dto.setPaidApplicationFees(isPaidApplicationFees());
        dto.setSubmittedApplicationForm(isSubmittedApplicationForm());
        dto.setHasApprovedApplicationForm(isHasApprovedApplicationForm());
        dto.setHasScheduledPresentation(isHasScheduledPresentation());
        if (isHasScheduledPresentation()) {
            ScheduledMeeting scheduledMeeting = getPendingScheduledMeetingForApplicant();
            if (scheduledMeeting != null) {
                dto.setMeetingStatus(scheduledMeeting.getMeetingStatusString());
                dto.setMeetingVenue(scheduledMeeting.getVenue());
                LocalDateTime meetingDateTime = scheduledMeeting.getMeetingDate();
                if (meetingDateTime != null) {
                    dto.setMeetingDate(meetingDateTime.toString("dd-MM-yyyy"));
                    dto.setMeetingTime(meetingDateTime.toString("HH:mm a"));
                }
            }
        }
        return dto;
    }

    private Institution getInstitution() {
        if (StringUtils.isEmpty(this.institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(this.institutionId, Institution.class).block();
    }

    private ScheduledMeeting getPendingScheduledMeetingForApplicant() {
        if (this.hasScheduledPresentation) {
            Query query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(this.institutionId));
            query.addCriteria(Criteria.where("meetingPurposeId").is(ScheduledMeetingPurposeReferenceData.APPLICANT_ID));
            query.addCriteria(Criteria.where("scheduledMeetingStatusId").is(ScheduledMeetingStatusReferenceData.PENDING_STATUS_ID));
            query.addCriteria(Criteria.where("meetingDate").lte(LocalDate.now()));
            return (ScheduledMeeting) mongoRepositoryReactive.find(query, ScheduledMeeting.class).block();
        }
        return null;
    }

    @Override
    public String getFactName() {
        return "InstitutionOnboardingWorkflow";
    }
}
