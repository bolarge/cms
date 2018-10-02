package com.software.finatech.lslb.cms.service.util.async_helpers;


import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
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
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NewUserEmailNotifierAsync {

    private static final Logger logger = LoggerFactory.getLogger(NewUserEmailNotifierAsync.class);

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private MailContentBuilderService mailContentBuilderService;
    private EmailService emailService;

    @Autowired
    public NewUserEmailNotifierAsync(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                     MailContentBuilderService mailContentBuilderService,
                                     EmailService emailService) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.mailContentBuilderService = mailContentBuilderService;
        this.emailService = emailService;
    }


    @Async
    public void sendNewSSOClientAdminNotificationToVGGAdmins(AuthInfo newSSoClientAdmin) {
        List<AuthInfo> vggAdminList = getAllActiveVGGAdmins();
        if (vggAdminList == null || vggAdminList.isEmpty()){
            logger.info("No VGG Admins found, skipping email");
            return;
        }
        for (AuthInfo vggAdmin:vggAdminList) {
            sendNewSSOClientAdminNotification(vggAdmin, newSSoClientAdmin);
        }
    }

    private void sendNewSSOClientAdminNotification(AuthInfo vggAdminUser, AuthInfo newSSOClientAdmin) {
        try {
            String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
            HashMap<String, Object> model = new HashMap<>();
            model.put("name", vggAdminUser.getFirstName());
            model.put("date", presentDateString);
            model.put("userEmail", newSSOClientAdmin.getEmailAddress());
            model.put("userFullName", newSSOClientAdmin.getFullName());
            String content = mailContentBuilderService.build(model, "VGG-Admin-SSO-Client-Admin-Notification");
            emailService.sendEmail(content, "New SSO Client Admin User on LSLB CMS", vggAdminUser.getEmailAddress());
        } catch (Exception e) {
            logger.error("An error occurred while sending email to user -> {}", vggAdminUser.getEmailAddress(), e);
        }
    }


    private List<AuthInfo> getAllActiveVGGAdmins() {
        Query query = new Query();
        query.addCriteria(Criteria.where("enabled").is(true));
        query.addCriteria(Criteria.where("authRoleId").is(AuthRoleReferenceData.VGG_ADMIN_ID));
        return (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());
    }
}
