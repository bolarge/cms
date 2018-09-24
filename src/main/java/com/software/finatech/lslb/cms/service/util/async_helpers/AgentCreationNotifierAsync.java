package com.software.finatech.lslb.cms.service.util.async_helpers;


import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AgentApprovalRequest;
import com.software.finatech.lslb.cms.service.domain.AgentInstitution;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.FrontEndPropertyHelper;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class AgentCreationNotifierAsync {

    private static final Logger logger = LoggerFactory.getLogger(AgentCreationNotifierAsync.class);

    private MailContentBuilderService mailContentBuilderService;
    private EmailService emailService;
    private AuthInfoService authInfoService;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private FrontEndPropertyHelper frontEndPropertyHelper;

    @Autowired
    public AgentCreationNotifierAsync(MailContentBuilderService mailContentBuilderService,
                                      EmailService emailService,
                                      AuthInfoService authInfoService,
                                      MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                      FrontEndPropertyHelper frontEndPropertyHelper) {
        this.mailContentBuilderService = mailContentBuilderService;
        this.emailService = emailService;
        this.authInfoService = authInfoService;
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.frontEndPropertyHelper = frontEndPropertyHelper;
    }

    @Async
    public void sendEmailNotificationToInstitutionAdminsAndLslbOnAgentRequestCreation(AgentApprovalRequest agentApprovalRequest) {
        List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(agentApprovalRequest.getInstitutionId());
        if (institutionAdmins == null || institutionAdmins.isEmpty()) {
            logger.info("Institution with id {} does not have admins, skipping email notification", agentApprovalRequest.getInstitutionId());
            return;
        }

        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendAgentCreationNotificationEmailToInstitutionAdmin(agentApprovalRequest, institutionAdmin);
        }

        if (agentApprovalRequest.isApprovedRequest() && agentApprovalRequest.isInstitutionAgentAdditionRequest()) {
            sendAgentCreationNotificationToAgent(agentApprovalRequest);
        }


        //TODO::validate lslb admin emails
        sendNewAgentRequestToLslbAdmin(agentApprovalRequest, "lslbcms@gmail.com");
    }


    private void sendAgentCreationNotificationEmailToInstitutionAdmin(AgentApprovalRequest agentApprovalRequest, AuthInfo institutionAdmin) {
        try {
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

            String content = mailContentBuilderService.build(model, "agent-creation-notification");
            emailService.sendEmail(content, "LSLB Agent Creation Notification", institutionAdmin.getEmailAddress());

        } catch (Exception e) {
            logger.error("An error occurred while sending agent creation notification email to user with email -> {}", institutionAdmin.getEmailAddress(), e);
        }
    }

    private void sendNewAgentRequestToLslbAdmin(AgentApprovalRequest agentApprovalRequest, String lslbAdminEmail) {
        try {
            String frontEndUrl = String.format("%s/agent-approval-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), agentApprovalRequest.getId());
            String presentDateString = LocalDate.now().toString("dd-MM-YYYY");
            HashMap<String, Object> model = new HashMap<>();
            model.put("institutionName", agentApprovalRequest.getInstitutionName());
            model.put("date", presentDateString);
            model.put("frontEndUrl", frontEndUrl);

            String content = mailContentBuilderService.build(model, "Lslb-CreateAgent-Notification");
            emailService.sendEmail(content, "LSLB Agent Creation Notification", lslbAdminEmail);
        } catch (Exception e) {
            logger.error("");
        }
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

            String content = mailContentBuilderService.build(model, "Agent-CreateAgent-Notification");
            emailService.sendEmail(content, "LSLB Profile Update", agent.getEmailAddress());
        } catch (Exception e) {
            logger.error("");
        }
    }

}
