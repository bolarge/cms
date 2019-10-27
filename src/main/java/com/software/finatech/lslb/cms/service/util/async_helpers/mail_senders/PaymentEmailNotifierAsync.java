package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.StringCapitalizer;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class PaymentEmailNotifierAsync extends AbstractMailSender {
    private static final Logger logger = LoggerFactory.getLogger(PaymentEmailNotifierAsync.class);

    @Async
    public void sendOfflinePaymentNotificationForPaymentRecordDetail(PaymentRecordDetail paymentRecordDetail, PaymentRecord paymentRecord) {
        if (paymentRecord.isAgentPayment() || paymentRecord.isGamingTerminalPayment()) {
            Agent agent = paymentRecord.getAgent();
            logger.info("WHERE MAIL IS TO BE SENT TO AGENT XXXXXXXXXXXXXXXXXX " + agent.getEmailAddress());
            sendPaymentNotificationToUser(paymentRecordDetail, paymentRecord, agent.getEmailAddress(), "payment-notifications/OfflinePaymentNotificationExternalUser");
        }
        if (paymentRecord.isInstitutionPayment() || paymentRecord.isGamingMachinePayment()) {
            ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(paymentRecord.getInstitutionId());
            for (AuthInfo institutionAdmin : institutionAdmins) {
                sendPaymentNotificationToUser(paymentRecordDetail, paymentRecord, institutionAdmin.getEmailAddress(), "payment-notifications/OfflinePaymentNotificationExternalUser");
            }
        }
        //isSuccessFullPayment() will be UNPAID(05) PaymentStatusIS for OfflinePayment after initiation
        /*if (paymentRecordDetail.isSuccessfulPayment()) {
            sendPaymentNotificationToLSLBUsers(paymentRecordDetail, paymentRecord);
        } else {
            sendFailedPaymentToVGGAdminAndUsers(paymentRecordDetail, paymentRecord);
        }*/
    }

    @Async
    public void sendPaymentNotificationForPaymentRecordDetail(PaymentRecordDetail paymentRecordDetail, PaymentRecord paymentRecord) {
        if (paymentRecord.isAgentPayment() || paymentRecord.isGamingTerminalPayment()) {
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
        } else if(paymentRecordDetail.isFailedPayment()){
            sendFailedPaymentToVGGAdminAndUsers(paymentRecordDetail, paymentRecord);
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
        logger.info(" What is invoice date " + templateName);
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
            if (paymentRecord.isAgentPayment() || paymentRecord.isGamingTerminalPayment()) {
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
            //String paymentDate = paymentRecordDetail.getPaymentDate().toString("dd-MM-yyyy");
            String paymentDate = paymentRecordDetail.getCreatedAt().toString("dd-MM-yyyy");
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

    private void sendFailedPaymentToVGGAdminAndUsers(PaymentRecordDetail paymentRecordDetail, PaymentRecord paymentRecord) {
        HashMap<String, Object> model = new HashMap<>();
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        boolean hasTransactionReference = !StringUtils.isEmpty(paymentRecordDetail.getVigiPayTransactionReference());
        boolean hasInvoiceNumber = !StringUtils.isEmpty(paymentRecordDetail.getInvoiceNumber());
        String paymentInitiator = "";
        if (paymentRecord.isInstitutionPayment() || paymentRecord.isGamingMachinePayment()) {
            Institution institution = paymentRecord.getInstitution();
            if (institution != null) {
                paymentInitiator = institution.getInstitutionName();
            }
        }
        if (paymentRecord.isAgentPayment() || paymentRecord.isGamingTerminalPayment()) {
            Agent agent = paymentRecord.getAgent();
            if (agent != null) {
                paymentInitiator = agent.getFullName();
            }
        }

        model.put("amount", String.valueOf(paymentRecordDetail.getAmount()));
        model.put("date", presentDateString);
        model.put("hasTransactionReference", hasTransactionReference);
        model.put("hasInvoiceNumber", hasInvoiceNumber);
        model.put("transactionReference", paymentRecordDetail.getVigiPayTransactionReference());
        model.put("invoiceNumber", paymentRecordDetail.getInvoiceNumber());
        model.put("channel", paymentRecordDetail.getModeOfPaymentName());
        model.put("id", paymentRecordDetail.getId());
        model.put("paymentStatus", paymentRecordDetail.getPaymentStatusName());
        model.put("paymentInitiator", paymentInitiator);
        String content = mailContentBuilderService.build(model, "payment-notifications/vgg-payment-failed-notification");

        ArrayList<AuthInfo> vggAdminsAndUsers = authInfoService.findAllActiveVGGAdminAndUsers();
        if (vggAdminsAndUsers.isEmpty()) {
            logger.info("There are no active vgg admins or users, skipping failed notification email");
        }
        for (AuthInfo vggAdmin : vggAdminsAndUsers) {
            String mailSubject = "LSLB Failed Payment Notification";
            String email = vggAdmin.getEmailAddress();
            logger.info("Sending failed payment notification to {}", email);
            emailService.sendEmail(content, mailSubject, email);
        }
    }


    @Async
    public void handlePostPaymentInitiationEvents(PaymentRecord paymentRecord, PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        /**
         *  If payment is license renewal payment and it is first payment
         *  (set RenewalPayment initiated for license object
         */
        if (paymentRecord.isLicenseRenewalPayment() && paymentRecordDetailCreateDto.isFirstPayment()) {
            Query query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(paymentRecord.getInstitutionId()));
            query.addCriteria(Criteria.where("gameTypeId").is(paymentRecord.getGameTypeId()));
            query.addCriteria(Criteria.where("licenseTypeId").is(paymentRecord.getLicenseTypeId()));
            query.addCriteria(Criteria.where("renewalStatus").is("true"));
            Sort sort = new Sort(Sort.Direction.DESC, "expiryDate");
            query.with(sort);
            License license = (License) mongoRepositoryReactive.find(query, License.class).block();
            if (license != null) {
                license.setRenewalPaymentInitiated(true);
                license.setRenewalPaymentRecordId(paymentRecord.getId());
                mongoRepositoryReactive.saveOrUpdate(license);
            }
        }
    }

    @Async
    public void sendInitialVigiPayPaymentNotificationToInitiator(PaymentRecordDetail paymentRecordDetail, PaymentRecord paymentRecord) {
        try {
            String paymentInitiator = "";
            Institution institution = null;
            Agent agent = null;
            boolean sendToOperator = false;
            boolean sendToAgent = false;
            if (paymentRecord.isInstitutionPayment() || paymentRecord.isGamingMachinePayment()) {
                institution = paymentRecord.getInstitution();
                if (institution != null) {
                    paymentInitiator = institution.getInstitutionName();
                    sendToOperator = true;
                }
            }
            if (paymentRecord.isAgentPayment() || paymentRecord.isGamingTerminalPayment()) {
                agent = paymentRecord.getAgent();
                if (agent != null) {
                    paymentInitiator = agent.getFullName();
                    sendToAgent = true;
                }
            }

            HashMap<String, Object> model = new HashMap<>();
            String presentDateString = DateTime.now().toString("dd-MM-yyyy");
            String feePaymentTypeName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecord.getFeePaymentTypeName());
            String amount = NumberFormat.getInstance().format(paymentRecordDetail.getAmount());
            String modeOfPaymentName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecordDetail.getModeOfPaymentName());
            String revenueName = StringCapitalizer.convertToTitleCaseIteratingChars(String.valueOf(paymentRecord.getLicenseType()));
            String gameTypeName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecord.getGameTypeName());
            String paymentDate = paymentRecordDetail.getCreatedAt().toString("dd-MM-yyyy");
            boolean isPartPayment = paymentRecord.getAmount() > paymentRecordDetail.getAmount();
            boolean isCompletePayment = paymentRecord.isCompletedPayment();

            model.put("amount", amount);
            model.put("invoiceNumber", paymentRecordDetail.getInvoiceNumber());
            model.put("date", presentDateString);
            model.put("paymentDate", paymentDate);
            model.put("modeOfPayment", modeOfPaymentName);
            model.put("feePaymentType", feePaymentTypeName);
            model.put("revenueName", revenueName);
            model.put("gameType", gameTypeName);
            model.put("paymentInitiator", paymentInitiator);
            model.put("isPartPayment", isPartPayment);
            model.put("isCompletePayment", isCompletePayment);

            String mailContent = mailContentBuilderService.build(model, "payment-notifications/NewVigiPayInBranchPaymentNotificationExternalUser");
            String mailSubject = "LSLB Payment Invoice";
            if (sendToOperator) {
                ArrayList<AuthInfo> operatorAdmins = authInfoService.findAllEnabledUsersForInstitution(paymentRecord.getInstitutionId());
                for (AuthInfo authInfo : operatorAdmins) {
                    emailService.sendEmail(mailContent, mailSubject, authInfo.getEmailAddress());
                }
            }

            if (sendToAgent) {
                emailService.sendEmail(mailContent, mailSubject, agent.getEmailAddress());
            }
        } catch (Exception e) {
            logger.error("An error occurred while sending payment invoice notification email ", e);
        }
    }

    public void sendIrregularPaymentStatusToVGGAdminAndUsers(PaymentRecordDetail paymentRecordDetail, PaymentRecord paymentRecord, String vigipayPaymentStatus) {
        HashMap<String, Object> model = new HashMap<>();
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        String paymentInitiator = "";
        if (paymentRecord.isInstitutionPayment() || paymentRecord.isGamingMachinePayment()) {
            Institution institution = paymentRecord.getInstitution();
            if (institution != null) {
                paymentInitiator = institution.getInstitutionName();
            }
        }
        if (paymentRecord.isAgentPayment() || paymentRecord.isGamingTerminalPayment()) {
            Agent agent = paymentRecord.getAgent();
            if (agent != null) {
                paymentInitiator = agent.getFullName();
            }
        }
        String feePaymentTypeName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecord.getFeePaymentTypeName());
        String modeOfPaymentName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecordDetail.getModeOfPaymentName());
        String revenueName = StringCapitalizer.convertToTitleCaseIteratingChars(String.valueOf(paymentRecord.getLicenseType()));
        String gameTypeName = StringCapitalizer.convertToTitleCaseIteratingChars(paymentRecord.getGameTypeName());
        boolean isPartPayment = paymentRecord.getAmount() > paymentRecordDetail.getAmount();

        model.put("amount", NumberFormat.getInstance().format(paymentRecordDetail.getAmount()));
        model.put("date", presentDateString);
        model.put("invoiceNumber", paymentRecordDetail.getInvoiceNumber());
        model.put("channel", paymentRecordDetail.getModeOfPaymentName());
        model.put("id", paymentRecordDetail.getId());
        model.put("paymentStatus", paymentRecordDetail.getPaymentStatusName());
        model.put("paymentInitiator", paymentInitiator);
        model.put("modeOfPayment", modeOfPaymentName);
        model.put("feePaymentType", feePaymentTypeName);
        model.put("revenueName", revenueName);
        model.put("gameType", gameTypeName);
        model.put("isPartPayment", isPartPayment);
        model.put("vigipayPaymentStatus", vigipayPaymentStatus);

        String content = mailContentBuilderService.build(model, "payment-notifications/vgg-irregular-payment-notification");

        ArrayList<AuthInfo> vggAdminsAndUsers = authInfoService.findAllActiveVGGAdminAndUsers();
        if (vggAdminsAndUsers.isEmpty()) {
            logger.info("There are no active vgg admins or users, skipping failed notification email");
            return;
        }
        for (AuthInfo vggAdmin : vggAdminsAndUsers) {
            String mailSubject = "LSLB Irregular Payment Status Notification";
            String email = vggAdmin.getEmailAddress();
            logger.info("Sending payment status notification to {}", email);
            emailService.sendEmail(content, mailSubject, email);
        }
    }
}