package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.ApplicationForm;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.domain.LslbAdminComment;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class ApplicationFormNotificationHelperAsync {

    private AuthInfoService authInfoService;
    private MailContentBuilderService mailContentBuilderService;
    private EmailService emailService;

    @Autowired
    public ApplicationFormNotificationHelperAsync(AuthInfoService authInfoService,
                                                  MailContentBuilderService mailContentBuilderService,
                                                  EmailService emailService) {
        this.authInfoService = authInfoService;
        this.mailContentBuilderService = mailContentBuilderService;
        this.emailService = emailService;
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
        String presentDate = DateTime.now().toString("dd/MM/yyyy");
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
        String presentDate = DateTime.now().toString("dd/MM/yyyy ");
        String gameTypeName = applicationForm.getGameTypeName();
        HashMap<String, Object> model = new HashMap<>();
        model.put("name", institutionAdminName);
        model.put("comment", comment);
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);

        String mailSubject = String.format("Notification on your application for %s license", gameTypeName);
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
        String gameTypeName = applicationForm.getGameTypeName();
        String presentDate = DateTime.now().toString("dd/MM/yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        String mailSubject = String.format("Notification on your application for %s license", gameTypeName);

        String content = mailContentBuilderService.build(model, "application-form-rejection-GA-new");
        emailService.sendEmail(content, mailSubject, institutionAdmin.getEmailAddress());
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
        String presentDate = DateTime.now().toString("dd/MM/yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        String mailSubject = String.format("Notification on your application for %s license", gameTypeName);

        String content = mailContentBuilderService.build(model, "application-form-approval-GA-new");
        emailService.sendEmail(content, mailSubject, institutionAdmin.getEmailAddress());
    }
}
