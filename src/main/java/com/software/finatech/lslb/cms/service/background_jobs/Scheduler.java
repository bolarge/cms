package com.software.finatech.lslb.cms.service.background_jobs;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.NotificationDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.AuthInfoServiceImpl;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.PaymentRecordServiceImpl;
import com.software.finatech.lslb.cms.service.util.*;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.AIPMailSenderAsync;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    @Autowired
    private AIPMailSenderAsync aipMailSenderAsync;

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


    @Scheduled(cron = "0 6 0 * * *")
    // @Scheduled(fixedRate = 1000)
    @SchedulerLock(name = "Check For Licenses Close To Expirations", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void checkForLicensesCloseToExpirations() {
        logger.info(" checkForLicensesCloseToExpirations");
        ArrayList<String> licenseStatuses = new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        licenseStatuses.add(LicenseStatusReferenceData.RENEWED_ID);

        List<License> licenses =
                expirationList.getExpiringLicences(90, licenseStatuses);
        List<NotificationDto> notificationDtos = new ArrayList<>();
        LocalDate endDate;
        dateTime = dateTime.plusMonths(3);
        if (licenses != null) {
            for (License license : licenses) {
                int days_diff = 0;
                days_diff = Days.daysBetween(LocalDate.now(), license.getExpiryDate()).getDays();

                if (days_diff <= 7 && days_diff >= 0) {

                    int days = 0;
                    String licenceType = license.getLicenseTypeId();
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
                    if (gameType != null) {
                        notificationDto.setGameType(gameType.getDescription());
                    }
                    notificationDto.setEndDate(endDate.toString("dd/MM/yyyy"));
                    if (licenceType.equalsIgnoreCase(LicenseTypeReferenceData.INSTITUTION_ID)) {
                        notificationDto.setInstitutionId(license.getInstitutionId());
                        Institution institution = (Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
                                Institution.class).block();
                        notificationDto.setInstitutionName(institution.getInstitutionName());
                        notificationDto.setInstitutionEmail(institution.getEmailAddress());
                    }
                    if (licenceType.equalsIgnoreCase(LicenseTypeReferenceData.AGENT_ID)) {
                        Agent agent = (Agent) mongoRepositoryReactive.findById(license.getAgentId(), Agent.class).block();
                        if (agent != null) {
                            notificationDto.setAgentFullName(agent.getFullName());
                            notificationDto.setAgentEmailAddress(agent.getEmailAddress());
                            notificationDto.setAgentId(license.getAgentId());
                        }
                    }
//                    Machine gamingMachine = (Machine) mongoRepositoryReactive.findById(license.convertToDto().getGamingMachineId(), Machine.class).block();
//                    if (gamingMachine != null) {
//                        notificationDto.setMachineNumber(gamingMachine.getSerialNumber());
//                    }
                    notificationDto.setTemplate("LicenseUpdate");
                    notificationDtos.add(notificationDto);
                }
                sendEmailNotification(notificationDtos, "expiring");
            }

        }
    }

    @Scheduled(cron = "0 0 6 * * *")
    @SchedulerLock(name = "Check For AIP Close To Expiration", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void checkForAIPCloseToExpirations() {

        logger.info("checkForAIPCloseToExpirations");
        ArrayList<String> licenseStatuses = new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        licenseStatuses.add(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID);
        List<License> licenses = expirationList.getExpiringLicences(14, licenseStatuses);
        List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_AIP_ID);
        if (lslbAdmins.size() != 0) {
            lslbAdmins.forEach(lslbAdmin -> {

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
        for (NotificationDto notificationDto : notificationDtos) {
            HashMap<String, Object> model = new HashMap<>();
            model.put("endDate", notificationDto.getEndDate());
            if (StringUtils.equals("expiring", type)) {
//                if (!StringUtils.isEmpty(notificationDto.getGamingMachineId())) {
//                    model.put("description", notificationDto.getInstitutionName() + " Gaming Machine with machine number: " + notificationDto.getMachineNumber() + " License is due to expire on " + notificationDto.getEndDate());
//
//                } else
                if (!StringUtils.isEmpty(notificationDto.getAgentId())) {
                    model.put("description", notificationDto.getInstitutionName() + " Agent: " + notificationDto.getAgentFullName() + " License is due to expire on " + notificationDto.getEndDate());

                } else if (!StringUtils.isEmpty(notificationDto.getInstitutionId())) {
                    model.put("institutionId", notificationDto.getInstitutionId());
                    model.put("institutionName", notificationDto.getInstitutionName());

                    model.put("description", notificationDto.getInstitutionName() + " with Game Type: " + notificationDto.getGameType() + " License is due to expire on " + notificationDto.getEndDate());

                }
            } else if (StringUtils.equals("expired", type)) {
                if (!StringUtils.isEmpty(notificationDto.getGamingMachineId())) {
                    model.put("description", notificationDto.getInstitutionName() + " Gaming Machine with machine number: " + notificationDto.getMachineNumber() + " License has expired. License Expiration Date is " + notificationDto.getEndDate());
                }
                if (!StringUtils.isEmpty(notificationDto.getAgentId())) {
                    model.put("description", " Agent: " + notificationDto.getAgentFullName() + " License for " + notificationDto.getGameType() + " is due to expire on " + notificationDto.getEndDate());
                } else {
                    model.put("description", notificationDto.getInstitutionName() + " with Game Type: " + notificationDto.getGameType() + " License has expired. License Expiration Date is " + notificationDto.getEndDate());

                }
            } else if (StringUtils.equals("AIPExpired", type)) {
                model.put("description", notificationDto.getInstitutionName() + " " + notificationDto.getGameType() + " AIP period has ended");
            } else if (StringUtils.equals("AIPExpiring", type)) {
                model.put("description", notificationDto.getInstitutionName() + " " + notificationDto.getGameType() + " AIP period is due to end on " + notificationDto.getEndDate());
            }

            model.put("gameType", notificationDto.getGameType());
            model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
            String content = mailContentBuilderService.build(model, notificationDto.getTemplate());
            List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_AIP_ID);
            emailService.sendEmail(content, "AIP Expiration Notification", notificationDto.getInstitutionEmail());

            if ((StringUtils.equals("AIPExpired", type)) || (StringUtils.equals("AIPExpiring", type))) {
                if (lslbAdmins.size() != 0) {
                    lslbAdmins.stream().forEach(lslbAdmin -> {
                        emailService.sendEmail(content, "AIP Expiration Notification", lslbAdmin.getEmailAddress());
                    });
                }
            } else {
                if (!StringUtils.isEmpty(notificationDto.getAgentId())) {
                    emailService.sendEmail(content, "Licence Expiration Notification", notificationDto.getAgentEmailAddress());
                }
                emailService.sendEmail(content, "Licence Expiration Notification", notificationDto.getInstitutionEmail());
                if (lslbAdmins.size() != 0) {
                    lslbAdmins.stream().forEach(lslbAdmin -> {
                        emailService.sendEmail(content, "Licence Expiration Notification", lslbAdmin.getEmailAddress());
                    });

                }
            }
        }
    }


    @Scheduled(cron = "0 0 6 * * *")
    @SchedulerLock(name = "Check For Expired Licenses", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void expiredLicense() {
        ArrayList<String> licenseStatuses = new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        licenseStatuses.add(LicenseStatusReferenceData.RENEWED_ID);
        List<License> licenses = expirationList.getExpiredLicences(licenseStatuses);
//        List<NotificationDto> notificationDtos = new ArrayList<>();
//        if (licenses != null) {
//            for (License license : licenses) {
//                boolean check = false;
//                int days_diff = 0;
//                if (license.getLastSentExpiryEmailDate() == null) {
//                    check = true;
//                    license.setLastSentExpiryEmailDate(LocalDate.now());
//
//                } else {
//                    days_diff = Days.daysBetween(license.getLastSentExpiryEmailDate(), LocalDate.now().plusDays(7)).getDays();
//                    if (days_diff > 0) {
//                        check = true;
//                        license.setLastSentExpiryEmailDate(LocalDate.now());
//                    }
//                }
//                mongoRepositoryReactive.saveOrUpdate(license);
//                if (check == true) {
//                    String licenceType = license.getLicenseTypeId();
//                    NotificationDto notificationDto = new NotificationDto();
//                    LocalDate endDate = license.getExpiryDate();
//                    GameType gameType = null;
//                    Map gameTypeMap = Mapstore.STORE.get("GameType");
//                    if (gameTypeMap != null) {
//                        gameType = (GameType) gameTypeMap.get(license.getGameTypeId());
//                    }
//                    if (gameType == null) {
//                        gameType = (GameType) mongoRepositoryReactive.findById(license.getGameTypeId(), GameType.class).block();
//                        if (gameType != null && gameTypeMap != null) {
//                            gameTypeMap.put(license.getGameTypeId(), gameType);
//                        }
//                    }
//                    notificationDto.setGameType(gameType.getDescription());
//
//                    notificationDto.setEndDate(endDate.toString("dd/MM/yyyy"));
//                    if (licenceType.equalsIgnoreCase(LicenseTypeReferenceData.INSTITUTION_ID)) {
//                        notificationDto.setInstitutionId(license.getInstitutionId());
//                        Institution institution = (Institution) mongoRepositoryReactive.findById(license.getInstitutionId(),
//                                Institution.class).block();
//                        notificationDto.setInstitutionName(institution.getInstitutionName());
//                        notificationDto.setInstitutionEmail(institution.getEmailAddress());
//                    }
//                    if (licenceType.equalsIgnoreCase(LicenseTypeReferenceData.AGENT_ID)) {
//                        Agent agent = (Agent) mongoRepositoryReactive.findById(license.getAgentId(), Agent.class).block();
//                        if (agent != null) {
//                            notificationDto.setAgentFullName(agent.getFullName());
//                            notificationDto.setAgentEmailAddress(agent.getEmailAddress());
//                            notificationDto.setAgentId(license.getAgentId());
//                        }
//                    }
//
//
////                    Machine gamingMachine = (Machine) mongoRepositoryReactive.findById(license.convertToDto().getGamingMachineId(), Machine.class).block();
////                    if (gamingMachine != null) {
////                        notificationDto.setMachineNumber(gamingMachine.getSerialNumber());
////
////                    }
//                    notificationDto.setTemplate("LicenseUpdate");
//                    notificationDtos.add(notificationDto);
//
//                }
//              //  sendEmailNotification(notificationDtos, "expired");
//            }
//        }
    }

    @Scheduled(cron = "0 0 6 * * *")
    @SchedulerLock(name = "Deactivate Institutions With Expired License", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void deactivateInstitutionsWithExpiredLicense() {
        ArrayList<String> licenseStatuses = new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        expirationList.getExpiredLicences(licenseStatuses);

    }

    @Scheduled(cron = "0 0 7 1 1 ?")
    @SchedulerLock(name = "Send email for expired Expired Gaming machine License", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void expiredGamingMachineLicense() {
        Query machineLicenseQuery = new Query();
        machineLicenseQuery.addCriteria(Criteria.where("licenceTypeId").is(LicenseTypeReferenceData.GAMING_MACHINE_ID));
        List<License> machineLicenses = (List<License>) mongoRepositoryReactive.findAll(machineLicenseQuery, License.class).toStream().collect(Collectors.toList());
        machineLicenses.parallelStream().forEach(machineLicense -> {
            NotificationDto notificationDto = new NotificationDto();
            LocalDate endDate = machineLicense.getExpiryDate();
            GameType gameType = null;
            Map gameTypeMap = Mapstore.STORE.get("GameType");
            if (gameTypeMap != null) {
                gameType = (GameType) gameTypeMap.get(machineLicense.getGameTypeId());
            }
            if (gameType == null) {
                gameType = (GameType) mongoRepositoryReactive.findById(machineLicense.getGameTypeId(), GameType.class).block();
                if (gameType != null && gameTypeMap != null) {
                    gameTypeMap.put(machineLicense.getGameTypeId(), gameType);
                }
            }
            notificationDto.setGameType(gameType.getDescription());
            notificationDto.setInstitutionId(machineLicense.getInstitutionId());
            notificationDto.setEndDate(endDate.toString("dd/MM/yyyy"));
            Institution institution = (Institution) mongoRepositoryReactive.findById(machineLicense.getInstitutionId(),
                    Institution.class).block();
            if (institution != null) {
                notificationDto.setInstitutionName(institution.getInstitutionName());
                notificationDto.setInstitutionEmail(institution.getEmailAddress());
                notificationDto.setDescription(institution.getInstitutionName() + " Gaming Machines/Terminals have expired, do make the required renewals");
                notificationDto.setTemplate("LicenseUpdate");
                sendEmail.sendEmailExpiredMachineLicenses(notificationDto);
            }


        });
    }

    @Scheduled(cron = "0 0 6 * * *")
    @SchedulerLock(name = "Send Email With Expired AIP", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    protected void WithExpiredAIP() {
        ArrayList<String> licenseStatuses = new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        licenseStatuses.add(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID);
        List<License> licenses = expirationList.getExpiredLicences(licenseStatuses);
        List<NotificationDto> notificationDtos = new ArrayList<>();
        if (licenses != null) {
            for (License license : licenses) {
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

                if (gameType != null) {
                    notificationDto.setGameType(gameType.getDescription());
                }
                notificationDto.setInstitutionId(license.getPaymentRecord().getInstitutionId());
                notificationDto.setEndDate(endDate.toString("dd/MM/yyyy"));
                Institution institution = (Institution) mongoRepositoryReactive.findById(license.getPaymentRecord().getInstitutionId(),
                        Institution.class).block();
                notificationDto.setInstitutionName(institution.getInstitutionName());
                notificationDto.setInstitutionEmail(institution.getEmailAddress());
                notificationDto.setTemplate("LicenseUpdate");
                notificationDtos.add(notificationDto);

            }
            sendEmailNotification(notificationDtos, "AIPExpired");
        }
    }

    @Scheduled(cron = "0 0 20 * * *")
    @SchedulerLock(name = "Deactivate Agent With No Payment", lockAtMostFor = ONE_HOUR, lockAtLeastFor = ONE_HOUR)
    public void deactivateAgentWithNoPayment() {
        Query queryAgent = new Query();

        queryAgent.addCriteria(Criteria.where("createdAt").lte(LocalDateTime.now().minusDays(7)));
        queryAgent.addCriteria(Criteria.where("inactive").is(false));
        queryAgent.addCriteria(Criteria.where("authRoleId").is(LSLBAuthRoleReferenceData.AGENT_ROLE_ID));
        queryAgent.addCriteria(Criteria.where("ssoUserId").ne(null));
        queryAgent.limit(1000);
        List<AuthInfo> agents = (List<AuthInfo>) mongoRepositoryReactive.findAll(queryAgent, AuthInfo.class).toStream().collect(Collectors.toList());
        agents.parallelStream().forEach(agent -> {

            int days = 0;
            boolean check = false;
            if (agent.getLastInactiveDate() != null) {
                days = Days.daysBetween(agent.getLastInactiveDate(), LocalDateTime.now()).getDays();
                if (days >= 7) {
                    check = true;
                }
            } else {
                check = true;
            }
            if (check == true) {
                Query queryPayment = new Query();
                queryPayment.addCriteria(Criteria.where("agentId").is(agent.getId()));
                queryPayment.addCriteria(Criteria.where("paymentStatusId").is(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID));
                try {
                    List<PaymentRecord> agentPaymentRecord = (List<PaymentRecord>) mongoRepositoryReactive.findAll(queryPayment, PaymentRecord.class).toStream().collect(Collectors.toList());
                    if (agentPaymentRecord.size() == 0) {
                        agent.setInactive(false);
                        agent.setInactiveReason("You have been deactivated for lack of license payment, please kindly click forget password below to begin re-activation");
                        agent.setLastInactiveDate(LocalDate.now());
                        mongoRepositoryReactive.saveOrUpdate(agent);
                        NotificationDto notificationDto = new NotificationDto();
                        notificationDto.setDescription("You have been deactivated for lack of license payment");
                        notificationDto.setTemplate("Agent_Deactivation");
                        notificationDto.setCallBackUrl(frontEndPropertyHelper.getFrontEndUrl() + "/forgot-password");
                        notificationDto.setAgentEmailAddress(agent.getEmailAddress());
                        sendEmail.sendEmailDeactivationNotification(notificationDto);
                    }
                } catch (Exception ex) {
                    logger.info(ex.getMessage());
                }
            }


        });

    }

    //@TODO fix pending document approval email
    @Scheduled(cron = "0 0 13 * * *")
    public void sendReminderEmail() {
//        Aggregation documentAgg = Aggregation.newAggregation(
//                Aggregation.match(Criteria.where("approvalRequestStatusId").is(ApprovalRequestStatusReferenceData.PENDING_ID)),
//             Aggregation.project()
//                     .and("documentTypeId").as("documentTypeId")
//                     .and("nextReminderDate").as("nextReminderDate")
//                     .and("id").as("id").and("approvalRequestStatusId").as("approvalRequestStatusId")
//                //Aggregation.group("id")
//        );

        Query query = new Query();
        query.addCriteria(Criteria.where("approvalRequestStatusId").is(ApprovalRequestStatusReferenceData.PENDING_ID));

        try {
            ArrayList<Document> documents = (ArrayList<Document>) mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());

            for (Document document : documents) {
                boolean sentEmail = false;
                if (document.getNextReminderDate() == null) {
                    sentEmail = true;
                } else {
                    if (document.getNextReminderDate() == LocalDate.now()) {
                        sentEmail = true;
                    }
                }
                if (sentEmail == true) {
                    DocumentType documentType = (DocumentType) mongoRepositoryReactive.findById(document.getDocumentTypeId(), DocumentType.class).block();
                    if (documentType != null) {
                        try {
                            AuthInfo approverAuthInfo = documentType.getApprover();
                            NotificationDto notificationDto = new NotificationDto();
                            notificationDto.setDescription("You have " + documentType.getName() + " documents pending your approval ");
                            notificationDto.setLslbApprovalEmailAddress(approverAuthInfo.getEmailAddress());
                            sendEmail.sendPendingDocumentEmailNotification(notificationDto, "Pending Document Approval");
                            document.setNextReminderDate(LocalDate.now().plusDays(3));
                            mongoRepositoryReactive.saveOrUpdate(document);
                        } catch (Throwable Ex) {
                            logger.info(Ex.getMessage());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    /**
     * sends mail to GM for all PENDING AIP forms
     * that have approved documents
     */
    // @Scheduled(cron = "0 0 9 * * *")
    @Scheduled(fixedRate = 1 * 60 * 1000, initialDelay = 600)
    @SchedulerLock(name = "Send Email for all AIP Ready for approval", lockAtMostFor = 1 * 60 * 1000, lockAtLeastFor = 1 * 60 * 1000)
    public void sendMailForAIPForms() {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("formStatusId").is(ApplicationFormStatusReferenceData.IN_REVIEW_STATUS_ID));
            query.addCriteria(Criteria.where("finalNotificationSent").is(false));
            ArrayList<AIPDocumentApproval> aipDocumentApprovals = (ArrayList<AIPDocumentApproval>) mongoRepositoryReactive.findAll(query, AIPDocumentApproval.class).toStream().collect(Collectors.toList());
            if (aipDocumentApprovals.isEmpty()) {
                return;
            }
            for (AIPDocumentApproval aipDocumentApproval : aipDocumentApprovals) {
                if (aipDocumentApproval.getReadyForApproval() != null
                        && aipDocumentApproval.getReadyForApproval()
                        && aipDocumentApproval.hasCompleteAssessmentReport()
                        && !aipDocumentApproval.isFinalNotificationSent()) {
                    aipMailSenderAsync.sendFinalAIPApprovalMailTOFianlApprovers(aipDocumentApproval);
                    aipDocumentApproval.setFinalNotificationSent(true);
                    aipDocumentApproval.setReadyForFinalApproval(true);
                    mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
                }
            }
        } catch (Throwable e) {
            logger.error("An error occurred while sending AIP mails", e);
        }
    }


    // @Scheduled(fixedDelay = 34500000, initialDelay = 600000)
    //  @Async
//    public void load() {
//        List<Document> documentList = (List<Document>) mongoRepositoryReactive.findAll(new Query(), Document.class).toStream().collect(Collectors.toList());
//        documentList.parallelStream().
//                forEach(document -> {
//                    try {
//                        if (document.getFile() != null) {
//                            DocumentBinary documentBinary = new DocumentBinary();
//                            documentBinary.setFile(document.getFile());
//                            documentBinary.setDocumentId(document.getId());
//                            mongoRepositoryReactive.saveOrUpdate(documentBinary);
//                            document.setFile(null);
//                            mongoRepositoryReactive.saveOrUpdate(document);
//                        }
//                    } catch (Exception e) {
//
//                    }
//                });
//    }
}
