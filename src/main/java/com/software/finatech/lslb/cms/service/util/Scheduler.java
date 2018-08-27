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
        List<License> licenses= (List<License>)expirationList.getExpiringLicences("schedulerClass",90,"02");
       List<NotificationDto> notificationDtos= new ArrayList<>();
        LocalDateTime endDate;
        dateTime=dateTime.plusDays(90);
        for(License license: licenses){
            int days=0;
            NotificationDto notificationDto= new NotificationDto();
            endDate=license.getEndDate();
            days= Days.daysBetween(dateTime,endDate).getDays();
            notificationDto.setDaysToExpiration(days);
            Map gameTypeMap = Mapstore.STORE.get("GameType");
            GameType gameType = null;
            if (gameTypeMap != null) {
                gameType = (GameType) gameTypeMap.get(license.getGameTypeId());
            }
            if (gameType == null) {
                gameType = (GameType) mongoRepositoryReactive.findById(license.getGameTypeId(), GameType.class).block();
                if (gameType != null && gameTypeMap != null) {
                    gameTypeMap.put(license.getGameTypeId(), gameType);
                }
            }
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
    @Scheduled(cron = "0 0 9 * * ?")
    protected void checkForAIPCloseToExpirations(){
        List<License> licenses= (List<License>)expirationList.getExpiringLicences("schedulerClass",14,"02");
        List<NotificationDto> notificationDtos= new ArrayList<>();
        LocalDateTime endDate;
        dateTime=dateTime.plusDays(14);
        for(License license: licenses){
            int days=0;
            NotificationDto notificationDto= new NotificationDto();
            endDate=license.getEndDate();
            days= Days.daysBetween(dateTime,endDate).getDays();
            notificationDto.setDaysToExpiration(days);
            Map gameTypeMap = Mapstore.STORE.get("GameType");
            GameType gameType = null;
            if (gameTypeMap != null) {
                gameType = (GameType) gameTypeMap.get(license.getGameTypeId());
            }
            if (gameType == null) {
                gameType = (GameType) mongoRepositoryReactive.findById(license.getGameTypeId(), GameType.class).block();
                if (gameType != null && gameTypeMap != null) {
                    gameTypeMap.put(license.getGameTypeId(), gameType);
                }
            }
            notificationDto.setGameType(gameType.getName());
            notificationDto.setInstitutionId(license.getInstitutionId());
            notificationDto.setEndDate(endDate.toString("dd/MM/yyyy HH:mm:ss"));
            Institution institution=(Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
                    Institution.class).block();
            notificationDto.setInstitutionName(institution.getInstitutionName());
            notificationDto.setInstitutionEmail(institution.getEmailAddress());
            notificationDtos.add(notificationDto);
        }
        sendEmailNotification(notificationDtos,"AIPExpiring");
    }

    private void sendEmailNotification(List<NotificationDto> notificationDtos, String type) {
        for(NotificationDto notificationDto: notificationDtos){
            HashMap<String, Object> model = new HashMap<>();
            model.put("institutionId", notificationDto.getInstitutionId());
            model.put("institutionName", notificationDto.getInstitutionName());
            model.put("endDate", notificationDto.getEndDate());
            if(type=="expiring"){
                model.put("description", notificationDto.getInstitutionName()+" "+notificationDto.getGameType()+" License will expire in "+notificationDto.getDaysToExpiration()+" days");
            }else if(type=="expired"){
                model.put("description", notificationDto.getInstitutionName()+" "+notificationDto.getGameType()+" License has expired");
            }else if(type=="AIPExpired"){
                model.put("description", notificationDto.getInstitutionName()+" "+notificationDto.getGameType()+" AIP has expired");
            }else if(type=="AIPExpiring"){
                model.put("description", notificationDto.getInstitutionName()+" "+notificationDto.getGameType()+" AIP will expire in"+notificationDto.getDaysToExpiration()+" days. Do contact LSLB Admin to confirm your license");
            }
            model.put("gameType", notificationDto.getGameType());
            model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
            String content = mailContentBuilderService.build(model, "LicenseExpiration");
           if((type=="AIPExpired")||(type=="AIPExpiring")){
               emailService.sendEmail(content,"AIP Expiration Notification", notificationDto.getInstitutionEmail());
               emailService.sendEmail(content,"AIP Expiration Notification", adminEmail);

           }else{
               emailService.sendEmail(content,"Licence Expiration Notification", notificationDto.getInstitutionEmail());
               emailService.sendEmail(content,"Licence Expiration Notification", adminEmail);

           }

        }
    }

    @Scheduled(cron = "0 0 11 * * ?")
    protected void deactivateInstitutionsWithExpiredLicense(){

        List<License> licenses= (List<License>)expirationList.getExpiredLicences("schedulerClass","02");
        List<NotificationDto> notificationDtos= new ArrayList<>();
        for(License license: licenses){
            NotificationDto notificationDto= new NotificationDto();
            LocalDateTime endDate=license.getEndDate();
            GameType gameType = null;
            Map gameTypeMap = Mapstore.STORE.get("GameType");
            if (gameTypeMap != null) {
                gameType = (GameType) gameTypeMap.get(license.getGameTypeId());
            }
            if (gameType == null) {
                gameType = (GameType) mongoRepositoryReactive.findById(license.getGameTypeId(), GameType.class).block();
                if (gameType != null && gameTypeMap != null) {
                    gameTypeMap.put(license.getGameTypeId(), gameType);
                }
            } notificationDto.setGameType(gameType.getName());
            notificationDto.setInstitutionId(license.getInstitutionId());
            notificationDto.setEndDate(endDate.toString("dd/MM/yyyy HH:mm:ss"));
            Institution institution=(Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
                    Institution.class).block();
            notificationDto.setInstitutionName(institution.getInstitutionName());
            notificationDto.setInstitutionEmail(institution.getEmailAddress());
            notificationDtos.add(notificationDto);

            license.setLicenseStatusId("03");
            mongoRepositoryReactive.saveOrUpdate(license);
        }
        sendEmailNotification(notificationDtos,"expired");
    }


    @Scheduled(cron = "0 0 10 * * ?")
    protected void WithExpiredAIP(){

        List<License> licenses= (List<License>)expirationList.getExpiredLicences("schedulerClass","01");
        List<NotificationDto> notificationDtos= new ArrayList<>();
        for(License license: licenses){
            NotificationDto notificationDto= new NotificationDto();
            LocalDateTime endDate=license.getEndDate();
            GameType gameType = null;
            Map gameTypeMap = Mapstore.STORE.get("GameType");
            if (gameTypeMap != null) {
                gameType = (GameType) gameTypeMap.get(license.getGameTypeId());
            }
            if (gameType == null) {
                gameType = (GameType) mongoRepositoryReactive.findById(license.getGameTypeId(), GameType.class).block();
                if (gameType != null && gameTypeMap != null) {
                    gameTypeMap.put(license.getGameTypeId(), gameType);
                }
            } notificationDto.setGameType(gameType.getName());
            notificationDto.setInstitutionId(license.getInstitutionId());
            notificationDto.setEndDate(endDate.toString("dd/MM/yyyy HH:mm:ss"));
            Institution institution=(Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
                    Institution.class).block();
            notificationDto.setInstitutionName(institution.getInstitutionName());
            notificationDto.setInstitutionEmail(institution.getEmailAddress());
            notificationDtos.add(notificationDto);

            license.setLicenseStatusId("03");
            mongoRepositoryReactive.saveOrUpdate(license);
        }
        sendEmailNotification(notificationDtos,"AIPExpired");
    }
}
