package com.software.finatech.lslb.cms.service.util.async_helpers;


import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AgentApprovalRequest;
import com.software.finatech.lslb.cms.service.domain.AgentInstitution;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.AbstractMailSender;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
public class AgentCreationNotifierAsync extends AbstractMailSender {

    private static final Logger logger = LoggerFactory.getLogger(AgentCreationNotifierAsync.class);

    @Async
    public void sendEmailNotificationToInstitutionAdminsAndLslbOnAgentRequestCreation(AgentApprovalRequest agentApprovalRequest) {
        List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(agentApprovalRequest.getInstitutionId());
        if (institutionAdmins == null || institutionAdmins.isEmpty()) {
            logger.info("Institution with id {} does not have admins, skipping email notification", agentApprovalRequest.getInstitutionId());
            return;
        }

        String emailContent = buildAgentCreationNotificationContent(agentApprovalRequest);
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendAgentCreationNotificationEmailToInstitutionAdmin(institutionAdmin.getEmailAddress(), emailContent);
        }

        if (agentApprovalRequest.isApprovedRequest() && agentApprovalRequest.isInstitutionAgentAdditionRequest()) {
            sendAgentCreationNotificationToAgent(agentApprovalRequest);
        }
    }


    @Async
    public void sendNewAgentApprovalRequestToLSLBAdmin(AgentApprovalRequest agentApprovalRequest) {
        List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_AGENT_APPROVAL_AGENT_REQUEST_ID);
        if (lslbAdmins == null || lslbAdmins.isEmpty()) {
            logger.info("No LSLB member can receive new agent approval request , skipping emails");
            return;
        }
        String content = buildNewAgentRequestLSLBAdminContent(agentApprovalRequest);
        for (AuthInfo lslbAdmin : lslbAdmins) {
            sendNewAgentRequestToLslbAdmin(content, lslbAdmin.getEmailAddress());
        }
    }

    private void sendAgentCreationNotificationEmailToInstitutionAdmin(String institutionAdminEmail, String emailContent) {
        try {
            logger.info("Sending agent approval request notification to institution admin with email {}", institutionAdminEmail);
            emailService.sendEmail(emailContent, "LSLB Agent Creation Notification", institutionAdminEmail);
        } catch (Exception e) {
            logger.error("An error occurred while sending agent creation notification email to user with email -> {}", institutionAdminEmail, e);
        }
    }

    private void sendNewAgentRequestToLslbAdmin(String content, String lslbAdminEmail) {
        try {
            logger.info("Sending new Agent approval request to lslb admin with email {}", lslbAdminEmail);
            emailService.sendEmail(content, "New Agent Creation Request on LSLB CMS", lslbAdminEmail);
        } catch (Exception e) {
            logger.error("");
        }
    }

    private String buildNewAgentRequestLSLBAdminContent(AgentApprovalRequest agentApprovalRequest) {
        String frontEndUrl = String.format("%s/agent-approval-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), agentApprovalRequest.getId());
        String presentDateString = LocalDate.now().toString("dd-MM-YYYY");
        HashMap<String, Object> model = new HashMap<>();
        model.put("institutionName", agentApprovalRequest.getInstitutionName());
        model.put("date", presentDateString);
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "agent/Lslb-CreateAgent-Notification");
    }

    private void sendAgentCreationNotificationToAgent(AgentApprovalRequest agentApprovalRequest) {
        try {
            Agent agent = agentApprovalRequest.getAgent();
            String frontEndUrl = String.format("%s/agent-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), agentApprovalRequest.getAgentId());
            List<String> businessAddressList = agentApprovalRequest.getBusinessAddressList();
            String presentDateString = LocalDate.now().toString("dd-MM-YYYY");
            HashMap<String, Object> model = new HashMap<>();
            model.put("institutionName", agentApprovalRequest.getInstitutionName());
            model.put("date", presentDateString);
            model.put("frontEndUrl", frontEndUrl);
            model.put("gameType", agentApprovalRequest.getGameTypeName());
            model.put("businessAddressList", businessAddressList);

            String content = mailContentBuilderService.build(model, "agent/Agent-CreateAgent-Notification");
            emailService.sendEmail(content, "LSLB Profile Update", agent.getEmailAddress());
        } catch (Exception e) {
            logger.error("");
        }
    }

    private String buildAgentCreationNotificationContent(AgentApprovalRequest agentApprovalRequest) {
        Agent agent = agentApprovalRequest.getAgent();
        boolean isApprovedRequest = agentApprovalRequest.isApprovedRequest();
        String institutionName = agentApprovalRequest.getInstitutionName();
        String gameTypeName = agentApprovalRequest.getGameTypeName();
        String rejectionReason = agentApprovalRequest.getRejectionReason();
        String presentDateString = LocalDate.now().toString("dd-MM-YYYY");
        String agentName = agent.getFullName();
        String agentEmail = agent.getEmailAddress();
        List<String> businessAddresses = new ArrayList<>();
        if (agentApprovalRequest.isAgentCreationRequest()) {

            AgentInstitution agentInstitution = agent.getAgentInstitutions().get(0);
            if (agentInstitution != null) {
                businessAddresses = agentInstitution.getBusinessAddressList();
            }
        }

        if (agentApprovalRequest.isInstitutionAgentAdditionRequest()) {
            businessAddresses = agentApprovalRequest.getBusinessAddressList();
        }


        HashMap<String, Object> model = new HashMap<>();
        model.put("institutionName", institutionName);
        model.put("date", presentDateString);
        model.put("agentName", agentName);
        model.put("rejectionReason", rejectionReason);
        model.put("gameType", gameTypeName);
        model.put("isApproved", isApprovedRequest);
        model.put("businessAddressList", businessAddresses);
        model.put("agentEmail", agentEmail);
        return mailContentBuilderService.build(model, "agent/agent-creation-notification-operator");
    }

    @Async
    public void sentAgentUpdateEmailToAgentInstitutions(Agent agent, Pair<String, String> oldPhoneAndAddressPair, Pair<String, String> newPhoneAndAddressPair) {
        Set<String> agentInstitutionIds = agent.getInstitutionIds();
        String mailContent = buildAgentAddressChangeNotificationEmailContent(agent, oldPhoneAndAddressPair, newPhoneAndAddressPair);
        for (String institutionId : agentInstitutionIds) {
            ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(institutionId);
            for (AuthInfo institutionAdmin : institutionAdmins) {
                sendAgentAddressChangeToInstitutionAdmin(mailContent, institutionAdmin.getEmailAddress());
            }
        }
    }

    private String buildAgentAddressChangeNotificationEmailContent(Agent agent, Pair<String, String> oldPhoneAndAddressPair, Pair<String, String> newPhoneAndAddressPair) {
        String oldPhoneNumber = oldPhoneAndAddressPair.getKey();
        String oldResidentialAddress = oldPhoneAndAddressPair.getValue();
        String newPhoneNumber = newPhoneAndAddressPair.getKey();
        String newResidentialAddress = newPhoneAndAddressPair.getValue();
        String agentId = agent.getAgentId();
        String agentFullName = agent.getFullName();
        String frontEndUrl = String.format("%s/agent-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), agent.getId());
        HashMap<String, Object> model = new HashMap<>();
        model.put("oldPhoneNumber", oldPhoneNumber);
        model.put("oldResidentialAddress", oldResidentialAddress);
        model.put("newPhoneNumber", newPhoneNumber);
        model.put("newResidentialAddress", newResidentialAddress);
        model.put("agentId", agentId);
        model.put("agentFullName", agentFullName);
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "agent/Agent-Profile-Update-Operator-Admin");
    }

    private void sendAgentAddressChangeToInstitutionAdmin(String mailContent, String institutionAdminEmail) {
        try {
            logger.info("Sending agent update email to {}", institutionAdminEmail);
            emailService.sendEmail(mailContent, "Agent Update Notification", institutionAdminEmail);
        } catch (Exception e) {
            logger.error(String.format("An error occurred while sending agent update notification to institution admin with email %s", institutionAdminEmail), e);
        }
    }
}
