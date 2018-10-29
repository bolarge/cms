package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Machine;
import com.software.finatech.lslb.cms.service.domain.MachineApprovalRequest;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

import static com.software.finatech.lslb.cms.service.util.StringCapitalizer.convertToTitleCaseIteratingChars;

@Component
public class MachineApprovalRequestMailSenderAsync extends AbstractMailSender {
    private static final Logger logger = LoggerFactory.getLogger(MachineApprovalRequestMailSenderAsync.class);

    @Async
    public void sendInitialMachineStateUpdateToAgentOperators(MachineApprovalRequest machineApprovalRequest, String newStatusName) {
        Machine gamingTerminal = machineApprovalRequest.getMachine();
        ArrayList<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(gamingTerminal.getInstitutionId());
        String content = buildInitialMachineStateChangeOperatorNotification(machineApprovalRequest, gamingTerminal, newStatusName);
        for (AuthInfo admin : institutionAdmins) {
            String email = admin.getEmailAddress();
            try {
                emailService.sendEmail(content, "Gaming Terminal Updated ", email);
            } catch (Exception e) {
                logger.error("An error occurred while sending mail to institution admin {}", email, e);
            }
        }
    }

    private String buildInitialMachineStateChangeOperatorNotification(MachineApprovalRequest machineApprovalRequest, Machine gamingTerminal, String newStatusName) {
        String frontEndUrl = String.format("%s/machine-approvals-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), machineApprovalRequest.getId());
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        Agent agent = gamingTerminal.getAgent();
        HashMap<String, Object> model = new HashMap<>();
        model.put("agentName", agent.getFullName());
        model.put("date", presentDateString);
        model.put("newState", convertToTitleCaseIteratingChars(newStatusName));
        model.put("oldState", convertToTitleCaseIteratingChars(gamingTerminal.getMachineStatus().toString()));
        model.put("serialNumber", gamingTerminal.getSerialNumber());
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "machine-approvals/Agent-ChangeMachine-Operator-Notification");
    }
}
