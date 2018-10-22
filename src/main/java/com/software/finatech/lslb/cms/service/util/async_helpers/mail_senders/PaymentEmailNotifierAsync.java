package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.StringCapitalizer;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class PaymentEmailNotifierAsync extends AbstractMailSender {
    private static final Logger logger = LoggerFactory.getLogger(PaymentEmailNotifierAsync.class);

    @Async
    public void sendPaymentNotificationForPaymentRecordDetail(PaymentRecordDetail paymentRecordDetail, PaymentRecord paymentRecord) {
        if (paymentRecord.isAgentPayment()) {
            Agent agent = paymentRecord.getAgent();
            sendPaymentNotificationToUser(paymentRecordDetail, paymentRecord, agent.getEmailAddress(), "payment-notifications/PaymentNotificationExternalUser");
        }
        if (paymentRecord.isInstitutionPayment() || paymentRecord.isGamingMachinePayment()) {
            ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(paymentRecord.getInstitutionId());
            for (AuthInfo institutionAdmin : institutionAdmins) {
                sendPaymentNotificationToUser(paymentRecordDetail, paymentRecord, institutionAdmin.getEmailAddress(), "payment-notifications/PaymentNotificationExternalUser");
            }
        }
        if (paymentRecordDetail.isSuccessfulPayment()) {
            sendPaymentNotificationToLSLBUsers(paymentRecordDetail, paymentRecord);
        }
    }


    private void sendPaymentNotificationToLSLBUsers(PaymentRecordDetail paymentRecordDetail, PaymentRecord paymentRecord) {
        ArrayList<AuthInfo> lslbMembersForPaymentNotification = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_PAYMENT_NOTIFICATION_ID);
        if (lslbMembersForPaymentNotification == null || lslbMembersForPaymentNotification.isEmpty()) {
            logger.info("No LSLB finance admin found, skipping email");
            return;
        }
        for (AuthInfo lslbMember : lslbMembersForPaymentNotification) {
            sendPaymentNotificationToUser(paymentRecordDetail, paymentRecord, lslbMember.getEmailAddress(), "payment-notifications/PaymentNotificationLSLBFinance");
        }
    }

    private void sendPaymentNotificationToUser(PaymentRecordDetail paymentRecordDetail, PaymentRecord paymentRecord, String userEmail, String templateName) {
        try {
            boolean isSuccessPayment = false;
            if (StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecordDetail.getPaymentStatusId())) {
                isSuccessPayment = true;
            }

            if (StringUtils.equals(PaymentStatusReferenceData.FAILED_PAYMENT_STATUS_ID, paymentRecordDetail.getPaymentStatusId())) {
                isSuccessPayment = false;
            }

            String paymentInitiator = "";
            if (paymentRecord.isInstitutionPayment() || paymentRecord.isGamingMachinePayment()) {
                Institution institution = paymentRecord.getInstitution();
                if (institution != null) {
                    paymentInitiator = institution.getInstitutionName();
                }
            }
            if (paymentRecord.isAgentPayment()) {
                Agent agent = paymentRecord.getAgent();
                if (agent != null) {
                    paymentInitiator = agent.getFullName();
                }
            }

            HashMap<String, Object> model = new HashMap<>();
            String presentDateString = DateTime.now().toString("dd-MM-yyyy");
            String feePaymentTypeName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecord.getFeePaymentTypeName());
            String amount = NumberFormat.getInstance().format(paymentRecordDetail.getAmount());
            String modeOfPaymentName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecordDetail.getModeOfPaymentName());
            String revenueName = StringCapitalizer.convertToTitleCaseIteratingChars(String.valueOf(paymentRecord.getLicenseType()));
            String gameTypeName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecord.getGameTypeName());
            String paymentDate = paymentRecordDetail.getPaymentDate().toString("dd-MM-yyyy");
            boolean isPartPayment = paymentRecord.getAmount() > paymentRecordDetail.getAmount();
            boolean isCompletePayment = paymentRecord.isCompletedPayment();

            model.put("amount", amount);
            model.put("date", presentDateString);
            model.put("paymentDate", paymentDate);
            model.put("modeOfPayment", modeOfPaymentName);
            model.put("feePaymentType", feePaymentTypeName);
            model.put("revenueName", revenueName);
            model.put("gameType", gameTypeName);
            model.put("isSuccessPayment", isSuccessPayment);
            model.put("paymentInitiator", paymentInitiator);
            model.put("isPartPayment", isPartPayment);
            model.put("isCompletePayment", isCompletePayment);

            String content = mailContentBuilderService.build(model, templateName);
            emailService.sendEmail(content, "LSLB Payment Notification", userEmail);
        } catch (Exception e) {
            logger.error(String.format("An error occurred while sending payment notification email to user -> %s", userEmail), e);
        }
    }
}