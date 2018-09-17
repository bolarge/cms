package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
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
    public String sendEmailLicenseApplicationNotification(NotificationDto notificationDto) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("institutionId", notificationDto.getInstitutionId());
        model.put("institutionName", notificationDto.getInstitutionName());
        model.put("description", notificationDto.getDescription());
        model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
        String content = mailContentBuilderService.build(model, "LicenseExpiration");
        emailService.sendEmail(content, "Licence Update Notification", notificationDto.getInstitutionEmail());
        return "success";
    }

}
