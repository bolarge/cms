package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Machine;
import com.software.finatech.lslb.cms.service.domain.MachineApprovalRequest;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.software.finatech.lslb.cms.service.util.StringCapitalizer.convertToTitleCaseIteratingChars;

@Component
public class MachineApprovalRequestMailSenderAsync extends AbstractMailSender {
    private static final Logger logger = LoggerFactory.getLogger(MachineApprovalRequestMailSenderAsync.class);

    @Async
    public void sendInitialMachineStateUpdateToAgentOperators(MachineApprovalRequest machineApprovalRequest) {
        Machine gamingTerminal = machineApprovalRequest.getMachine();
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(gamingTerminal.getInstitutionId());
        String content = buildInitialMachineApprovalRequestOperatorNotificationContent(machineApprovalRequest);
        for (AuthInfo admin : institutionAdmins) {
            String email = admin.getEmailAddress();
            try {
                logger.info("Sending machine state update to operator admin with email {}", email);
                emailService.sendEmail(content, "Gaming Terminal Approval Request on LSLB", email);
            } catch (Exception e) {
                logger.error("An error occurred while sending mail to institution admin {}", email, e);
            }
        }
    }

    @Async
    public void sendMachineApprovalInitialNotificationToLSLBAdmins(MachineApprovalRequest approvalRequest) {
        List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_MACHINE_APPLICATION_NOTIFICATION_ID);
        if (lslbAdmins.isEmpty()) {
            logger.info("There are no lslb admins that can receive new machine requests");
            return;
        }
        String content = buildNewMachineApprovalRequestNotificationMailContent(approvalRequest);
        String mailSubject = "";
        if (approvalRequest.isGamingTerminalRequest()) {
            mailSubject = "New Terminal Approval Request on LSLB Customer Management System";
        }
        if (approvalRequest.isGamingMachineRequest()) {
            mailSubject = "New Machine Approval Request on LSLB Customer Management System";
        }
        for (AuthInfo authInfo : lslbAdmins) {
            String email = authInfo.getEmailAddress();
            try {
                logger.info("Sending initial machine request email to {}", email);
                emailService.sendEmail(content, mailSubject, email);
            } catch (Exception e) {
                logger.error("An error occurred while sending initial machine approval notification to {}", email, e);
            }
        }
    }

    @Async
    public void sendMultipleMachineCreateRequestToLSLBAdmins(String institutionName, String count) {
        List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_MACHINE_APPLICATION_NOTIFICATION_ID);
        if (lslbAdmins.isEmpty()) {
            logger.info("There are no lslb admins that can receive new machine requests");
            return;
        }
        String content = buildMultipleMachineCreateApprovalNotificationOperator(institutionName, count);
        for (AuthInfo authInfo : lslbAdmins) {
            String email = authInfo.getEmailAddress();
            try {
                logger.info("Sending initial machine request email to {}", email);
                emailService.sendEmail(content, "New Multiple Machine Approval Request on LSLB Customer Management System", email);
            } catch (Exception e) {
                logger.error("An error occurred while sending initial machine approval notification to {}", email, e);
            }
        }
    }


    @Async
    public void sendMachineApprovalNotificationToRequestInitiator(MachineApprovalRequest approvalRequest) {
        String content = buildMachineApprovalNotificationOperator(approvalRequest);
        String machineSerialNumber = approvalRequest.getMachineRequestSerialNumber();
        if (approvalRequest.isInitiatedByInstitution()) {
            List<AuthInfo> operatorAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(approvalRequest.getInstitutionId());
            String mailSubject = String.format("Update on your machine approval for machine with serial number %s", machineSerialNumber);
            for (AuthInfo operatorAdmin : operatorAdmins) {
                String adminEmail = operatorAdmin.getEmailAddress();
                try {
                    logger.info("Sending machine approval notification to {}", adminEmail);
                    emailService.sendEmail(content, mailSubject, adminEmail);
                } catch (Exception e) {
                    logger.error("An error occurred while sending  machine approval notification to {}", adminEmail, e);
                }
            }
        } else {
            AuthInfo initiator = approvalRequest.getInitiator();
            String mailSubject = String.format("Update on your approval request for terminal with serial number %s", machineSerialNumber);
            logger.info("Sending machine approval notification to {}", initiator.getEmailAddress());
            emailService.sendEmail(content, mailSubject, initiator.getEmailAddress());
        }
    }

    private String buildInitialMachineApprovalRequestOperatorNotificationContent(MachineApprovalRequest machineApprovalRequest) {
        String frontEndUrl = String.format("%s/machine-approvals-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), machineApprovalRequest.getId());
        Machine gamingTerminal = machineApprovalRequest.getMachine();
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        Agent agent = gamingTerminal.getAgent();
        HashMap<String, Object> model = new HashMap<>();
        model.put("agentName", agent.getFullName());
        model.put("date", presentDateString);
        model.put("approvalType", convertToTitleCaseIteratingChars(String.valueOf(machineApprovalRequest.getMachineApprovalRequestType())));
        model.put("serialNumber", gamingTerminal.getSerialNumber());
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "machine-approvals/Agent-MachineApproval-Operator-Notification");
    }

    private String buildNewMachineApprovalRequestNotificationMailContent(MachineApprovalRequest machineApprovalRequest) {
        String frontEndUrl = String.format("%s/machine-approvals-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), machineApprovalRequest.getId());
        String initiatorName = machineApprovalRequest.getRequestInitiatorName();
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("approvalType", convertToTitleCaseIteratingChars(String.valueOf(machineApprovalRequest.getMachineApprovalRequestType())));
        model.put("serialNumber", machineApprovalRequest.getMachineRequestSerialNumber());
        model.put("frontEndUrl", frontEndUrl);
        model.put("initiatorName", initiatorName);
        return mailContentBuilderService.build(model, "machine-approvals/NewMachineApprovalRequest");
    }

    private String buildMachineApprovalNotificationOperator(MachineApprovalRequest approvalRequest) {
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("approvalType", convertToTitleCaseIteratingChars(String.valueOf(approvalRequest.getMachineApprovalRequestType())));
        model.put("serialNumber", approvalRequest.getMachineRequestSerialNumber());
        model.put("isApproved", approvalRequest.isApproved());
        model.put("rejectionReason", approvalRequest.getRejectionReason());
        model.put("institutionName", approvalRequest.getInstitutionName());
        return mailContentBuilderService.build(model, "machine-approvals/Machine-ApprovalNotifcation-Operator");
    }

    private String buildMultipleMachineCreateApprovalNotificationOperator(String institutionName, String count) {
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("count", count);
        model.put("frontEndUrl", frontEndPropertyHelper.getFrontEndUrl());
        model.put("initiatorName", institutionName);
        return mailContentBuilderService.build(model, "machine-approvals/NewMultipleMachineApprovalRequest");
    }
}
