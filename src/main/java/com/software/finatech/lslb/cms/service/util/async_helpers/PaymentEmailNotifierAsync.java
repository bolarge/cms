package com.software.finatech.lslb.cms.service.util.async_helpers;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.RevenueNameReferenceData;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.StringCapitalizer;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            sendPaymentNotificationToUser(paymentRecordDetail, paymentRecord, agent.getEmailAddress(), "PaymentNotificationExternalUser");
        }
        if (StringUtils.equals(RevenueNameReferenceData.INSTITUTION_REVENUE_ID, revenueNameId) || StringUtils.equals(RevenueNameReferenceData.GAMING_MACHINE_ID, revenueNameId)) {
            ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(paymentRecord.getInstitutionId());
            for (AuthInfo institutionAdmin : institutionAdmins) {
                sendPaymentNotificationToUser(paymentRecordDetail, paymentRecord, institutionAdmin.getEmailAddress(), "PaymentNotificationExternalUser");
            }
        }

        if (paymentRecordDetail.isSuccessfulPayment()) {
            sendPaymentNotificationToLSLBFianceAdmins(paymentRecordDetail, paymentRecord);
        }

    }


    private void sendPaymentNotificationToLSLBFianceAdmins(PaymentRecordDetail paymentRecordDetail, PaymentRecord paymentRecord) {
        ArrayList<AuthInfo> lslbFinaceAdmins = authInfoService.getAllActiveLSLBFinanceAdmins();
        if (lslbFinaceAdmins == null || lslbFinaceAdmins.isEmpty()) {
            logger.info("No LSLB finance admin found, skipping email");
            return;
        }
        for (AuthInfo fianceAdmin : lslbFinaceAdmins) {
            sendPaymentNotificationToUser(paymentRecordDetail, paymentRecord, fianceAdmin.getEmailAddress(), "PaymentNotificationLSLBFinance");
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
            String revenueName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecord.getRevenueNameName());
            String gameTypeName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecord.getGameTypeName());
            String paymentDate = paymentRecordDetail.getPaymentDate().toString("dd-MM-yyyy");
            boolean isPartPayment = paymentRecord.getAmount() > paymentRecordDetail.getAmount();

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

            String content = mailContentBuilderService.build(model, templateName);
            emailService.sendEmail(content, "LSLB Payment Notification", userEmail);
        } catch (Exception e) {
            logger.error(String.format("An error occurred while sending payment notification email to user -> %s", userEmail), e);
        }
    }
}