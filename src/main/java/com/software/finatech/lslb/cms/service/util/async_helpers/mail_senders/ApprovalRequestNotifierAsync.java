package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.DocumentApprovalRequest;
import com.software.finatech.lslb.cms.service.domain.FeeApprovalRequest;
import com.software.finatech.lslb.cms.service.domain.UserApprovalRequest;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class ApprovalRequestNotifierAsync extends AbstractMailSender {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalRequestNotifierAsync.class);

    @Async
    public void sendNewUserApprovalRequestEmailToAllOtherUsersInRole(AuthInfo initiator, UserApprovalRequest userApprovalRequest) {
        ArrayList<AuthInfo> otherUserWithRole = authInfoService.findAllOtherActiveUsersForApproval(initiator);
        if (otherUserWithRole == null || otherUserWithRole.isEmpty()) {
            logger.info("There are no other enabled users with user role");
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
        ArrayList<AuthInfo> otherUserWithRole = authInfoService.findAllOtherActiveUsersForApproval(initiator);
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
        ArrayList<AuthInfo> otherUserWithRole = authInfoService.findAllOtherActiveUsersForApproval(initiator);
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


    private String buildNewUserApprovalRequestEmailContent(UserApprovalRequest userApprovalRequest) {
        String frontEndUrl = String.format("%s/user-approvals/%s", frontEndPropertyHelper.getFrontEndUrl(), userApprovalRequest.getId());
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
}
