package com.software.finatech.lslb.cms.service.util.async_helpers;


import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AgentCreationNotifierAsync {

    private static final Logger logger = LoggerFactory.getLogger(AgentCreationNotifierAsync.class);

    private MailContentBuilderService mailContentBuilderService;
    private EmailService emailService;
    private AuthInfoService authInfoService;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public AgentCreationNotifierAsync(MailContentBuilderService mailContentBuilderService,
                                      EmailService emailService,
                                      AuthInfoService authInfoService,
                                      MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mailContentBuilderService = mailContentBuilderService;
        this.emailService = emailService;
        this.authInfoService = authInfoService;
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    public void sendEmailNotificationToInstitutionAdminsOnAgentRequestCreation(AgentApprovalRequest agentApprovalRequest) {
        List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(agentApprovalRequest.getInstitutionId());
        if (institutionAdmins == null || institutionAdmins.isEmpty()) {
            logger.info("Institution with id {} does not have admins, skipping email notification", agentApprovalRequest.getInstitutionId());
            return;
        }

        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendAgentCreationNotificationEmailToInstitutionAdmin(agentApprovalRequest, institutionAdmin);
        }
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
}
