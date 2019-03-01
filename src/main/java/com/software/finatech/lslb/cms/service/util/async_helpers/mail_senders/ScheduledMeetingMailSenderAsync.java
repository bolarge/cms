package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class ScheduledMeetingMailSenderAsync extends AbstractMailSender {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledMeetingMailSenderAsync.class);
    @Autowired
    private InstitutionService institutionService;

    private String buildMeetingCreatorMailContent(ScheduledMeeting scheduledMeeting, String templateName) {
        Institution institution = institutionService.findByInstitutionId(scheduledMeeting.getInstitutionId());
        String meetingUrl = String.format("%s/schedule-presentation-view/%s", frontEndPropertyHelper.getFrontEndUrl(), scheduledMeeting.getId());
        String institutionName = institution.getInstitutionName();
        HashMap<String, Object> model = new HashMap<>();
        String meetingDateString = scheduledMeeting.getMeetingDateString();
        String meetingTimeString = scheduledMeeting.getMeetingTimeString();
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        model.put("institutionName", institutionName);
        model.put("meetingDate", meetingDateString);
        model.put("meetingTime", meetingTimeString);
        model.put("meetingTitle", scheduledMeeting.getMeetingSubject());
        model.put("venue", scheduledMeeting.getVenue());
        model.put("additionalNotes", scheduledMeeting.getMeetingDescription());
        model.put("date", presentDateString);
        model.put("frontEndUrl", meetingUrl);
        model.put("newRecipientList", scheduledMeeting.getRecipientNames());
        return mailContentBuilderService.build(model, templateName);
    }

    private String buildMeetingRecipientMailContent(ScheduledMeeting scheduledMeeting, String templateName) {
        AuthInfo inviter = authInfoService.getUserById(scheduledMeeting.getCreatorId());
        Institution institution = institutionService.findByInstitutionId(scheduledMeeting.getInstitutionId());
        String meetingUrl = String.format("%s/schedule-presentation-view/%s", frontEndPropertyHelper.getFrontEndUrl(), scheduledMeeting.getId());
        String institutionName = institution.getInstitutionName();
        HashMap<String, Object> model = new HashMap<>();
        String meetingDateString = scheduledMeeting.getMeetingDateString();
        String meetingTimeString = scheduledMeeting.getMeetingTimeString();
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        model.put("inviterName", inviter.getFullName());
        model.put("institutionName", institutionName);
        model.put("meetingDate", meetingDateString);
        model.put("meetingTime", meetingTimeString);
        model.put("meetingTitle", scheduledMeeting.getMeetingSubject());
        model.put("venue", scheduledMeeting.getVenue());
        model.put("additionalNotes", scheduledMeeting.getMeetingDescription());
        model.put("date", presentDateString);
        model.put("frontEndUrl", meetingUrl);
        return mailContentBuilderService.build(model, templateName);
    }

    private String buildOperatorMeetingMailContent(ScheduledMeeting scheduledMeeting, String templateName) {
        Institution institution = institutionService.findByInstitutionId(scheduledMeeting.getInstitutionId());
        String meetingUrl = String.format("%s/schedule-presentation-view/%s", frontEndPropertyHelper.getFrontEndUrl(), scheduledMeeting.getId());
        String institutionName = institution.getInstitutionName();
        String transferorName = "";
        if (scheduledMeeting.isForLicenseTransferee() || scheduledMeeting.isForLicenseTransferror()) {
            LicenseTransfer licenseTransfer = scheduledMeeting.getLicenseTransfer();
            Institution transferror = licenseTransfer.getFromInstitution();
            if (transferror != null) {
                transferorName = transferror.getInstitutionName();
            }
        }
        HashMap<String, Object> model = new HashMap<>();
        String meetingDateString = scheduledMeeting.getMeetingDateString();
        String meetingTimeString = scheduledMeeting.getMeetingTimeString();
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        String gameTypeName = "";
        if (scheduledMeeting.isForLicenseApplicant()) {
            ApplicationForm applicationForm = scheduledMeeting.getApplicationForm();
            if (applicationForm != null) {
                gameTypeName = applicationForm.getGameTypeName();
            }
        }
        if (scheduledMeeting.isForLicenseTransferror() || scheduledMeeting.isForLicenseTransferee()) {
            LicenseTransfer licenseTransfer = scheduledMeeting.getLicenseTransfer();
            if (licenseTransfer != null) {
                gameTypeName = licenseTransfer.getGameTypeName();
            }
        }
        model.put("institutionName", institutionName);
        model.put("meetingDate", meetingDateString);
        model.put("meetingTime", meetingTimeString);
        model.put("meetingTitle", scheduledMeeting.getMeetingSubject());
        model.put("venue", scheduledMeeting.getVenue());
        model.put("additionalNotes", scheduledMeeting.getMeetingDescription());
        model.put("date", presentDateString);
        model.put("transferor", transferorName);
        model.put("frontEndUrl", meetingUrl);
        model.put("gameType", gameTypeName);
        return mailContentBuilderService.build(model, templateName);
    }

    @Async
    public void sendEmailToMeetingCreator(String templateName, String mailSubject, ScheduledMeeting meeting) {
        try {
            AuthInfo initiator = meeting.getCreator();
            String emailContent = buildMeetingCreatorMailContent(meeting, templateName);
            logger.info("Sending meeting email to {}", initiator.getEmailAddress());
            emailService.sendEmail(emailContent, mailSubject, initiator.getEmailAddress());
        } catch (Exception e) {
            logger.info("An error occurred while sending email to meeting creator", e);
        }
    }

    @Async
    public void sendEmailToMeetingInvitedOperators(String templateName, String mailSubject, ScheduledMeeting scheduledMeeting) {
        try {
            ArrayList<AuthInfo> operatorAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(scheduledMeeting.getInstitutionId());
            String mailContent = buildOperatorMeetingMailContent(scheduledMeeting, templateName);
            for (AuthInfo authInfo : operatorAdmins) {
                logger.info("Sending meeting email to {}", authInfo.getEmailAddress());
                emailService.sendEmail(mailContent, mailSubject, authInfo.getEmailAddress());
            }
        } catch (Exception e) {
            logger.error("An error occurred while sending email to institution admin", e);
        }
    }

    @Async
    public void sendEmailToMeetingRecipients(String templateName, String mailSubject, ScheduledMeeting meeting, ArrayList<AuthInfo> invitees) {
        try {
            String mailContent = buildMeetingRecipientMailContent(meeting, templateName);
            for (AuthInfo invitee : invitees) {
                if (!StringUtils.equals(invitee.getId(), meeting.getCreatorId())) {
                    logger.info("Sending meeting email to {}", invitee.getEmailAddress());
                    emailService.sendEmail(mailContent, mailSubject, invitee.getEmailAddress());
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred when sending email to invitee", e);
        }
    }
}