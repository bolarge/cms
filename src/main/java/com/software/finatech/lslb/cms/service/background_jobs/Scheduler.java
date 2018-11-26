package com.software.finatech.lslb.cms.service.background_jobs;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.DocumentSummaryDto;
import com.software.finatech.lslb.cms.service.dto.NotificationDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.AuthInfoServiceImpl;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.PaymentRecordServiceImpl;
import com.software.finatech.lslb.cms.service.util.*;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    SendEmail sendEmail;

    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private AuthInfoServiceImpl authInfoService;

    private static final int ONE_HOUR = 360 * 60 * 1000;

    @Autowired
    private FrontEndPropertyHelper frontEndPropertyHelper;
    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }
    LocalDateTime dateTime = new LocalDateTime();


    @Scheduled(cron = "0 0 6 * * *")
    @SchedulerLock(name = "Check For Licenses Close To Expirations", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void checkForLicensesCloseToExpirations(){
        logger.info(" checkForLicensesCloseToExpirations");
        ArrayList<String> licenseStatuses= new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);

        List<License> licenses=
                expirationList.getExpiringLicences(90,licenseStatuses);
        List<NotificationDto> notificationDtos= new ArrayList<>();
        LocalDate endDate;
        dateTime=dateTime.plusMonths(3);
        if(licenses!=null){
            for(License license: licenses){
                boolean check=false;
                int days_diff=0;
                if(license.getLastSentExpiryEmailDate()==null){
                    check=true;
                    license.setLastSentExpiryEmailDate(LocalDate.now());
                }else{
                    days_diff= Days.daysBetween(license.getLastSentExpiryEmailDate(),LocalDate.now().plusDays(7)).getDays();
                    if(days_diff>0){
                        check=true;
                    }
                }
                if(check==true){
                    int days=0;
                    NotificationDto notificationDto= new NotificationDto();
                    endDate=license.getExpiryDate();
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
                    Machine gamingMachine=(Machine) mongoRepositoryReactive.findById(license.convertToDto().getGamingMachineId(),Machine.class).block();
                    if(gamingMachine!=null){
                        notificationDto.setMachineNumber(gamingMachine.getSerialNumber());
                    }
                    notificationDto.setTemplate("LicenseUpdate");
                    notificationDtos.add(notificationDto);
                }
                sendEmailNotification(notificationDtos,"expiring");
            }

        }

    }
    @Scheduled(cron = "0 0 6 * * *")
    @SchedulerLock(name = "Check For AIP Close To Expiration", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void checkForAIPCloseToExpirations(){

        logger.info("checkForAIPCloseToExpirations");
        ArrayList<String> licenseStatuses= new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        licenseStatuses.add(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID);
        List<License> licenses= expirationList.getExpiringLicences(14, licenseStatuses);
        List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_AIP_ID);
        if (lslbAdmins.size() != 0) {
            lslbAdmins.stream().forEach(lslbAdmin -> {

                List<NotificationDto> notificationDtos = new ArrayList<>();
                LocalDate endDate;
                dateTime = dateTime.plusDays(14);
                if (licenses != null) {
                    for (License license : licenses) {
                        int days = 0;
                        NotificationDto notificationDto = new NotificationDto();
                        endDate = license.getExpiryDate();
                        days = Days.daysBetween(dateTime, endDate).getDays();
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
                        Institution institution = (Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
                                Institution.class).block();
                        notificationDto.setInstitutionName(institution.getInstitutionName());
                        notificationDto.setInstitutionEmail(institution.getEmailAddress());
                        notificationDto.setTemplate("LicenseUpdate");
                        notificationDtos.add(notificationDto);
                    }
                    sendEmailNotification(notificationDtos, "AIPExpiring");
                }
            });
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
                model.put("description", notificationDto.getInstitutionName()+" "+notificationDto.getGameType()+" AIP period has ended");
            }else if(type=="AIPExpiring"){
                model.put("description", notificationDto.getInstitutionName()+" "+notificationDto.getGameType()+" AIP period is due to end on "+notificationDto.getEndDate());
            }

            model.put("gameType", notificationDto.getGameType());
            model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
            String content = mailContentBuilderService.build(model, notificationDto.getTemplate());
            List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_AIP_ID);

            if((type=="AIPExpired")||(type=="AIPExpiring")){
                if (lslbAdmins.size() != 0) {
                    lslbAdmins.stream().forEach(lslbAdmin -> {

                        emailService.sendEmail(content, "AIP Expiration Notification", lslbAdmin.getEmailAddress());
                        emailService.sendEmail(content, "AIP Expiration Notification", notificationDto.getInstitutionEmail());
                    });
            }else {
                    if (!StringUtils.isEmpty(notificationDto.getAgentId())) {
                        emailService.sendEmail(content, "Licence Expiration Notification", notificationDto.getAgentEmailAddress());

                    }
                    if (lslbAdmins.size() != 0) {
                        lslbAdmins.stream().forEach(lslbAdmin -> {

                            emailService.sendEmail(content, "Licence Expiration Notification", lslbAdmin.getEmailAddress());
                            emailService.sendEmail(content, "Licence Expiration Notification", notificationDto.getInstitutionEmail());
                        });

                    }
                }
        }
    }
    }

    @Scheduled(cron = "0 0 6 * * *")
    @SchedulerLock(name = "Check For Expired Licenses", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void InstitutionsWithExpiredLicense(){
        ArrayList<String> licenseStatuses= new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        List<License> licenses= expirationList.getExpiredLicences(licenseStatuses);
        List<NotificationDto> notificationDtos= new ArrayList<>();
        if(licenses!=null){
            for(License license: licenses) {
                boolean check = false;
                int days_diff = 0;
                if (license.getLastSentExpiryEmailDate() == null) {
                    check = true;
                    license.setLastSentExpiryEmailDate(LocalDate.now());
                } else {
                    days_diff = Days.daysBetween(license.getLastSentExpiryEmailDate(), LocalDate.now().plusDays(7)).getDays();
                    if (days_diff > 0) {
                        check = true;
                    }
                }
                if (check == true) {
                    NotificationDto notificationDto = new NotificationDto();
                    LocalDate endDate = license.getExpiryDate();
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
                    }
                    notificationDto.setGameType(gameType.getDescription());
                    notificationDto.setInstitutionId(license.getInstitutionId());
                    notificationDto.setEndDate(endDate.toString("dd/MM/yyyy"));
                    Institution institution = (Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
                            Institution.class).block();
                    notificationDto.setInstitutionName(institution.getInstitutionName());
                    notificationDto.setInstitutionEmail(institution.getEmailAddress());
                    Agent agent = (Agent) mongoRepositoryReactive.findById(license.getAgentId(), Agent.class).block();
                    if (agent != null) {
                        notificationDto.setAgentFullName(agent.getFullName());
                        notificationDto.setAgentEmailAddress(agent.getEmailAddress());

                    }
                    Machine gamingMachine = (Machine) mongoRepositoryReactive.findById(license.convertToDto().getGamingMachineId(), Machine.class).block();
                    if (gamingMachine != null) {
                        notificationDto.setMachineNumber(gamingMachine.getSerialNumber());

                    }
                    notificationDto.setTemplate("LicenseUpdate");
                    notificationDtos.add(notificationDto);

                    license.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID);

                    mongoRepositoryReactive.saveOrUpdate(license);
                }
                sendEmailNotification(notificationDtos, "expired");
            }
        }
    }
    @Scheduled(cron = "0 0 6 * * *")
    @SchedulerLock(name = "Deactivate Institutions With Expired License", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void deactivateInstitutionsWithExpiredLicense() {
        ArrayList<String> licenseStatuses = new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        expirationList.getExpiredLicences(licenseStatuses);

    }
    @Scheduled(cron = "0 0 6 * * *")
    @SchedulerLock(name = "Send Email With Expired AIP", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void WithExpiredAIP(){
        ArrayList<String> licenseStatuses= new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        licenseStatuses.add(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID);
        List<License> licenses= expirationList.getExpiredLicences(licenseStatuses);
        List<NotificationDto> notificationDtos= new ArrayList<>();
        if(licenses!=null){
            for(License license: licenses){
                NotificationDto notificationDto= new NotificationDto();
                LocalDate endDate=license.getExpiryDate();
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
                notificationDto.setTemplate("LicenseUpdate");
                notificationDtos.add(notificationDto);

                license.setLicenseStatusId("01");
                mongoRepositoryReactive.saveOrUpdate(license);
            }
            sendEmailNotification(notificationDtos,"AIPExpired");
        }
    }

    @Scheduled(cron = "0 0 20 * * *")
    @SchedulerLock(name = "Deactivate Agent With No Payment", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    public void deactivateAgentWithNoPayment(){
        Query queryAgent= new Query();

        queryAgent.addCriteria(Criteria.where("createdAt").lte(LocalDateTime.now().minusDays(7)));
        queryAgent.addCriteria(Criteria.where("inactive").is(false));
        queryAgent.addCriteria(Criteria.where("authRoleId").is(LSLBAuthRoleReferenceData.AGENT_ROLE_ID));
        queryAgent.addCriteria(Criteria.where("ssoUserId").ne(null));
        queryAgent.limit(1000);
        List<AuthInfo> agents= (List<AuthInfo>)mongoRepositoryReactive.findAll(queryAgent, AuthInfo.class).toStream().collect(Collectors.toList());
        agents.parallelStream().forEach(agent -> {

            int days=0;
            boolean check = false;
            if(agent.getLastInactiveDate()!=null){
                days= Days.daysBetween(agent.getLastInactiveDate(),LocalDateTime.now()).getDays();
                if(days>=7){
                    check=true;
                }
            }else{
                check=true;
            }
            if(check==true){
                Query queryPayment= new Query();
                queryPayment.addCriteria(Criteria.where("agentId").is(agent.getId()));
                queryPayment.addCriteria(Criteria.where("paymentStatusId").is(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID));
                List<PaymentRecord> agentPaymentRecord=(List<PaymentRecord>)mongoRepositoryReactive.findAll(queryPayment,PaymentRecord.class).toStream().collect(Collectors.toList());
                if(agentPaymentRecord.size()==0){
                    agent.setInactive(false);
                    agent.setInactiveReason("You have been deactivated for lack of license payment, please kindly click forget password below to begin re-activation");
                    mongoRepositoryReactive.saveOrUpdate(agent);
                    NotificationDto notificationDto = new NotificationDto();
                    notificationDto.setDescription("You have been deactivated for lack of license payment");
                    notificationDto.setTemplate("Agent_Deactivation");
                    notificationDto.setCallBackUrl(frontEndPropertyHelper.getFrontEndUrl() + "/forgot-password");
                    notificationDto.setAgentEmailAddress(agent.getEmailAddress());
                    sendEmail.sendEmailDeactivationNotification(notificationDto);
                }
            }



        });

    }
    //@TODO fix pending document approval email
  //  @Scheduled(fixedRate = 5*60*1000)
    public void sendReminderEmail(){
        Aggregation documentAgg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("approvalRequestStatusId").is(ApprovalRequestStatusReferenceData.PENDING_ID)),
             Aggregation.project("id","documentTypeId","nextReminderDate"),
                Aggregation.group("approvalRequestStatusId")
        );

        List<DocumentSummaryDto> results = mongoTemplate.aggregate(documentAgg, Document.class, DocumentSummaryDto.class).getMappedResults();

        for (DocumentSummaryDto document: results) {
            boolean sentEmail=false;
            if(document.getNextReminderDate()==null){
                sentEmail=true;
            }else{
                if(document.getNextReminderDate()==LocalDate.now()) {
                    sentEmail=true;
                }
            }
            if(sentEmail==true) {
                DocumentType documentType=(DocumentType)mongoRepositoryReactive.findById(document.getDocumentTypeId(), DocumentType.class).block();
               if(documentType!=null){
                AuthInfo approverAuthInfo = documentType.getApprover();
                NotificationDto notificationDto = new NotificationDto();
                notificationDto.setDescription("You have " + documentType.getName() + " documents pending your approval ");
                notificationDto.setLslbApprovalEmailAddress(approverAuthInfo.getEmailAddress());
                sendEmail.sendPendingDocumentEmailNotification(notificationDto, "Pending Document Approval");
              document.setNextReminderDate(LocalDate.now().plusDays(3));
             // mongoRepositoryReactive.saveOrUpdate(document);
            }
            }

        }


    }
}
