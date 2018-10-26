package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.DocumentNotification;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ApplicationFormEmailSenderAsync extends AbstractMailSender {

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
        return mailContentBuilderService.build(model, "application-form/ApplicationFormPendingUploadGAadmin");
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
        List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_APPLICATION_ID);
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
        sendEmailToDocumentApprovers(applicationForm);
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
        return mailContentBuilderService.build(model, "application-form/ApplicationFormSubmissionLSLB");
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
        return mailContentBuilderService.build(model, "application-form/application-form-rejection-GA-new");
    }

    private String buildApprovalEmailContent(ApplicationForm applicationForm) {
        String gameTypeName = applicationForm.getGameTypeName();
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        String callBackUrl = String.format("%s/payment-page", frontEndPropertyHelper.getFrontEndUrl());
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        model.put("frontEndUrl", callBackUrl);
        return mailContentBuilderService.build(model, "application-form/application-form-approval-GA-new");
    }

    private ArrayList<Document> getAllDocumentsForApplicationForm(ApplicationForm applicationForm) {
        Query query = new Query();
        query.addCriteria(Criteria.where("entityId").is(applicationForm.getId()));
        query.addCriteria(Criteria.where("isActive").is(true));
        query.addCriteria(Criteria.where("notificationSent").is(false));
        return (ArrayList<Document>) mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());
    }

    private List<DocumentNotification> getDocumentNotificationsForApplicationForm(ApplicationForm applicationForm) {
        List<DocumentNotification> documentNotifications = new ArrayList<>();
        ArrayList<Document> documentsForForm = getAllDocumentsForApplicationForm(applicationForm);
        if (documentsForForm.isEmpty()) {
            sendApproverMailToFinalApproval(applicationForm);
        } else {
            for (Document document : documentsForForm) {
                DocumentType documentType = document.getDocumentType();
                if (documentType != null) {
                    AuthInfo approver = documentType.getApprover();
                    if (approver != null) {
                        documentNotifications.add(DocumentNotification.fromApproverEmailAndDocument(approver.getEmailAddress(), document));
                    }
                }
            }
        }
        return documentNotifications;
    }

    private String buildDocumentSubmissionMailContentLSLB(ApplicationForm applicationForm, Document document) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), applicationForm.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", applicationForm.getGameTypeName());
        model.put("applicantName", applicationForm.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        model.put("documentType", String.valueOf(document.getDocumentType()));
        return mailContentBuilderService.build(model, "application-form/ApplicationFormDocumentSubmissionLSLB");
    }

    private void sendEmailToDocumentApprovers(ApplicationForm applicationForm) {
        try {
            List<DocumentNotification> documentNotifications = getDocumentNotificationsForApplicationForm(applicationForm);
            if (documentNotifications.isEmpty()) {
                applicationForm.setReadyForApproval(true);
                mongoRepositoryReactive.saveOrUpdate(applicationForm);
                sendApproverMailToFinalApproval(applicationForm);
                return;
            }
            FormDocumentApproval documentApproval = new FormDocumentApproval();
            documentApproval.setSupposedLength(documentNotifications.size());
            Map<String, Boolean> approvalMap = new HashMap<>();
            String mailSubject = "New Application submission on LSLB Customer Management System";
            for (DocumentNotification documentNotification : documentNotifications) {
                String email = documentNotification.getApproverEmail();
                logger.info("Sending approval email to {}", email);
                Document document = documentNotification.getDocument();
                String mailContent = buildDocumentSubmissionMailContentLSLB(applicationForm, document);
                emailService.sendEmail(mailContent, mailSubject, email);
                document.setNotificationSent(true);
                approvalMap.put(document.getId(), false);
                mongoRepositoryReactive.saveOrUpdate(document);
            }
            documentApproval.setApprovalMap(approvalMap);
            applicationForm.setDocumentApproval(documentApproval);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);
        } catch (Exception e) {
            logger.error("An error occurred while sending email to application form document approvers", e);
        }
    }

    public void sendApproverMailToFinalApproval(ApplicationForm applicationForm) {
        ArrayList<AuthInfo> finalApprovers = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.APPROVE_APPICATION_FORM_ID);
        if (finalApprovers.isEmpty()) {
            logger.info("No final approvers for application form");
            return;
        }
        String mailContent = buildApplicationFormSubmissionApprovalEmailContent(applicationForm);
        for (AuthInfo authInfo : finalApprovers) {
            String emailAddress = authInfo.getEmailAddress();
            logger.info("Sending final approver email to {}", emailAddress);
            emailService.sendEmail(mailContent, "New Application Submission on LSLB Customer Management System", emailAddress);
        }
    }

    private String buildApplicationFormSubmissionApprovalEmailContent(ApplicationForm applicationForm) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), applicationForm.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", applicationForm.getGameTypeName());
        model.put("applicantName", applicationForm.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        return mailContentBuilderService.build(model, "application-form/ApplicationFormSubmissionApprovalLSLB");
    }

    public void sendDocumentReturnMailToInstitutionMembers(ApplicationForm applicationForm, Document document) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(applicationForm.getInstitutionId());
        String mailContent = buildDocumentReturnMailContent(applicationForm, document);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            try {
                String email = institutionAdmin.getEmailAddress();
                logger.info("Sending document return email to {}", email);
                emailService.sendEmail(mailContent, String.format("Notification on your application for %s licence", applicationForm.getGameTypeName()), email);
            } catch (Exception e) {
                logger.error("An error occurred while sending email", e);
            }
        }
    }

    private String buildDocumentReturnMailContent(ApplicationForm applicationForm, Document document) {
        String callbackUrl = String.format("%s/%s/reupload/%s", frontEndPropertyHelper.getFrontEndUrl(), applicationForm.getId(), document.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", applicationForm.getGameTypeName());
        model.put("applicantName", applicationForm.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        model.put("fileName", document.getFilename());
        model.put("documentType", String.valueOf(document.getDocumentType()));
        return mailContentBuilderService.build(model, "application-form/ApplicationFormDocumentReturnGAAdmin");
    }

    private String buildResubmissionNotificationFoApplicationForm(ApplicationForm applicationForm, Document document) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), applicationForm.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", applicationForm.getGameTypeName());
        model.put("applicantName", applicationForm.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        model.put("documentType", String.valueOf(document.getDocumentType()));
        return mailContentBuilderService.build(model, "application-form/ApplicationFormDocumentResubmissionLSLB");
    }

    @Async
    public void sendResubmissionNotificationFoApplicationForm(ApplicationForm applicationForm, Document document) {
        AuthInfo approver = document.getApprover();
        if (approver != null) {
            String mailContent = buildResubmissionNotificationFoApplicationForm(applicationForm, document);
            emailService.sendEmail(mailContent, String.format("%s has resubmitted %s", applicationForm.getInstitutionName(), document.getDocumentType()), approver.getEmailAddress());
        }
    }
}
