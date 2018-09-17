package com.software.finatech.lslb.cms.service.util.async_helpers;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.referencedata.RevenueNameReferenceData;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
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
public class PaymentEmailNotifierAsync {
    private static final Logger logger = LoggerFactory.getLogger(PaymentEmailNotifierAsync.class);
    private AuthInfoService authInfoService;
    private MailContentBuilderService mailContentBuilderService;
    private EmailService emailService;

    @Autowired
    public PaymentEmailNotifierAsync(AuthInfoService authInfoService,
                                     MailContentBuilderService mailContentBuilderService,
                                     EmailService emailService) {
        this.authInfoService = authInfoService;
        this.mailContentBuilderService = mailContentBuilderService;
        this.emailService = emailService;
    }

    @Async
    public void sendPaymentNotificationForPaymentRecordDetail(PaymentRecordDetail paymentRecordDetail, PaymentRecord paymentRecord) {
        String revenueNameId = paymentRecord.getRevenueNameId();
        if (StringUtils.equals(RevenueNameReferenceData.AGENT_REVENUE_ID, revenueNameId)) {
            Agent agent = paymentRecord.getAgent();
            sendPaymentNotificationToAgent(agent, paymentRecordDetail);
        }
        if (StringUtils.equals(RevenueNameReferenceData.INSTITUTION_REVENUE_ID, revenueNameId)) {
            sendPaymentNotificationsToInstitution(paymentRecord.getInstitutionId(), paymentRecordDetail);
        }
        if (StringUtils.equals(RevenueNameReferenceData.GAMING_MACHINE_ID, revenueNameId)) {
            sendPaymentNotificationsToInstitution(paymentRecord.getInstitutionId(), paymentRecordDetail);
        }
    }


    private void sendPaymentNotificationToAgent(Agent agent, PaymentRecordDetail paymentRecordDetail) {
        if (paymentRecordDetail.isSuccessfulPayment()) {
            sendPaymentNotificationToUser(paymentRecordDetail, agent.getEmailAddress(), "payment-success-notification");
        }
        if (paymentRecordDetail.isFailedPayment()) {
            sendPaymentNotificationToUser(paymentRecordDetail, agent.getEmailAddress(), "payment-failed-notification");
        }
    }

    private void sendPaymentNotificationsToInstitution(String institutionId, PaymentRecordDetail paymentRecordDetail) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(institutionId);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            if (paymentRecordDetail.isSuccessfulPayment()) {
                sendPaymentNotificationToUser(paymentRecordDetail, institutionAdmin.getEmailAddress(), "payment-success-notification");
            }
            if (paymentRecordDetail.isFailedPayment()) {
                sendPaymentNotificationToUser(paymentRecordDetail, institutionAdmin.getEmailAddress(), "payment-failed-notification");
            }
        }
    }

    private void sendPaymentNotificationToUser(PaymentRecordDetail paymentRecordDetail, String userEmail, String templateName) {
        try {
            HashMap<String, Object> model = new HashMap<>();
            String presentDateString = DateTime.now().toString("dd/MM/yyyy");
            ModeOfPayment modeOfPayment = paymentRecordDetail.getModeOfPayment();
            String modeOfPaymentName = "";
            if (modeOfPayment != null) {
                modeOfPaymentName = modeOfPayment.getName();
            }
            String amount = String.valueOf(paymentRecordDetail.getAmount());
            model.put("amount", amount);
            model.put("date", presentDateString);
            model.put("channel", modeOfPaymentName);

            String content = mailContentBuilderService.build(model, templateName);
            emailService.sendEmail(content, "LSLB Payment Notification", userEmail);
        } catch (Exception e) {
            logger.error(String.format("An error occurred while sending payment notification email to user -> %s", userEmail), e);
        }
    }
}