package com.software.finatech.lslb.cms.service.util.async_helpers;

import com.software.finatech.lslb.cms.service.domain.ApplicationForm;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.domain.LslbAdminComment;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.FrontEndPropertyHelper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApplicationFormNotificationHelperAsync {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationFormNotificationHelperAsync.class);

    private AuthInfoService authInfoService;
    private MailContentBuilderService mailContentBuilderService;
    private EmailService emailService;
    private FrontEndPropertyHelper frontEndPropertyHelper;


    @Autowired
    public ApplicationFormNotificationHelperAsync(AuthInfoService authInfoService,
                                                  MailContentBuilderService mailContentBuilderService,
                                                  EmailService emailService,
                                                  FrontEndPropertyHelper frontEndPropertyHelper) {
        this.authInfoService = authInfoService;
        this.mailContentBuilderService = mailContentBuilderService;
        this.emailService = emailService;
        this.frontEndPropertyHelper = frontEndPropertyHelper;
    }

    @Async
    public void sendCompleteApplicationNotificationToLslbAdmin(ApplicationForm applicationForm) {
        LslbAdminComment lslbAdminComment = applicationForm.getLslbAdminComment();
        if (lslbAdminComment == null) {
            return;
        }
        sendCompletionNotificationToLslbAdmin(applicationForm, lslbAdminComment.getUserId());
    }

    private void sendCompletionNotificationToLslbAdmin(ApplicationForm applicationForm, String lslbAdminId) {
        Institution institution = applicationForm.getInstitution();
        AuthInfo lslbAdmin = authInfoService.getUserById(lslbAdminId);
        String presentDate = DateTime.now().toString("dd-MM-yyyy");
        String gameTypeName = applicationForm.getGameTypeName();
        String institutionName = institution.getInstitutionName();


        HashMap<String, Object> model = new HashMap<>();
        model.put("name", lslbAdmin.getFullName());
        model.put("institutionName", institutionName);
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);

        String content = mailContentBuilderService.build(model, "ApplicationFormCompleteUploadNotificationLslbAdmin");
        String mailSubject = String.format("%s has re application form for %s", institutionName, gameTypeName);

        emailService.sendEmail(content, mailSubject, lslbAdmin.getEmailAddress());
    }


    @Async
    public void sendAdminCommentNotificationToInstitutionAdmins(ApplicationForm applicationForm, String comment) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(applicationForm.getInstitutionId());
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendCommentNotificationToInstitutionUser(institutionAdmin, comment, applicationForm);
        }
    }

    private void sendCommentNotificationToInstitutionUser(AuthInfo institutionAdmin, String comment, ApplicationForm applicationForm) {
        String institutionAdminName = institutionAdmin.getFullName();
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        String gameTypeName = applicationForm.getGameTypeName();
        HashMap<String, Object> model = new HashMap<>();
        model.put("name", institutionAdminName);
        model.put("comment", comment);
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);

        String mailSubject = String.format("Notification on your application for %s licence", gameTypeName);
        String content = mailContentBuilderService.build(model, "ApplicationFormPendingUploadGAadmin");
        emailService.sendEmail(content, mailSubject, institutionAdmin.getEmailAddress());
    }


    @Async
    public void sendRejectionMailToInstitutionAdmins(ApplicationForm applicationForm) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(applicationForm.getInstitutionId());
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendRejectionMailToInstitutionUser(institutionAdmin, applicationForm);
        }
    }

    private void sendRejectionMailToInstitutionUser(AuthInfo institutionAdmin, ApplicationForm applicationForm) {
        try {
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
            String mailSubject = String.format("Notification on your application for %s licence", gameTypeName);

            String content = mailContentBuilderService.build(model, "application-form-rejection-GA-new");
            emailService.sendEmail(content, mailSubject, institutionAdmin.getEmailAddress());
        } catch (Exception e) {
            logger.error("An error occurred while sending rejection mail to -> {}", institutionAdmin.getEmailAddress(), e);
        }
    }


    @Async
    public void sendApprovedMailToInstitutionAdmins(ApplicationForm applicationForm) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(applicationForm.getInstitutionId());
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendApprovedMailToInstitutionUser(institutionAdmin, applicationForm);
        }
    }

    private void sendApprovedMailToInstitutionUser(AuthInfo institutionAdmin, ApplicationForm applicationForm) {
        String gameTypeName = applicationForm.getGameTypeName();
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        String callBackUrl = String.format("%s/payment-page", frontEndPropertyHelper.getFrontEndUrl());
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        model.put("frontEndUrl", callBackUrl);
        String mailSubject = String.format("Notification on your application for %s licence", gameTypeName);

        String content = mailContentBuilderService.build(model, "application-form-approval-GA-new");
        emailService.sendEmail(content, mailSubject, institutionAdmin.getEmailAddress());
    }


    @Async
    public void sendApplicationFormSubmissionMailToLSLBAdmins(ApplicationForm applicationForm) {
        String institutionName = applicationForm.getInstitutionName();
        String gameTypeName = applicationForm.getGameTypeName();

        List<String> adminEmails = new ArrayList<>();
        ArrayList<AuthInfo> lslbFinanceAdmins = authInfoService.getAllActiveLSLBFinanceAdmins();
        if (lslbFinanceAdmins == null || lslbFinanceAdmins.isEmpty()) {
            logger.info("No active LSLB finance admin found, skipping finance admins");
        } else {
            adminEmails.addAll(lslbFinanceAdmins.stream().map(AuthInfo::getEmailAddress).collect(Collectors.toList()));
        }

        ArrayList<AuthInfo> lslbLegalAdmins = authInfoService.getAllActiveLSLBLegalAdmins();
        if (lslbLegalAdmins == null || lslbLegalAdmins.isEmpty()) {
            logger.info("No active LSLB Legal admin found, skipping legal admins");
        } else {
            adminEmails.addAll(lslbLegalAdmins.stream().map(AuthInfo::getEmailAddress).collect(Collectors.toList()));
        }

        ArrayList<AuthInfo> lslbITAdmins = authInfoService.getAllActiveLSLBITAdmins();
        if (lslbITAdmins == null || lslbITAdmins.isEmpty()) {
            logger.info("No active LSLB IT admin found, skipping IT admins");
        } else {
            adminEmails.addAll(lslbITAdmins.stream().map(AuthInfo::getEmailAddress).collect(Collectors.toList()));
        }

        ArrayList<AuthInfo> lslbGMs = authInfoService.getAllActiveLSLBGeneralManagers();
        if (lslbGMs == null || lslbGMs.isEmpty()) {
            logger.info("No active LSLB GM admin found, skipping GMs admins");
        } else {
            adminEmails.addAll(lslbGMs.stream().map(AuthInfo::getEmailAddress).collect(Collectors.toList()));
        }

        if (adminEmails.isEmpty()) {
            logger.info("No LSLB Admin staff found, Skipping email sending");
            return;
        }
        for (String adminEmail : adminEmails) {
            logger.info("Sending email to LSLB admin with email -> {}", adminEmail);
            sendApplicationFormSubmissionNotificationToLSLSBAdmin(adminEmail, applicationForm, institutionName, gameTypeName);
        }
        logger.info("Finished sending emails to LSLB Admins");
    }


    private void sendApplicationFormSubmissionNotificationToLSLSBAdmin(String adminEmail, ApplicationForm applicationForm, String institutionName, String gameTypeName) {
        try {
            String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), applicationForm.getId());

            String presentDate = DateTime.now().toString("dd-MM-yyyy ");
            HashMap<String, Object> model = new HashMap<>();
            model.put("date", presentDate);
            model.put("gameType", gameTypeName);
            model.put("applicantName", institutionName);
            model.put("frontEndUrl", callbackUrl);
            String mailSubject = "New Application submitted on LSLB Customer Management System";

            String content = mailContentBuilderService.build(model, "ApplicationFormSubmissionLSLB");
            emailService.sendEmail(content, mailSubject, adminEmail);
        } catch (Exception e) {
            logger.error("An error occurred while sending application submission email to -> {}", adminEmail, e);
        }
    }
}
