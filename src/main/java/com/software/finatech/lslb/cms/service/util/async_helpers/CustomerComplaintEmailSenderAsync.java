package com.software.finatech.lslb.cms.service.util.async_helpers;


import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.CustomerComplain;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.util.FrontEndPropertyHelper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class CustomerComplaintEmailSenderAsync {

    private static final Logger logger = LoggerFactory.getLogger(CustomerComplaintEmailSenderAsync.class);

    private MailContentBuilderService mailContentBuilderService;
    private EmailService emailService;
    private FrontEndPropertyHelper frontEndPropertyHelper;

    @Autowired
    public CustomerComplaintEmailSenderAsync(MailContentBuilderService mailContentBuilderService,
                                             EmailService emailService,
                                             FrontEndPropertyHelper frontEndPropertyHelper) {
        this.mailContentBuilderService = mailContentBuilderService;
        this.emailService = emailService;
        this.frontEndPropertyHelper = frontEndPropertyHelper;
    }

    @Async
    public void sendInitialNotificationsForCustomerComplain(CustomerComplain customerComplain) {
        sendNewCustomerComplainEmailToLSLBAdmins(customerComplain);
        sendNewCustomerComplainEmailToCustomer(customerComplain);
    }


    private void sendNewCustomerComplainEmailToLSLBAdmins(CustomerComplain customerComplain) {
        List<AuthInfo> lslbAdminsForCustomerComplain = new ArrayList<>();
        if (lslbAdminsForCustomerComplain.isEmpty()) {
            logger.info("No LSLB Admin has authority to receive customer complain email, skipping lslb admins emails");
            return;
        }
        String mailContent = buildNewCustomerComplaintLSLSBAdminEmailContent(customerComplain);
        for (AuthInfo authInfo : lslbAdminsForCustomerComplain) {
            sendNewCustomerComplainEmailToLSLBAdmin(authInfo.getEmailAddress(), mailContent);
        }
    }

    private void sendNewCustomerComplainEmailToCustomer(CustomerComplain customerComplain) {
        try {
            String ticketId = customerComplain.getTicketId();
            String presentDateString = DateTime.now().toString("dd-MM-yyyy");

            HashMap<String, Object> model = new HashMap<>();
            model.put("date", presentDateString);
            model.put("ticketId", ticketId);
            String mailContent = mailContentBuilderService.build(model, "NewCustomerComplainCustomer");
            logger.info("Sending new customer complain notification to customer with email address {}", customerComplain.getCustomerEmailAddress());
            emailService.sendEmail(mailContent, "LSLB have received your complain", customerComplain.getCustomerEmailAddress());
        } catch (Exception e) {
            logger.error(String.format("An error occurred while sending customer complain acknowledgement to customer with email %s", customerComplain.getCustomerEmailAddress()), e);
        }
    }

    private String buildNewCustomerComplaintLSLSBAdminEmailContent(CustomerComplain customerComplain) {
        String customerNameEmail = String.format("%s(%s)", customerComplain.getCustomerFullName(), customerComplain.getCustomerEmailAddress());
        String frontEndUrl = String.format("%s/customer-complains/%s", frontEndPropertyHelper.getFrontEndUrl(), customerComplain.getId());
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");

        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("customerNameEmail", customerNameEmail);
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "NewCustomerComplainLSLBAdmin");
    }

    private void sendNewCustomerComplainEmailToLSLBAdmin(String lslbAdminEmailAddress, String mailContent) {
        try {
            logger.info("Sending new customer complain to -> {}", lslbAdminEmailAddress);
            String mailSubject = "New Customer Complain on LSLB CMS";
            emailService.sendEmail(mailContent, mailSubject, lslbAdminEmailAddress);
        } catch (Exception e) {
            logger.error(String.format("An error occurred while sending new customer complaint email to LSLB Admin -> %s", lslbAdminEmailAddress), e);
        }
    }
}
