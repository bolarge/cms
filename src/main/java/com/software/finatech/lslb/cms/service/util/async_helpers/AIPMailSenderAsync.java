package com.software.finatech.lslb.cms.service.util.async_helpers;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;


@Component
public class AIPMailSenderAsync {

    private static Logger logger = LoggerFactory.getLogger(AIPMailSenderAsync.class);

    private AuthInfoService authInfoService;
    private MailContentBuilderService mailContentBuilderService;
    private EmailService emailService;

    @Autowired
    public AIPMailSenderAsync(AuthInfoService authInfoService,
                              MailContentBuilderService mailContentBuilderService,
                              EmailService emailService) {
        this.authInfoService = authInfoService;
        this.mailContentBuilderService = mailContentBuilderService;
        this.emailService = emailService;
    }

    @Async
    public void sendAipNotificationToInstitutionAdmins(PaymentRecord paymentRecord) {
        List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(paymentRecord.getInstitutionId());
        if (institutionAdmins == null || institutionAdmins.isEmpty()) {
            logger.info("There are no institution admins for {}, skipping AIP notification mail", paymentRecord.getInstitutionName());
            return;
        }

        String gameTypeName = paymentRecord.getGameTypeName();
        String institutionName = paymentRecord.getInstitutionName();
        String emailContent = makeAIPNotificationEmailContent(institutionName, gameTypeName);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            String institutionAdminEmail = institutionAdmin.getEmailAddress();
            sendAipNotificationToInstitutionAdmin(institutionAdminEmail, emailContent);
        }
    }

    private void sendAipNotificationToInstitutionAdmin(String institutionAdminEmail, String content) {
        try {
            emailService.sendEmail(content, "LSLB AIP NOTIFICATION", institutionAdminEmail);
        } catch (Exception e) {
            logger.error("An error occurred while sending AIP notification to user");
        }
    }

    private String makeAIPNotificationEmailContent(String institutionName, String gameTypeName) {
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        HashMap<String, Object> model = new HashMap<>();
        model.put("gameType", gameTypeName);
        model.put("date", presentDateString);
        model.put("institutionName", institutionName);
        String content = mailContentBuilderService.build(model, "aip-license-notification");
        return content;
    }

}
