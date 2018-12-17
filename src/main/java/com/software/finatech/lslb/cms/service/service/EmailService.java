package com.software.finatech.lslb.cms.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;

@Component("emailService")
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Async("threadPoolTaskExecutor")
    public void sendEmail(String content, String subject, String to) {
        //mailSender.send(email);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("noreply@lslbcms.com");
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
        };
        try {
            mailSender.send(messagePreparator);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }


    @Async("threadPoolTaskExecutor")
    public void sendEmailWithAttachment(String content, String subject, String to, File file) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("noreply@lslbcms.com");
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

   /* @Async("threadPoolTaskExecutor")
    public void sendEmail(SimpleMailMessage email) {
        mailSender.send(email);
    }*/
}
