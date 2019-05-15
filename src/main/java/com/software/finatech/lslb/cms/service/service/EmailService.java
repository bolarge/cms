package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.FailedEmailNotification;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.util.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.util.UUID;

@Component("emailService")
public class EmailService {

    //    @Autowired
//    private SendGrid sendGrid;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    private EnvironmentUtils environmentUtils;


    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Async("threadPoolTaskExecutor")
    public void sendEmail(String content, String subject, String to) {
        //mailSender.send(email);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(getMailSender());
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
        };

        try {
            //   sendSendGridEmail(content, subject, to);
            mailSender.send(messagePreparator);
        } catch (Throwable e) {
            FailedEmailNotification failedEmailNotification = new FailedEmailNotification();
            failedEmailNotification.setId(UUID.randomUUID().toString());
            failedEmailNotification.setExceptionMessage(e.getMessage());
            failedEmailNotification.setContent(content);
            failedEmailNotification.setFromEmailAddress(getMailSender());
            failedEmailNotification.setSent(false);
            failedEmailNotification.setToEmailAddress(to);
            failedEmailNotification.setSubject(subject);
            mongoRepositoryReactive.saveOrUpdate(failedEmailNotification);
            logger.error("An error occurred while sending mail", e);
        }
    }

    public void sendFailedEmail(FailedEmailNotification failedEmailNotification) {
        String toEmailAddress = failedEmailNotification.getToEmailAddress();
        if (!isValidEmailAddress(toEmailAddress)) {
            mongoRepositoryReactive.delete(failedEmailNotification);
            logger.info("Invalid Email Address ==> {}", toEmailAddress);
            return;
        }
        failedEmailNotification.setProcessing(true);
        mongoRepositoryReactive.saveOrUpdate(failedEmailNotification);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(getMailSender());
            messageHelper.setTo(failedEmailNotification.getToEmailAddress());
            messageHelper.setSubject(failedEmailNotification.getSubject());
            messageHelper.setText(failedEmailNotification.getContent(), true);
        };
        try {
            mailSender.send(messagePreparator);
            failedEmailNotification.setProcessing(false);
            failedEmailNotification.setSent(true);
            mongoRepositoryReactive.delete(failedEmailNotification);
        } catch (Throwable e) {
            failedEmailNotification.setSent(false);
            failedEmailNotification.setProcessing(false);
            failedEmailNotification.setExceptionMessage(e.getMessage());
            logger.error("An error occurred while sending mail", e);
            mongoRepositoryReactive.saveOrUpdate(failedEmailNotification);
        }
    }


    @Async("threadPoolTaskExecutor")
    public void sendEmailWithAttachment(String content, String subject, String to, File file) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom(getMailSender());
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            messageHelper.addAttachment(file.getName(), file);
        };
        try {
            mailSender.send(messagePreparator);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

    /*
    private void sendSendGridEmail(String contentString, String subject, String toEmailAddress) throws Exception {
        try {
            Email fromEmail = new Email("noreply@lslbcms.com");
            Email toEmail = new Email(toEmailAddress);
            Content content = new Content("text/html", contentString);

            Mail mail = new Mail(fromEmail, subject, toEmail, content);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            logger.info(response.getBody());
            logger.info("{}", response.getStatusCode());
            if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                throw new Exception(response.getBody());
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }
*/

    private boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private String getMailSender() {
        if (environmentUtils.isDevelopmentEnvironment() ||
                environmentUtils.isStagingEnvironment() ||
                environmentUtils.isTestEnvironment()) {
            return "dev@lslbcms.com";
        }

        if (environmentUtils.isProductionEnvironment()) {
            return "no-reply@lslbcms.com";
        }
        return "";
    }
}
