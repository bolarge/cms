package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.util.FrontEndPropertyHelper;
import com.software.finatech.lslb.cms.service.util.StringCapitalizer;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
public class ApprovalRequestNotifierAsync  {
    private static final Logger logger = LoggerFactory.getLogger(ApprovalRequestNotifierAsync.class);
    @Autowired
    protected MailContentBuilderService mailContentBuilderService;
    @Autowired
    protected EmailService emailService;
    @Autowired
    protected FrontEndPropertyHelper frontEndPropertyHelper;
    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Async
    public void sendNewUserApprovalRequestEmailToAllOtherUsersInRole(AuthInfo initiator, UserApprovalRequest userApprovalRequest) {
        //Find if there is user available for approvals, if there no user, set approver to him
        ArrayList<AuthInfo> otherUserWithRole = findAllOtherActiveUsersForApproval(initiator);
        if (otherUserWithRole == null || otherUserWithRole.isEmpty()) {
            logger.info("There are no other enabled users with user role");
            userApprovalRequest.setInitiatorId(null);
            mongoRepositoryReactive.saveOrUpdate(userApprovalRequest);
            return;
        }

        String mailContent = buildNewUserApprovalRequestEmailContent(userApprovalRequest);
        for (AuthInfo user : otherUserWithRole) {
            String userEmail = user.getEmailAddress();
            logger.info("Sending new user email to {}", userEmail);
            emailService.sendEmail(mailContent, "New User Approval Request on LSLB CMS", userEmail);
        }
    }

    @Async
    public void sendNewDocumentApprovalRequestEmailToAllOtherUsersInRole(AuthInfo initiator, DocumentApprovalRequest documentApprovalRequest) {
        ArrayList<AuthInfo> otherUserWithRole = findAllOtherActiveUsersForApproval(initiator);
        if (otherUserWithRole == null || otherUserWithRole.isEmpty()) {
            logger.info("There are no other enabled users with user role");
            return;
        }

        String mailContent = buildNewDocumentApprovalRequestEmailContent(documentApprovalRequest);
        for (AuthInfo user : otherUserWithRole) {
            String userEmail = user.getEmailAddress();
            logger.info("Sending new document email to {}", userEmail);
            emailService.sendEmail(mailContent, "New Document Approval Request on LSLB CMS", userEmail);
        }
    }

    @Async
    public void sendNewFeeApprovalRequestEmailToAllOtherUsersInRole(AuthInfo initiator, FeeApprovalRequest feeApprovalRequest) {
        ArrayList<AuthInfo> otherUserWithRole = findAllOtherActiveUsersForApproval(initiator);
        if (otherUserWithRole == null || otherUserWithRole.isEmpty()) {
            logger.info("There are no other enabled users with user role");
            return;
        }

        String mailContent = buildNewFeeApprovalRequestEmailContent(feeApprovalRequest);
        for (AuthInfo user : otherUserWithRole) {
            String userEmail = user.getEmailAddress();
            logger.info("Sending new fee email to {}", userEmail);
            emailService.sendEmail(mailContent, "New Fee Approval Request on LSLB CMS", userEmail);
        }
    }

    @Async
    public void sendRejectedFeeApprovalRequestEmailToInitiator(FeeApprovalRequest feeApprovalRequest) {
        AuthInfo initiator = feeApprovalRequest.getInitiator();
        String mailContent = buildRejectedFeeApprovalRequestEmailContent(feeApprovalRequest);
        String userEmail = initiator.getEmailAddress();
        logger.info("Sending rejected fee email to {}", userEmail);
        emailService.sendEmail(mailContent, "Update on your Fee Approval Request on LSLB CMS", userEmail);
    }

    @Async
    public void sendRejectedDocumentApprovalRequestEmailToInitiator(DocumentApprovalRequest documentApprovalRequest) {
        AuthInfo initiator = documentApprovalRequest.getInitiator();
        String mailContent = buildRejectedDocumentApprovalRequestEmailContent(documentApprovalRequest);
        String userEmail = initiator.getEmailAddress();
        logger.info("Sending rejected document email to {}", userEmail);
        emailService.sendEmail(mailContent, "Update on your Document Approval Request on LSLB CMS", userEmail);
    }

    @Async
    public void sendRejectedUserApprovalRequestEmailToInitiator(UserApprovalRequest userApprovalRequest) {
        AuthInfo initiator = userApprovalRequest.getInitiator();
        String mailContent = buildRejectedUserApprovalRequestEmailContent(userApprovalRequest);
        String userEmail = initiator.getEmailAddress();
        logger.info("Sending rejected user email to {}", userEmail);
        emailService.sendEmail(mailContent, "Update on your User Approval Request on LSLB CMS", userEmail);
    }

    private String buildNewUserApprovalRequestEmailContent(UserApprovalRequest userApprovalRequest) {
        String frontEndUrl = String.format("%s/user-approval-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), userApprovalRequest.getId());
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        AuthInfo initiator = userApprovalRequest.getInitiator();
        String initiatorName = "";
        if (initiator != null) {
            initiatorName = initiator.getFullName();
        }
        String approvalRequestType = String.valueOf(userApprovalRequest.getUserApprovalRequestType());
        HashMap<String, Object> model = new HashMap<>();
        model.put("initiatorName", initiatorName);
        model.put("date", presentDateString);
        model.put("approvalType", approvalRequestType);
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "approval-request/NewUserApprovalRequest");
    }

    private String buildNewFeeApprovalRequestEmailContent(FeeApprovalRequest feeApprovalRequest) {
        String frontEndUrl = String.format("%s/fee-configurations-details/%s", frontEndPropertyHelper.getFrontEndUrl(), feeApprovalRequest.getId());
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        AuthInfo initiator = feeApprovalRequest.getInitiator();
        String initiatorName = "";
        if (initiator != null) {
            initiatorName = initiator.getFullName();
        }
        String approvalRequestType = String.valueOf(feeApprovalRequest.getFeeApprovalRequestType());
        HashMap<String, Object> model = new HashMap<>();
        model.put("initiatorName", initiatorName);
        model.put("date", presentDateString);
        model.put("approvalType", approvalRequestType);
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "approval-request/NewFeeApprovalRequest");
    }

    private String buildNewDocumentApprovalRequestEmailContent(DocumentApprovalRequest documentApprovalRequest) {
        String frontEndUrl = String.format("%s/document-approvals-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), documentApprovalRequest.getId());
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        AuthInfo initiator = documentApprovalRequest.getInitiator();
        String initiatorName = "";
        if (initiator != null) {
            initiatorName = initiator.getFullName();
        }
        String approvalRequestType = String.valueOf(documentApprovalRequest.getDocumentApprovalRequestType());
        HashMap<String, Object> model = new HashMap<>();
        model.put("initiatorName", initiatorName);
        model.put("date", presentDateString);
        model.put("approvalType", approvalRequestType);
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "approval-request/NewDocumentApprovalRequest");
    }

    private String buildRejectedDocumentApprovalRequestEmailContent(DocumentApprovalRequest documentApprovalRequest) {
        String frontEndUrl = String.format("%s/document-approvals-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), documentApprovalRequest.getId());
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        String approvalRequestType = String.valueOf(documentApprovalRequest.getDocumentApprovalRequestType());
        DocumentType documentType = documentApprovalRequest.getSubjectDocumentType();
        String documentTypeName = String.valueOf(documentType);
        documentTypeName = StringCapitalizer.convertToTitleCaseIteratingChars(documentTypeName);
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("approvalType", approvalRequestType);
        model.put("documentName", documentTypeName);
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "approval-request/RejectedDocumentApprovalRequest");
    }


    private String buildRejectedFeeApprovalRequestEmailContent(FeeApprovalRequest feeApprovalRequest) {
        String frontEndUrl = String.format("%s/fee-configurations-details/%s", frontEndPropertyHelper.getFrontEndUrl(), feeApprovalRequest.getId());
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        String approvalRequestType = String.valueOf(feeApprovalRequest.getFeeApprovalRequestType());
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("approvalType", approvalRequestType);
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "approval-request/RejectedFeeApprovalRequest");
    }

    private String buildRejectedUserApprovalRequestEmailContent(UserApprovalRequest userApprovalRequest) {
        String frontEndUrl = String.format("%s/user-approval-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), userApprovalRequest.getId());
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        String subjectUserName = userApprovalRequest.getSubjectUserName();
        String approvalRequestType = String.valueOf(userApprovalRequest.getUserApprovalRequestType());
        HashMap<String, Object> model = new HashMap<>();
        model.put("userName", subjectUserName);
        model.put("date", presentDateString);
        model.put("approvalType", approvalRequestType);
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "approval-request/RejectedUserApprovalRequest");
    }

    private ArrayList<AuthInfo> findAllOtherActiveUsersForApproval(AuthInfo initiator) {
        Query query = new Query();
        query.addCriteria(Criteria.where("enabled").is(true));
        query.addCriteria(Criteria.where("authRoleId").is(initiator.getAuthRoleId()));
        query.addCriteria(Criteria.where("id").ne(initiator.getId()));
        return (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());
    }
}
