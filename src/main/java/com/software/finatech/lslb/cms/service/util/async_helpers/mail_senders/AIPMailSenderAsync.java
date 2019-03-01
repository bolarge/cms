package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.lowagie.text.DocumentException;
import com.software.finatech.lslb.cms.service.domain.AIPDocumentApproval;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class AIPMailSenderAsync extends AbstractMailSender {

    private static Logger logger = LoggerFactory.getLogger(AIPMailSenderAsync.class);

    @Async
    public void sendAipNotificationToInstitutionAdmins(PaymentRecord paymentRecord) {
        List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(paymentRecord.getInstitutionId());
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
        return mailContentBuilderService.build(model, "aip-license-notification");
    }

    private File buildAIPAttachment(String content) {
        try {
            File file = File.createTempFile("AIP Licence Rules", ".pdf");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(content);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream, false);
            FileUtils.writeByteArrayToFile(file, byteArrayOutputStream.toByteArray());
            return file;
        } catch (DocumentException e) {
            logger.error("Document Exception occurred while building AIP attachment", e);
            return null;
        } catch (IOException e) {
            logger.error("IO Exception occurred while building AIP attachment", e);
            return null;
        }
    }


    public void sendFinalAIPApprovalMailTOFianlApprovers(AIPDocumentApproval aipDocumentApproval) {
        ArrayList<AuthInfo> finalApprovers = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.APPROVE_APPLICATION_FORM_ID);
        if (finalApprovers.isEmpty()) {
            return;
        }
        String content = buildFinalAIPSubmissionApproverMailContent(aipDocumentApproval);
        for (AuthInfo finalApprover : finalApprovers) {
            String email = finalApprover.getEmailAddress();
            try {
                emailService.sendEmail(content, "Licence Issuance Notification", email);
            } catch (Exception e) {
                logger.error("An error occurred while sending mail to {}", email, e);
            }
        }
    }

    private String buildFinalAIPSubmissionApproverMailContent(AIPDocumentApproval aipDocumentApproval) {
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        String gameTypeName = aipDocumentApproval.getGameTypeName();
        String institutionName = aipDocumentApproval.getInstitutionName();
        String frontEndUrl = String.format("%s/aip-document-download/%s", frontEndPropertyHelper.getFrontEndUrl(), aipDocumentApproval.getId());
        HashMap<String, Object> model = new HashMap<>();
        model.put("gameType", gameTypeName);
        model.put("date", presentDateString);
        model.put("institutionName", institutionName);
        return mailContentBuilderService.build(model, "aip-form/AIPFormDocumentSubmissionLSLBFinalApprovalNotification");
    }
}
