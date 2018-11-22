package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;


import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.LoggedCase;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class LoggedCaseMailSenderAsync extends AbstractMailSender {

    private static final Logger logger = LoggerFactory.getLogger(LoggedCaseMailSenderAsync.class);

    @Async
    public void sendNewCaseNotificationToLslbUsersThatCanReceive(LoggedCase loggedCase) {
        List<AuthInfo> lslbMembersThatCanReceiveNewCaseNotification = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_CASE_NOTIFICATION_ID);
        if (lslbMembersThatCanReceiveNewCaseNotification == null || lslbMembersThatCanReceiveNewCaseNotification.isEmpty()) {
            logger.info("No LSLB member can receive new case notifications, skipping notification");
            return;
        }
        String mailContent = buildNewCaseNotificationContent(loggedCase);
        for (AuthInfo lslbAmdin : lslbMembersThatCanReceiveNewCaseNotification) {
            sendNewCaseNotificationToLslbUser(lslbAmdin, mailContent);
        }
    }

    private void sendNewCaseNotificationToLslbUser(AuthInfo lslbAmdin, String mailContent) {
        String userEmail = lslbAmdin.getEmailAddress();
        try {
            logger.info("Sending new case notification email to lslb admin  with email address {}", userEmail);
            emailService.sendEmail(mailContent, "New LoggedCase on LSLB Customer Management System", userEmail);
        } catch (Exception e) {
            logger.error(String.format("An error occurred while sending new case notification email to %s", userEmail), e);
        }
    }

    private String buildNewCaseNotificationContent(LoggedCase loggedCase) {
        String frontEndUrl = String.format("%s/logged-cases/%s", frontEndPropertyHelper.getFrontEndUrl(), loggedCase.getId());
        String ticketId = loggedCase.getTicketId();
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");

        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("ticketId", ticketId);
        model.put("frontEndUrl", frontEndUrl);
        model.put("reportedName", loggedCase.getReportedEntityName());
        model.put("reporterName", loggedCase.getReporterName());
        return mailContentBuilderService.build(model, "logged-cases/NewCaseLSLBAdmin");
    }

    @Async
    public void sendPenaltyMailToOffender(LoggedCase loggedCase) {
        String mailContent = buildPenaltyMailContent(loggedCase);
        String subject = "Penalty Notification";
        sendEmailToOffender(mailContent, subject, loggedCase);
    }

    @Async
    public void sendOutcomeMailToOffender(LoggedCase loggedCase) {
        String mailContent = buildOutcomeMailContent(loggedCase);
        String subject = String.format("Notification on your %s licence", loggedCase.getGameType());
        sendEmailToOffender(mailContent, subject, loggedCase);
    }

    private void sendEmailToOffender(String mailContent, String subject, LoggedCase loggedCase) {
        if (loggedCase.isLoggedAgainstGamingTerminal() || loggedCase.isLoggedAgainstAgent()) {
            Agent agent = loggedCase.getAgent();
            emailService.sendEmail(mailContent, subject, agent.getEmailAddress());
        }
        if (loggedCase.isLoggedAgainstGamingMachine() || loggedCase.isLoggedAgainstInstitution()) {
            ArrayList<AuthInfo> operatorAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(loggedCase.getInstitutionId());
            for (AuthInfo operatorAdmin : operatorAdmins) {
                String email = operatorAdmin.getEmailAddress();
                try {
                    emailService.sendEmail(mailContent, subject, email);
                } catch (Exception e) {
                    logger.error("Error occurred while sending mail to {}", email, e);
                }
            }
        }
    }


    private String buildPenaltyMailContent(LoggedCase loggedCase) {
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("gameType", String.valueOf(loggedCase.getGameType()));
        model.put("outcome", String.valueOf(loggedCase.getLoggedCaseOutcome(loggedCase.getLoggedCaseOutcomeId())));
        return mailContentBuilderService.build(model, "logged-cases/CaseLicensePenalty");
    }

    private String buildOutcomeMailContent(LoggedCase loggedCase) {
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        String outcome = String.valueOf(loggedCase.getLoggedCaseOutcome(loggedCase.getLoggedCaseOutcomeId()));
        outcome = outcome.replace("LICENCE", "");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("gameType", String.valueOf(loggedCase.getGameType()));
        model.put("outcome", outcome);
        return mailContentBuilderService.build(model, "logged-cases/CaseLicenseOutcome");
    }
}
