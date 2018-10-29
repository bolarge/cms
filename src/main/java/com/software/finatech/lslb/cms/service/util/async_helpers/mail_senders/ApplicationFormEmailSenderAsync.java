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

    @Async
    public void sendAdminCommentNotificationToInstitutionAdmins(RenewalForm renewalForm, String comment) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(renewalForm.getInstitutionId());
        String mailSubject = String.format("Notification on your renewal application for %s licence", renewalForm.getGameTypeName());
        String emailContent = buildRenewalCommentFromLSLBAdminEmailContent(renewalForm);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendCommentNotificationToInstitutionUser(institutionAdmin.getEmailAddress(), mailSubject, emailContent);
        }
    }

    @Async
    public void sendAdminCommentNotificationToInstitutionAdmins(AIPDocumentApproval aipDocumentApproval, String comment) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(aipDocumentApproval.getInstitutionId());
        String mailSubject = String.format("Notification on your AIP application for %s licence", aipDocumentApproval.getGameTypeName());
        String emailContent = buildAIPCommentFromLSLBAdminEmailContent(aipDocumentApproval);
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

    private String buildAIPCommentFromLSLBAdminEmailContent(AIPDocumentApproval aipDocumentApproval) {
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        String gameTypeName = aipDocumentApproval.getGameTypeName();
        HashMap<String, Object> model = new HashMap<>();
        model.put("comment", aipDocumentApproval.getLslbAdminComment());
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        return mailContentBuilderService.build(model, "application-form/AIPFormPendingUploadGAadmin");
    }

    private String buildRenewalCommentFromLSLBAdminEmailContent(RenewalForm renewalForm) {
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        String gameTypeName = renewalForm.getGameTypeName();
        HashMap<String, Object> model = new HashMap<>();
        model.put("comment", renewalForm.getLslbAdminComment());
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        return mailContentBuilderService.build(model, "renewal-form/RenewalFormPendingUploadGAadmin");
    }

    @Async
    public void sendRejectionMailToInstitutionAdmins(ApplicationForm applicationForm) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(applicationForm.getInstitutionId());
        String emailContent = buildRejectionEmailContent(applicationForm);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendRejectionMailToInstitutionUser(institutionAdmin.getEmailAddress(), applicationForm, emailContent);
        }
    }

    @Async
    public void sendRejectionMailToInstitutionAdmins(RenewalForm renewalForm) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(renewalForm.getInstitutionId());
        String emailContent = buildRejectionEmailContent(renewalForm);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendRejectionMailToInstitutionUser(institutionAdmin.getEmailAddress(), renewalForm, emailContent);
        }
    }


    @Async
    public void sendRejectionMailToInstitutionAdmins(AIPDocumentApproval aipDocumentApproval) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(aipDocumentApproval.getInstitutionId());
        String emailContent = buildRejectionEmailContent(aipDocumentApproval);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendRejectionMailToInstitutionUser(institutionAdmin.getEmailAddress(), aipDocumentApproval, emailContent);
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
    private void sendRejectionMailToInstitutionUser(String institutionAdminEmail, AIPDocumentApproval aipDocumentApproval, String emailContent) {
        try {
            String mailSubject = String.format("Notification on your AIP for %s licence", aipDocumentApproval.getGameTypeName());
            emailService.sendEmail(emailContent, mailSubject, emailContent);
        } catch (Exception e) {
            logger.error("An error occurred while sending rejection mail to -> {}", institutionAdminEmail, e);
        }
    }

    private void sendRejectionMailToInstitutionUser(String institutionAdminEmail, RenewalForm renewalForm, String emailContent) {
        try {
            String mailSubject = String.format("Notification on your application for %s licence", renewalForm.getGameTypeName());
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
    @Async
    public void sendApprovedMailToInstitutionAdmins(AIPDocumentApproval aipDocumentApproval) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(aipDocumentApproval.getInstitutionId());
        String mailSubject = String.format("Notification on your application for %s licence", aipDocumentApproval.getGameTypeName());
        String emailContent = buildApprovalEmailContent(aipDocumentApproval);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendApprovedMailToInstitutionUser(institutionAdmin.getEmailAddress(), mailSubject, emailContent);
        }
    }
    @Async
    public void sendApprovedMailToInstitutionAdmins(RenewalForm renewalForm) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(renewalForm.getInstitutionId());
        String mailSubject = String.format("Notification on your renewal application for %s licence", renewalForm.getGameTypeName());
        String emailContent = buildApprovalEmailContent(renewalForm);
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

    @Async
    public void sendRenewalFormSubmissionMailToLSLBAdmins(RenewalForm renewalForm) {
        List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_APPLICATION_ID);
        if (lslbAdmins.isEmpty()) {
            logger.info("No LSLB Admin staff found, Skipping email sending");
            return;
        }
        String mailSubject = "New Application submitted on LSLB Customer Management System";
        String emailContent = buildApplicationFormSubmissionEmailContent(renewalForm);
        for (AuthInfo lslbAdmin : lslbAdmins) {
            String adminEmail = lslbAdmin.getEmailAddress();
            logger.info("Sending email to LSLB admin with email -> {}", adminEmail);
            sendApplicationFormSubmissionNotificationToLSLSBAdmin(adminEmail, mailSubject, emailContent);
        }
        sendEmailToDocumentApprovers(renewalForm);
        logger.info("Finished sending emails to LSLB Admins");
    }

    @Async
    public void sendAIPFormSubmissionMailToLSLBAdmins(AIPDocumentApproval aipDocumentApproval) {
        List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_APPLICATION_ID);
        if (lslbAdmins.isEmpty()) {
            logger.info("No LSLB Admin staff found, Skipping email sending");
            return;
        }
        String mailSubject = "New AIP submitted on LSLB Customer Management System";
        String emailContent = buildAIPFormSubmissionEmailContent(aipDocumentApproval);
        for (AuthInfo lslbAdmin : lslbAdmins) {
            String adminEmail = lslbAdmin.getEmailAddress();
            logger.info("Sending email to LSLB admin with email -> {}", adminEmail);
            sendApplicationFormSubmissionNotificationToLSLSBAdmin(adminEmail, mailSubject, emailContent);
        }
        sendEmailToDocumentApprovers(aipDocumentApproval);
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
    private String buildApplicationFormSubmissionEmailContent(RenewalForm renewalForm) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), renewalForm.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", renewalForm.getGameTypeName());
        model.put("applicantName", renewalForm.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        return mailContentBuilderService.build(model, "renewal-form/RenewalFormSubmissionLSLB");
    }

    private String buildAIPFormSubmissionEmailContent(AIPDocumentApproval aipDocumentApproval) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), aipDocumentApproval.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", aipDocumentApproval.getGameTypeName());
        model.put("applicantName", aipDocumentApproval.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        return mailContentBuilderService.build(model, "aip-form/AIPFormSubmissionLSLB");
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

    private String buildRejectionEmailContent(AIPDocumentApproval aipDocumentApproval) {
        String gameTypeName = aipDocumentApproval.getGameTypeName();
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        LocalDate submissionDate = aipDocumentApproval.getSubmissionDate();
        String submissionDateString = "";
        if (submissionDate != null) {
            submissionDateString = submissionDate.toString("dd-MM-yyyy");
        }
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        model.put("institutionName", aipDocumentApproval.getInstitutionName());
        model.put("submissionDate", submissionDateString);
        model.put("rejectionReason", aipDocumentApproval.getReasonForRejection());
        return mailContentBuilderService.build(model, "aip-form/aip-form-rejection-GA-new");
    }
    private String buildRejectionEmailContent(RenewalForm renewalForm) {
        String gameTypeName = renewalForm.getGameTypeName();
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        LocalDate submissionDate = renewalForm.getSubmissionDate();
        String submissionDateString = "";
        if (submissionDate != null) {
            submissionDateString = submissionDate.toString("dd-MM-yyyy");
        }
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        model.put("institutionName", renewalForm.getInstitutionName());
        model.put("submissionDate", submissionDateString);
        model.put("rejectionReason", renewalForm.getReasonForRejection());
        return mailContentBuilderService.build(model, "renewal-form/renewal-form-rejection-GA-new");
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
    private String buildApprovalEmailContent(AIPDocumentApproval aipDocumentApproval) {
        String gameTypeName = aipDocumentApproval.getGameTypeName();
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        String callBackUrl = String.format("%s/payment-page", frontEndPropertyHelper.getFrontEndUrl());
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        model.put("frontEndUrl", callBackUrl);
        return mailContentBuilderService.build(model, "aip-form/aip-form-approval-GA-new");
    }

    private String buildApprovalEmailContent(RenewalForm renewalForm) {
        String gameTypeName = renewalForm.getGameTypeName();
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        String callBackUrl = String.format("%s/payment-page", frontEndPropertyHelper.getFrontEndUrl());
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", gameTypeName);
        model.put("frontEndUrl", callBackUrl);
        return mailContentBuilderService.build(model, "renewal-form/renewal-form-approval-GA-new");
    }

    private ArrayList<Document> getAllDocumentsForApplicationForm(ApplicationForm applicationForm) {
        Query query = new Query();
        query.addCriteria(Criteria.where("entityId").is(applicationForm.getId()));
        query.addCriteria(Criteria.where("isActive").is(true));
        query.addCriteria(Criteria.where("notificationSent").is(false));
        return (ArrayList<Document>) mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());
    }
    private ArrayList<Document> getAllDocumentsForRenewalForm(RenewalForm renewalForm) {
        Query query = new Query();
        query.addCriteria(Criteria.where("entityId").is(renewalForm.getId()));
        query.addCriteria(Criteria.where("isActive").is(true));
        query.addCriteria(Criteria.where("notificationSent").is(false));
        return (ArrayList<Document>) mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());
    }

    private ArrayList<Document> getAllDocumentsForAIPForm(AIPDocumentApproval aipDocumentApproval) {
        Query query = new Query();
        query.addCriteria(Criteria.where("entityId").is(aipDocumentApproval.getId()));
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

    private List<DocumentNotification> getDocumentNotificationsForRenewalForm(RenewalForm renewalForm) {
        List<DocumentNotification> documentNotifications = new ArrayList<>();
        ArrayList<Document> documentsForForm = getAllDocumentsForRenewalForm(renewalForm);
        if (documentsForForm.isEmpty()) {
            sendApproverMailToFinalApproval(renewalForm);
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

    private List<DocumentNotification> getDocumentNotificationsForAIPForm(AIPDocumentApproval aipDocumentApproval) {
        List<DocumentNotification> documentNotifications = new ArrayList<>();
        ArrayList<Document> documentsForForm = getAllDocumentsForAIPForm(aipDocumentApproval);
        if (documentsForForm.isEmpty()) {
            sendApproverMailToFinalApproval(aipDocumentApproval);
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
    private String buildDocumentSubmissionMailContentLSLB(RenewalForm renewalForm, Document document) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), renewalForm.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", renewalForm.getGameTypeName());
        model.put("applicantName", renewalForm.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        model.put("documentType", String.valueOf(document.getDocumentType()));
        return mailContentBuilderService.build(model, "renewal-form/RenewalFormDocumentSubmissionLSLB");
    }
    private String buildDocumentSubmissionMailContentLSLB(AIPDocumentApproval aipDocumentApproval, Document document) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), aipDocumentApproval.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", aipDocumentApproval.getGameTypeName());
        model.put("applicantName", aipDocumentApproval.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        model.put("documentType", String.valueOf(document.getDocumentType()));
        return mailContentBuilderService.build(model, "aip-form/AIPFormDocumentSubmissionLSLB");
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

    private void sendEmailToDocumentApprovers(RenewalForm renewalForm) {
        try {
            List<DocumentNotification> documentNotifications = getDocumentNotificationsForRenewalForm(renewalForm);
            if (documentNotifications.isEmpty()) {
                renewalForm.setReadyForApproval(true);
                mongoRepositoryReactive.saveOrUpdate(renewalForm);
                sendApproverMailToFinalApproval(renewalForm);
                return;
            }
            FormDocumentApproval documentApproval = new FormDocumentApproval();
            documentApproval.setSupposedLength(documentNotifications.size());
            Map<String, Boolean> approvalMap = new HashMap<>();
            String mailSubject = "New Renewal Application submission on LSLB Customer Management System";
            for (DocumentNotification documentNotification : documentNotifications) {
                String email = documentNotification.getApproverEmail();
                logger.info("Sending approval email to {}", email);
                Document document = documentNotification.getDocument();
                String mailContent = buildDocumentSubmissionMailContentLSLB(renewalForm, document);
                emailService.sendEmail(mailContent, mailSubject, email);
                document.setNotificationSent(true);
                approvalMap.put(document.getId(), false);
                mongoRepositoryReactive.saveOrUpdate(document);
            }
            documentApproval.setApprovalMap(approvalMap);
            renewalForm.setDocumentApproval(documentApproval);
            mongoRepositoryReactive.saveOrUpdate(renewalForm);
        } catch (Exception e) {
            logger.error("An error occurred while sending email to application form document approvers", e);
        }
    }

    private void sendEmailToDocumentApprovers(AIPDocumentApproval aipDocumentApproval) {
        try {
            List<DocumentNotification> documentNotifications = getDocumentNotificationsForAIPForm(aipDocumentApproval);
            if (documentNotifications.isEmpty()) {
                aipDocumentApproval.setReadyForApproval(true);
                mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
                sendApproverMailToFinalApproval(aipDocumentApproval);
                return;
            }
            FormDocumentApproval documentApproval = new FormDocumentApproval();
            documentApproval.setSupposedLength(documentNotifications.size());
            Map<String, Boolean> approvalMap = new HashMap<>();
            String mailSubject = "New AIP submission on LSLB Customer Management System";
            for (DocumentNotification documentNotification : documentNotifications) {
                String email = documentNotification.getApproverEmail();
                logger.info("Sending approval email to {}", email);
                Document document = documentNotification.getDocument();
                String mailContent = buildDocumentSubmissionMailContentLSLB(aipDocumentApproval, document);
                emailService.sendEmail(mailContent, mailSubject, email);
                document.setNotificationSent(true);
                approvalMap.put(document.getId(), false);
                mongoRepositoryReactive.saveOrUpdate(document);
            }
            documentApproval.setApprovalMap(approvalMap);
            aipDocumentApproval.setDocumentApproval(documentApproval);
            mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
        } catch (Exception e) {
            logger.error("An error occurred while sending email to AIP form document approvers", e);
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
    public void sendApproverMailToFinalApproval(AIPDocumentApproval aipDocumentApproval) {
        ArrayList<AuthInfo> finalApprovers = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.APPROVE_APPICATION_FORM_ID);
        if (finalApprovers.isEmpty()) {
            logger.info("No final approvers for application form");
            return;
        }
        String mailContent = buildAIPFormSubmissionApprovalEmailContent(aipDocumentApproval);
        for (AuthInfo authInfo : finalApprovers) {
            String emailAddress = authInfo.getEmailAddress();
            logger.info("Sending final approver email to {}", emailAddress);
            emailService.sendEmail(mailContent, "New AIP Submission on LSLB Customer Management System", emailAddress);
        }
    }

    public void sendApproverMailToFinalApproval(RenewalForm renewalForm) {
        ArrayList<AuthInfo> finalApprovers = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.APPROVE_APPICATION_FORM_ID);
        if (finalApprovers.isEmpty()) {
            logger.info("No final approvers for application form");
            return;
        }
        String mailContent = buildRenewalFormFormSubmissionApprovalEmailContent(renewalForm);
        for (AuthInfo authInfo : finalApprovers) {
            String emailAddress = authInfo.getEmailAddress();
            logger.info("Sending final approver email to {}", emailAddress);
            emailService.sendEmail(mailContent, "New Application Submission on LSLB Customer Management System", emailAddress);
        }
    }

    private String buildAIPFormSubmissionApprovalEmailContent(AIPDocumentApproval aipDocumentApproval) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), aipDocumentApproval.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", aipDocumentApproval.getGameTypeName());
        model.put("applicantName", aipDocumentApproval.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        return mailContentBuilderService.build(model, "application-form/ApplicationFormSubmissionApprovalLSLB");
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

    private String buildRenewalFormFormSubmissionApprovalEmailContent(RenewalForm renewalForm) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), renewalForm.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", renewalForm.getGameTypeName());
        model.put("applicantName", renewalForm.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        return mailContentBuilderService.build(model, "renewal-form/RenewalFormSubmissionApprovalLSLB");
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
    public void sendDocumentReturnMailToInstitutionMembers(AIPDocumentApproval aipDocumentApproval, Document document) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(aipDocumentApproval.getInstitutionId());
        String mailContent = buildDocumentReturnMailContent(aipDocumentApproval, document);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            try {
                String email = institutionAdmin.getEmailAddress();
                logger.info("Sending document return email to {}", email);
                emailService.sendEmail(mailContent, String.format("Notification on your AIP for %s licence", aipDocumentApproval.getGameTypeName()), email);
            } catch (Exception e) {
                logger.error("An error occurred while sending email", e);
            }
        }
    }

    public void sendDocumentReturnMailToInstitutionMembers(RenewalForm renewalForm, Document document) {
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(renewalForm.getInstitutionId());
        String mailContent = buildDocumentReturnMailContent(renewalForm, document);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            try {
                String email = institutionAdmin.getEmailAddress();
                logger.info("Sending document return email to {}", email);
                emailService.sendEmail(mailContent, String.format("Notification on your renewal application for %s licence", renewalForm.getGameTypeName()), email);
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

    private String buildDocumentReturnMailContent(AIPDocumentApproval aipDocumentApproval, Document document) {
        String callbackUrl = String.format("%s/%s/reupload/%s", frontEndPropertyHelper.getFrontEndUrl(), aipDocumentApproval.getId(), document.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", aipDocumentApproval.getGameTypeName());
        model.put("applicantName", aipDocumentApproval.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        model.put("fileName", document.getFilename());
        model.put("documentType", String.valueOf(document.getDocumentType()));
        return mailContentBuilderService.build(model, "aip-form/AIPFormDocumentReturnGAAdmin");
    }
    private String buildDocumentReturnMailContent(RenewalForm renewalForm, Document document) {
        String callbackUrl = String.format("%s/%s/reupload/%s", frontEndPropertyHelper.getFrontEndUrl(), renewalForm.getId(), document.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", renewalForm.getGameTypeName());
        model.put("applicantName", renewalForm.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        model.put("fileName", document.getFilename());
        model.put("documentType", String.valueOf(document.getDocumentType()));
        return mailContentBuilderService.build(model, "renewal-form/RenewalFormDocumentReturnGAAdmin");
    }

    private String buildResubmissionNotificationForApplicationForm(ApplicationForm applicationForm, Document document) {
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
    private String buildResubmissionNotificationForAIPForm(AIPDocumentApproval aipDocumentApproval, Document document) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), aipDocumentApproval.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", aipDocumentApproval.getGameTypeName());
        model.put("applicantName", aipDocumentApproval.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        model.put("documentType", String.valueOf(document.getDocumentType()));
        return mailContentBuilderService.build(model, "aip-form/AIPFormDocumentResubmissionLSLB");
    }

    private String buildResubmissionNotificationForRenewalForm(RenewalForm renewalForm, Document document) {
        String callbackUrl = String.format("%s/applications/%s", frontEndPropertyHelper.getFrontEndUrl(), renewalForm.getId());
        String presentDate = DateTime.now().toString("dd-MM-yyyy ");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDate);
        model.put("gameType", renewalForm.getGameTypeName());
        model.put("applicantName", renewalForm.getInstitutionName());
        model.put("frontEndUrl", callbackUrl);
        model.put("documentType", String.valueOf(document.getDocumentType()));
        return mailContentBuilderService.build(model, "renewal-form/RenewalFormDocumentResubmissionLSLB");
    }

    @Async
    public void sendResubmissionNotificationForApplicationForm(ApplicationForm applicationForm, Document document) {
        AuthInfo approver = document.getApprover();
        if (approver != null) {
            String mailContent = buildResubmissionNotificationForApplicationForm(applicationForm, document);
            emailService.sendEmail(mailContent, String.format("%s has resubmitted %s", applicationForm.getInstitutionName(), document.getDocumentType()), approver.getEmailAddress());
        }
    }

    @Async
    public void sendResubmissionNotificationForApplicationForm(AIPDocumentApproval aipDocumentApproval, Document document) {
        AuthInfo approver = document.getApprover();
        if (approver != null) {
            String mailContent = buildResubmissionNotificationForAIPForm(aipDocumentApproval, document);
            emailService.sendEmail(mailContent, String.format("%s has resubmitted %s", aipDocumentApproval.getInstitutionName(), document.getDocumentType()), approver.getEmailAddress());
        }
    }

    @Async
    public void sendResubmissionNotificationForRenewalForm(RenewalForm renewalForm, Document document) {
        AuthInfo approver = document.getApprover();
        if (approver != null) {
            String mailContent = buildResubmissionNotificationForRenewalForm(renewalForm, document);
            emailService.sendEmail(mailContent, String.format("%s has resubmitted %s", renewalForm.getInstitutionName(), document.getDocumentType()), approver.getEmailAddress());
        }
    }
}
