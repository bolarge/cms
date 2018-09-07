package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.NotificationDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.PaymentRecordServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Scheduler {

    @Autowired
    MapValues mapValues;
    @Autowired
    EmailService emailService;
    @Autowired
    MailContentBuilderService mailContentBuilderService;
    @Autowired
    ExpirationList expirationList;

    @Value("email-username")
    String adminEmail;
    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }
    LocalDateTime dateTime = new LocalDateTime();


    @Scheduled(cron = "0 0 4 * * ?")
    protected void checkForLicensesCloseToExpirations(){
        logger.info(" checkForLicensesCloseToExpirations");
        List<License> licenses=
                expirationList.getExpiringLicences(90,LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
       List<NotificationDto> notificationDtos= new ArrayList<>();
        LocalDate endDate;
        dateTime=dateTime.plusMonths(3);
        if(licenses!=null){
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

                notificationDto.setGameType(gameType.getDescription());
                notificationDto.setInstitutionId(license.getInstitutionId());
                notificationDto.setEndDate(endDate.toString("dd/MM/yyyy"));
                Institution institution=(Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),Institution.class).block();
                if(institution!=null){
                    notificationDto.setInstitutionName(institution.getInstitutionName());
                    notificationDto.setInstitutionEmail(institution.getEmailAddress());

                }
                Agent agent=(Agent) mongoRepositoryReactive.findById(license.getAgentId(),Agent.class).block();
                if(agent!=null){
                    notificationDto.setAgentFullName(agent.getFullName());
                    notificationDto.setAgentEmailAddress(agent.getEmailAddress());

                }
                GamingMachine gamingMachine=(GamingMachine) mongoRepositoryReactive.findById(license.convertToDto().getGamingMachineId(),GamingMachine.class).block();
                if(gamingMachine!=null){
                    notificationDto.setMachineNumber(gamingMachine.getMachineNumber());

                }

                notificationDtos.add(notificationDto);
            }
            sendEmailNotification(notificationDtos,"expiring");
        }

    }
    @Scheduled(cron = "0 0 3 * * ?")
    protected void checkForAIPCloseToExpirations(){

        logger.info("checkForAIPCloseToExpirations");
        List<License> licenses= expirationList.getExpiringLicences(14,LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        List<NotificationDto> notificationDtos= new ArrayList<>();
        LocalDate endDate;
        dateTime=dateTime.plusDays(14);
        if(licenses!=null){
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
                notificationDto.setGameType(gameType.getDescription());
                notificationDto.setInstitutionId(license.getInstitutionId());
                notificationDto.setEndDate(endDate.toString("dd/MM/yyyy"));
                Institution institution=(Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
                        Institution.class).block();
                notificationDto.setInstitutionName(institution.getInstitutionName());
                notificationDto.setInstitutionEmail(institution.getEmailAddress());
                notificationDtos.add(notificationDto);
            }
            sendEmailNotification(notificationDtos,"AIPExpiring");
        }

    }

    private void sendEmailNotification(List<NotificationDto> notificationDtos, String type) {
        for(NotificationDto notificationDto: notificationDtos){
            HashMap<String, Object> model = new HashMap<>();
            model.put("institutionId", notificationDto.getInstitutionId());
            model.put("institutionName", notificationDto.getInstitutionName());
            model.put("endDate", notificationDto.getEndDate());
            if(type=="expiring"){
                if(!StringUtils.isEmpty(notificationDto.getGamingMachineId())){
                    model.put("description", notificationDto.getInstitutionName()+" Gaming Machine with machine number: "+notificationDto.getMachineNumber()+" License is due to expire on "+notificationDto.getEndDate());

                }else if(!StringUtils.isEmpty(notificationDto.getAgentId())){
                    model.put("description", notificationDto.getInstitutionName()+" Agent: "+notificationDto.getAgentFullName()+" License is due to expire on "+notificationDto.getEndDate());

                }else{
                    model.put("description", notificationDto.getInstitutionName()+" with Game Type: "+notificationDto.getGameType()+" License is due to expire on "+notificationDto.getEndDate());

                }
            }else if(type=="expired"){
                if(!StringUtils.isEmpty(notificationDto.getGamingMachineId())) {
                    model.put("description", notificationDto.getInstitutionName()+" Gaming Machine with machine number: "+notificationDto.getMachineNumber() + " License has expired. License Expiration Date is "+notificationDto.getEndDate());
                }
                if(!StringUtils.isEmpty(notificationDto.getAgentId())) {
                    model.put("description", notificationDto.getInstitutionName()+" Agent: "+notificationDto.getAgentFullName()+ " License is due to expire on "+notificationDto.getEndDate());
                }
                else{
                    model.put("description", notificationDto.getInstitutionName()+" with Game Type: "+notificationDto.getGameType()+" License has expired. License Expiration Date is "+notificationDto.getEndDate());

                }
                }else if(type=="AIPExpired"){
                model.put("description", notificationDto.getInstitutionName()+" "+notificationDto.getGameType()+" AIP has expired");
            }else if(type=="AIPExpiring"){
                model.put("description", notificationDto.getInstitutionName()+" "+notificationDto.getGameType()+" AIP is due to expire on "+notificationDto.getEndDate());
            }

            model.put("gameType", notificationDto.getGameType());
            model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
            String content = mailContentBuilderService.build(model, "LicenseExpiration");

           if((type=="AIPExpired")||(type=="AIPExpiring")){
               emailService.sendEmail(content,"AIP Expiration Notification", "elohor.evwrujae@venturegardengroup.com");
               emailService.sendEmail(content,"AIP Expiration Notification", notificationDto.getInstitutionEmail());

           }else{
               if(!StringUtils.isEmpty(notificationDto.getAgentId())){
                   emailService.sendEmail(content, "Licence Expiration Notification", notificationDto.getAgentEmailAddress());

               }
                   emailService.sendEmail(content, "Licence Expiration Notification", "elohor.evwrujae@venturegardengroup.com");
                   emailService.sendEmail(content, "Licence Expiration Notification", notificationDto.getInstitutionEmail());

           }

        }
    }
    //@Scheduled(cron = "0 0/1 * * * ?")
    @Scheduled(cron = "0 0 4 * * ?")
    protected void deactivateInstitutionsWithExpiredLicense(){

        List<License> licenses= expirationList.getExpiredLicences(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        List<NotificationDto> notificationDtos= new ArrayList<>();
        if(licenses!=null){
            for(License license: licenses){
                NotificationDto notificationDto= new NotificationDto();
                LocalDate endDate=license.getEndDate();
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
                } notificationDto.setGameType(gameType.getDescription());
                notificationDto.setInstitutionId(license.getInstitutionId());
                notificationDto.setEndDate(endDate.toString("dd/MM/yyyy"));
                Institution institution=(Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
                        Institution.class).block();
                notificationDto.setInstitutionName(institution.getInstitutionName());
                notificationDto.setInstitutionEmail(institution.getEmailAddress());
                Agent agent=(Agent) mongoRepositoryReactive.findById(license.getAgentId(),Agent.class).block();
                if(agent!=null){
                    notificationDto.setAgentFullName(agent.getFullName());
                    notificationDto.setAgentEmailAddress(agent.getEmailAddress());

                }
                GamingMachine gamingMachine=(GamingMachine) mongoRepositoryReactive.findById(license.convertToDto().getGamingMachineId(),GamingMachine.class).block();
                if(gamingMachine!=null){
                    notificationDto.setMachineNumber(gamingMachine.getMachineNumber());

                }
                notificationDtos.add(notificationDto);

                license.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID);

                mongoRepositoryReactive.saveOrUpdate(license);
            }
            sendEmailNotification(notificationDtos,"expired");
        }
    }


    @Scheduled(cron = "0 0 3 * * ?")
    protected void WithExpiredAIP(){

        List<License> licenses= expirationList.getExpiredLicences(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        List<NotificationDto> notificationDtos= new ArrayList<>();
        if(licenses!=null){
            for(License license: licenses){
                NotificationDto notificationDto= new NotificationDto();
                LocalDate endDate=license.getEndDate();
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
                } notificationDto.setGameType(gameType.getDescription());
                notificationDto.setInstitutionId(license.getPaymentRecord().getInstitutionId());
                notificationDto.setEndDate(endDate.toString("dd/MM/yyyy"));
                Institution institution=(Institution) mongoRepositoryReactive.findById(license.getPaymentRecord().getInstitutionId(),
                        Institution.class).block();
                notificationDto.setInstitutionName(institution.getInstitutionName());
                notificationDto.setInstitutionEmail(institution.getEmailAddress());
                notificationDtos.add(notificationDto);

                license.setLicenseStatusId("01");
                mongoRepositoryReactive.saveOrUpdate(license);
            }
            sendEmailNotification(notificationDtos,"AIPExpired");
        }
    }
}
