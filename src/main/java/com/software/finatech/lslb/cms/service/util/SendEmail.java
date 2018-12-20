package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.dto.NotificationDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class SendEmail {
    @Autowired
    EmailService emailService;
    @Autowired
    MailContentBuilderService mailContentBuilderService;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    public String sendEmailNotification(NotificationDto notificationDto, String subject) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("description", notificationDto.getDescription());
        model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
        model.put("CallbackUrl", notificationDto.getCallBackUrl());
        String content = mailContentBuilderService.build(model, notificationDto.getTemplate());
        content = content.replaceAll("CallbackUrl", notificationDto.getCallBackUrl());
        emailService.sendEmail(content, subject, notificationDto.getInstitutionEmail());
        return "success";
    }

    public String sendEmailRenewalNotification(NotificationDto notificationDto, String subject) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("description", notificationDto.getDescription());
        model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
        String content = mailContentBuilderService.build(model, notificationDto.getTemplate());
        content = content.replaceAll("CallbackUrl", notificationDto.getCallBackUrl());
        emailService.sendEmail(content, subject, notificationDto.getInstitutionEmail());
        return "success";
    }

    public String sendPendingDocumentEmailNotification(NotificationDto notificationDto, String subject) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("description", notificationDto.getDescription());
        model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
        String content = mailContentBuilderService.build(model, "LicenseUpdate");
        emailService.sendEmail(content, subject, notificationDto.getLslbApprovalEmailAddress());
        return "success";
    }

    public String sendEmailLicenseApplicationNotification(NotificationDto notificationDto) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("description", notificationDto.getDescription());
        model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
        model.put("CallbackUrl", notificationDto.getCallBackUrl());
        String content = mailContentBuilderService.build(model, notificationDto.getTemplate());
        content = content.replaceAll("CallbackUrl", notificationDto.getCallBackUrl());
        emailService.sendEmail(content, "Licence Notification", notificationDto.getInstitutionEmail());
        return "success";
    }

    public String sendEmailDeactivationNotification(NotificationDto notificationDto) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("description", notificationDto.getDescription());
        model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
        model.put("CallbackUrl", notificationDto.getCallBackUrl());
        String content = mailContentBuilderService.build(model, notificationDto.getTemplate());
        content = content.replaceAll("CallbackUrl", notificationDto.getCallBackUrl());
        emailService.sendEmail(content, "AGENT DEACTIVATION NOTIFICATION", notificationDto.getAgentEmailAddress());
        return "success";
    }

    public String sendEmailExpiredMachineLicenses(NotificationDto notificationDto) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("description", notificationDto.getDescription());
        model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
        String content = mailContentBuilderService.build(model, notificationDto.getTemplate());
        content = content.replaceAll("CallbackUrl", notificationDto.getCallBackUrl());
        emailService.sendEmail(content, "Gaming Machine/Terminal Licence Expiration Notification", notificationDto.getInstitutionEmail());
        return "success";
    }

}
