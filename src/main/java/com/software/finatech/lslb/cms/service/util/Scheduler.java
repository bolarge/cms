package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.dto.NotificationDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.PaymentRecordServiceImpl;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler {
    @Autowired
    EmailService emailService;
    @Autowired
    MapValues mapValues;
    @Autowired
    MailContentBuilderService mailContentBuilderService;
    @Autowired
    ExpirationList expirationList;
    @Value("email-username")
    String adminEmail;
    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    LocalDateTime dateTime = new LocalDateTime();


    @Scheduled(cron = "0 0 12 * * ?")
    protected void checkForLicensesCloseToExpirations(){
        List<License> licenses= (List<License>)expirationList.getExpiringLicences("schedulerClass");
       List<NotificationDto> notificationDtos= new ArrayList<>();
        LocalDateTime endDate;
        dateTime.plusDays(90);
        for(License license: licenses){
            int days=0;
            NotificationDto notificationDto= new NotificationDto();
            endDate=license.getEndDate();
            days= Days.daysBetween(dateTime,endDate).getDays();
            notificationDto.setDaysToExpiration(days);
            GameType gameType = mapValues.getGameType(license.getGameTypeId());
            notificationDto.setGameType(gameType.getName());
            notificationDto.setInstitutionId(license.getInstitutionId());
            notificationDto.setEndDate(endDate.toString("dd/MM/yyyy HH:mm:ss"));
            Institution institution=(Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
                    Institution.class).block();
            notificationDto.setInstitutionName(institution.getInstitutionName());
            notificationDto.setInstitutionEmail(institution.getEmailAddress());
            notificationDtos.add(notificationDto);
        }
        sendEmailNotification(notificationDtos,"expiring");
    }

    private void sendEmailNotification(List<NotificationDto> notificationDtos, String type) {
        for(NotificationDto notificationDto: notificationDtos){
            HashMap<String, Object> model = new HashMap<>();
            model.put("institutionId", notificationDto.getInstitutionId());
            model.put("institutionName", notificationDto.getInstitutionName());
            model.put("endDate", notificationDto.getEndDate());
            if(type=="expiring"){
                model.put("description", "Your "+notificationDto.getGameType()+" License will expire in "+notificationDto.getDaysToExpiration()+" days");
            }else if(type=="expired"){
                model.put("description", "Your "+notificationDto.getGameType()+" License has expired");
            }
            model.put("gameType", notificationDto.getGameType());
            model.put("date", LocalDate.now().toString("dd-MM-YYYY"));

            String content = mailContentBuilderService.build(model, "LicenseExpiration");
            emailService.sendEmail(content,"License Expiration Notification", notificationDto.getInstitutionEmail());
            //emailService.sendEmail(content,""+notificationDto.getInstitutionName()+" License Expiration Notification", adminEmail);

        }
    }

    @Scheduled(cron = "0 0 10 * * ?")
    protected void deactivateInstitutionsWithExpiredLicense(){

        List<License> licenses= (List<License>)expirationList.getExpiredLicences("schedulerClass");
        List<NotificationDto> notificationDtos= new ArrayList<>();
        for(License license: licenses){
            NotificationDto notificationDto= new NotificationDto();
            LocalDateTime endDate=license.getEndDate();
            GameType gameType = mapValues.getGameType(license.getGameTypeId());
            notificationDto.setGameType(gameType.getName());
            notificationDto.setInstitutionId(license.getInstitutionId());
            notificationDto.setEndDate(endDate.toString("dd/MM/yyyy HH:mm:ss"));
            Institution institution=(Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
                    Institution.class).block();
            notificationDto.setInstitutionName(institution.getInstitutionName());
            notificationDto.setInstitutionEmail(institution.getEmailAddress());
            notificationDtos.add(notificationDto);
        }
        sendEmailNotification(notificationDtos,"expired");
    }
}
