package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.ApplicationForm;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ApplicationFormEmailSenderAsync extends AbstractMailSender{

    private static final Logger logger = LoggerFactory.getLogger(ApplicationFormEmailSenderAsync.class);

    @Async
    public void sendAdminCommentNotificationToInstitutionAdmins(ApplicationForm applicationForm, String comment) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(applicationForm.getInstitutionId());
        String mailSubject = String.format("Notification on your application for %s licence", applicationForm.getGameTypeName());
        String emailContent = buildApplicationCommentFromLSLBAdminEmailContent(applicationForm);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendCommentNotificationToInstitutionUser(institutionAdmin.getEmailAddress(), mailSubject, emailContent);
        }
    }

    private void sendCommentNotificationToInstitutionUser(String institutionAdminEmail, String mailSubject, String emailContent) {
        try {
            emailService.sendEmail(emailContent, mailSubject, institutionAdminEmail);
        } catch (Exception e) {
            logger.error(String.format("An error occurred while sending email to %s", institutionAdminEmail), e);
        }
    }


    private String buildApplicationCommentFromLSLBAdminEmailContent(ApplicationForm applicationForm) {
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        String gameTypeName = applicationForm.getGameTypeName();
        HashMap<String, Object> model = new HashMap<>();
        model.put("comment", applicationForm.getLslbAdminComment());
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        return mailContentBuilderService.build(model, "ApplicationFormPendingUploadGAadmin");
    }

    @Async
    public void sendRejectionMailToInstitutionAdmins(ApplicationForm applicationForm) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(applicationForm.getInstitutionId());
        String emailContent = buildRejectionEmailContent(applicationForm);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendRejectionMailToInstitutionUser(institutionAdmin.getEmailAddress(), applicationForm, emailContent);
        }
    }

    private void sendRejectionMailToInstitutionUser(String institutionAdminEmail, ApplicationForm applicationForm, String emailContent) {
        try {
            String mailSubject = String.format("Notification on your application for %s licence", applicationForm.getGameTypeName());
            emailService.sendEmail(emailContent, mailSubject, emailContent);
        } catch (Exception e) {
            logger.error("An error occurred while sending rejection mail to -> {}", institutionAdminEmail, e);
        }
    }

    @Async
    public void sendApprovedMailToInstitutionAdmins(ApplicationForm applicationForm) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(applicationForm.getInstitutionId());
        String mailSubject = String.format("Notification on your application for %s licence", applicationForm.getGameTypeName());
        String emailContent = buildApprovalEmailContent(applicationForm);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendApprovedMailToInstitutionUser(institutionAdmin.getEmailAddress(), mailSubject, emailContent);
        }
    }

    private void sendApprovedMailToInstitutionUser(String institutionAdminEmail, String mailSubject, String emailContent) {
        try {
            emailService.sendEmail(emailContent, mailSubject, institutionAdminEmail);
        } catch (Exception e) {
            logger.error("An error occurred while sending approval email to institution admin with email ->  {}", institutionAdminEmail, e);
        }
    }


    @Async
    public void sendApplicationFormSubmissionMailToLSLBAdmins(ApplicationForm applicationForm) {
        List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatCanReceiveApplicationSubmissionNotification();
        if (lslbAdmins.isEmpty()) {
            logger.info("No LSLB Admin staff found, Skipping email sending");
            return;
        }
        String mailSubject = "New Application submitted on LSLB Customer Management System";
        String emailContent = buildApplicationFormSubmissionEmailContent(applicationForm);
        for (AuthInfo lslbAdmin : lslbAdmins) {
            String adminEmail = lslbAdmin.getEmailAddress();
            logger.info("Sending email to LSLB admin with email -> {}", adminEmail);
            sendApplicationFormSubmissionNotificationToLSLSBAdmin(adminEmail, mailSubject, emailContent);
        }
        logger.info("Finished sending emails to LSLB Admins");
    }


    private void sendApplicationFormSubmissionNotificationToLSLSBAdmin(String adminEmail, String mailSubject, String emailContent) {
        try {
            emailService.sendEmail(emailContent, mailSubject, adminEmail);
        } catch (Exception e) {
            logger.error("An error occurred while sending application submission email to -> {}", adminEmail, e);
        }
    }

    private String buildApplicationFormSubmissionEmailContent(ApplicationForm applicationForm) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), applicationForm.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", applicationForm.getGameTypeName());
        model.put("applicantName", applicationForm.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        return mailContentBuilderService.build(model, "ApplicationFormSubmissionLSLB");
    }

    private String buildRejectionEmailContent(ApplicationForm applicationForm) {
        String gameTypeName = applicationForm.getGameTypeName();
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        LocalDate submissionDate = applicationForm.getSubmissionDate();
        String submissionDateString = "";
        if (submissionDate != null) {
            submissionDateString = submissionDate.toString("dd-MM-yyyy");
        }
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        model.put("institutionName", applicationForm.getInstitutionName());
        model.put("submissionDate", submissionDateString);
        model.put("rejectionReason", applicationForm.getReasonForRejection());
        return mailContentBuilderService.build(model, "application-form-rejection-GA-new");
    }

    private String buildApprovalEmailContent(ApplicationForm applicationForm) {
        String gameTypeName = applicationForm.getGameTypeName();
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        String callBackUrl = String.format("%s/payment-page", frontEndPropertyHelper.getFrontEndUrl());
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        model.put("frontEndUrl", callBackUrl);
        return mailContentBuilderService.build(model, "application-form-approval-GA-new");
    }
}
