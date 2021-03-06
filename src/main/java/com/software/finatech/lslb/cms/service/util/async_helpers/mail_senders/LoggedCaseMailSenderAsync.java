package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;


import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.CasePenaltyParams;
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
        String frontEndUrl = String.format("%s/update-case/%s", frontEndPropertyHelper.getFrontEndUrl(), loggedCase.getId());
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
    public void sendPenaltyMailToOffender(LoggedCase loggedCase, CasePenaltyParams casePenaltyParams) {
        String mailContent = buildPenaltyMailContent(loggedCase, casePenaltyParams);
        String subject = "Penalty Notification";
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

    @Async
    public void sendRelicenseMailToLicense(License license, LicenseStatus oldStatus) {
        String content = buildRelicensedMailContent(license, oldStatus);
        String mailSubject = String.format("Notification on your %s licence", license.getGameType());
        if (license.isGamingTerminalLicense() || license.isAgentLicense()) {
            Agent agent = license.getAgent();
            emailService.sendEmail(content, mailSubject, agent.getEmailAddress());
        }

        if (license.isGamingMachineLicense() || license.isInstitutionLicense()) {
            ArrayList<AuthInfo> operatorAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(license.getInstitutionId());
            for (AuthInfo operatorAdmin : operatorAdmins) {
                String email = operatorAdmin.getEmailAddress();
                try {
                    emailService.sendEmail(content, mailSubject, email);
                } catch (Exception e) {
                    logger.error("Error occurred while sending mail to {}", email, e);
                }
            }
        }
    }

    private String buildPenaltyMailContent(LoggedCase loggedCase, CasePenaltyParams casePenaltyParams) {
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("gameType", String.valueOf(loggedCase.getGameType()));
        model.put("clause", casePenaltyParams.getClause());
        model.put("relevantSection", casePenaltyParams.getRelevantSection());
        model.put("amount", String.format("NGN%s", casePenaltyParams.getAmount()));
        return mailContentBuilderService.build(model, "logged-cases/CaseLicensePenalty");
    }

//    private String buildOutcomeMailContent(LoggedCase loggedCase) {
//        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
//        String outcome = String.valueOf(loggedCase.getLoggedCaseOutcome(loggedCase.getLoggedCaseOutcomeId()));
//        outcome = outcome.replace("LICENCE", "");
//        HashMap<String, Object> model = new HashMap<>();
//        model.put("date", presentDateString);
//        model.put("gameType", String.valueOf(loggedCase.getGameType()));
//        model.put("outcome", outcome);
//        return mailContentBuilderService.build(model, "logged-cases/CaseLicenseOutcome");
//    }

    private String buildRelicensedMailContent(License license, LicenseStatus oldStatus) {
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("gameType", String.valueOf(license.getGameType()));
        model.put("licenseNumber", license.getLicenseNumber());
        model.put("oldStatus", String.valueOf(oldStatus));
        model.put("newStatus", String.valueOf(license.getLicenseStatus()));
        return mailContentBuilderService.build(model, "logged-cases/Relicense");
    }

    @Async
    public void sendOutcomeNotificationToOffender(LoggedCase loggedCase) {
        String mailSubject = String.format("Notification on your %s licence", loggedCase.getGameType());
        String mailContent;
        if (loggedCase.isOutcomeLicenseSuspended()) {
            mailContent = buildLicenseOutcomeMailContent(loggedCase, "logged-cases/CaseLicenseOutcomeSuspension");
        } else if (loggedCase.isOutcomeLicenseTerminated()) {
            mailContent = buildLicenseOutcomeMailContent(loggedCase, "logged-cases/CaseLicenseOutcomeTermination");
        } else if (loggedCase.isOutcomePenalty()) {
            mailContent = buildLicenseOutcomeMailContent(loggedCase, "logged-cases/CaseLicensePenalty");
        } else {
            mailContent = buildLicenseOutcomeMailContent(loggedCase, "logged-cases/CaseLicenseOutcome");
        }

        if (loggedCase.isLoggedAgainstGamingTerminal() || loggedCase.isLoggedAgainstAgent()) {
            Agent agent = loggedCase.getAgent();
            emailService.sendEmail(mailContent, mailSubject, agent.getEmailAddress());
        }
        if (loggedCase.isLoggedAgainstGamingMachine() || loggedCase.isLoggedAgainstInstitution()) {
            ArrayList<AuthInfo> operatorAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(loggedCase.getInstitutionId());
            for (AuthInfo operatorAdmin : operatorAdmins) {
                String email = operatorAdmin.getEmailAddress();
                try {
                    emailService.sendEmail(mailContent, mailSubject, email);
                } catch (Exception e) {
                    logger.error("Error occurred while sending mail to {}", email, e);
                }
            }
        }
    }

    private String buildLicenseOutcomeMailContent(LoggedCase loggedCase, String templateName) {
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        String outcomeString = String.valueOf(loggedCase.getLoggedCaseOutcome(loggedCase.getLoggedCaseOutcomeId())).replaceAll("LICENCE", "");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("gameType", String.valueOf(loggedCase.getGameType()));
        model.put("outcome", outcomeString);
        return mailContentBuilderService.build(model, templateName);
    }
}
