package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;


import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.LoggedCase;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.FrontEndPropertyHelper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
        return mailContentBuilderService.build(model, "NewCaseLSLBAdmin");
    }
}
