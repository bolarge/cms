package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.domain.LicenseTransfer;
import com.software.finatech.lslb.cms.service.domain.ScheduledMeeting;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ScheduledMeetingPurposeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ScheduledMeetingStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.ApplicationFormService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.ScheduledMeetingService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ScheduledMeetingMailSenderAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
import static com.software.finatech.lslb.cms.service.referencedata.ScheduledMeetingPurposeReferenceData.isValidMeetingPurpose;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class ScheduledMeetingServiceImpl implements ScheduledMeetingService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledMeetingServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private AuthInfoService authInfoService;
    private AuditLogHelper auditLogHelper;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private ScheduledMeetingMailSenderAsync scheduledMeetingMailSenderAsync;

    private static final int NUMBER_OF_DAYS_BEFORE_MEETING_REMINDER = 1;
    private static final int POST_MEETING_REMINDER_DAYS = 7;
    private static final String scheduleMeetingAuditActionId = AuditActionReferenceData.SCHEDULED_MEETING_ID;

    @Autowired
    public ScheduledMeetingServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                       AuthInfoService authInfoService,
                                       AuditLogHelper auditLogHelper,
                                       SpringSecurityAuditorAware springSecurityAuditorAware,
                                       ScheduledMeetingMailSenderAsync scheduledMeetingMailSenderAsync) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.authInfoService = authInfoService;
        this.auditLogHelper = auditLogHelper;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.scheduledMeetingMailSenderAsync = scheduledMeetingMailSenderAsync;
    }


    @Override
    public Mono<ResponseEntity> findAllScheduledMeetings(int page,
                                                         int pageSize,
                                                         String sortDirection,
                                                         String sortProperty,
                                                         String institutionId,
                                                         String startDate,
                                                         String endDate,
                                                         String dateProperty,
                                                         String creatorId,
                                                         String cancelerId,
                                                         String purposeId,
                                                         HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(creatorId)) {
                query.addCriteria(Criteria.where("creatorId").is(creatorId));
            }
            if (!StringUtils.isEmpty(cancelerId)) {
                query.addCriteria(Criteria.where("cancelerId").is(cancelerId));
            }
            if (!StringUtils.isEmpty(purposeId)) {
                query.addCriteria(Criteria.where("meetingPurposeId").is(purposeId));
            }
            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, ScheduledMeeting.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

            if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                LocalDate endD8 = new LocalDate(endDate);
                LocalDate startD8 = new LocalDate(startDate);

                if (StringUtils.isEmpty(dateProperty)) {
                    dateProperty = "meetingDate";
                }
                query.addCriteria(Criteria.where(dateProperty).gte(startD8).lte(endD8));
            }

            Sort sort;
            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "createdAt");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            ArrayList<ScheduledMeeting> scheduledMeetings = (ArrayList<ScheduledMeeting>) mongoRepositoryReactive.findAll(query, ScheduledMeeting.class).toStream().collect(Collectors.toList());
            if (scheduledMeetings == null || scheduledMeetings.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<ScheduledMeetingDto> scheduledMeetingDtos = new ArrayList<>();
            scheduledMeetings.forEach(scheduledMeeting -> {
                scheduledMeetingDtos.add(scheduledMeeting.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(scheduledMeetingDtos, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format for meeting date , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while finding scheduled meetings", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllMeetingPurposes() {
        return getAllEnumeratedEntity("MeetingPurpose");
    }

    @Override
    public Mono<ResponseEntity> createScheduledMeeting(ScheduledMeetingCreateDto scheduledMeetingCreateDto, HttpServletRequest request) {
        try {
            HttpStatus badRequestStatus = HttpStatus.BAD_REQUEST;
            AuthInfo creator = getUser(scheduledMeetingCreateDto.getCreatorId());
            if (creator == null) {
                return Mono.just(new ResponseEntity<>("Creating user does not exist", badRequestStatus));
            }
            if (!isValidMeetingPurpose(scheduledMeetingCreateDto.getMeetingPurposeId())) {
                return Mono.just(new ResponseEntity<>("Invalid Meeting Purpose supplied", HttpStatus.BAD_REQUEST));
            }

            String institutionId = scheduledMeetingCreateDto.getInstitutionId();
            Institution invitedInstitution = getInstitution(institutionId);
            if (invitedInstitution == null) {
                return Mono.just(new ResponseEntity<>("Invited institution does not exist", badRequestStatus));
            }
            ArrayList<AuthInfo> gamingOperatorAdminsForInstitution = authInfoService.getAllActiveGamingOperatorUsersForInstitution(institutionId);
            if (gamingOperatorAdminsForInstitution == null || gamingOperatorAdminsForInstitution.isEmpty()) {
                return Mono.just(new ResponseEntity<>("There is no user for institution", badRequestStatus));
            }

            ArrayList<AuthInfo> recipientList = new ArrayList<>();
            for (String userId : scheduledMeetingCreateDto.getRecipients()) {
                AuthInfo recipient = authInfoService.getUserById(userId);
                if (recipient == null) {
                    return Mono.just(new ResponseEntity<>(String.format("User with id %s not found", userId), HttpStatus.BAD_REQUEST));
                }
                recipientList.add(recipient);
            }

            ScheduledMeeting scheduledMeeting = fromCreateDto(scheduledMeetingCreateDto);
            saveScheduledMeeting(scheduledMeeting);
            //         String creatorMailSubject = String.format("Scheduled meeting with %s", invitedInstitution.getInstitutionName());
//
//            scheduledMeetingMailSenderAsync.sendEmailToMeetingCreator("scheduled-meetings/ScheduledMeeting-InitialNotification-Creator", creatorMailSubject, scheduledMeeting);
//            scheduledMeetingMailSenderAsync.sendEmailToMeetingInvitedOperators("scheduled-meetings/ScheduledMeeting-InitialNotification-Operator", "Meeting Invite With Lagos State Lotteries Board", scheduledMeeting, null);
//            scheduledMeetingMailSenderAsync.sendEmailToMeetingRecipients("scheduled-meetings/ScheduledMeeting-InitialNotification-Recipient", creatorMailSubject, scheduledMeeting, recipientList);

            sendInitialMeetingNotifications(scheduledMeeting, recipientList, invitedInstitution);
            String institutionName = invitedInstitution.getInstitutionName();
            String verbiage = String.format("Created Scheduled Meeting, Institution Name-> %s , Meeting Purpose -> %s, Meeting Date -> %s, Venue -> %s, Id -> %s",
                    institutionName, scheduledMeeting.getMeetingPurpose(), scheduledMeeting.getMeetingDateString(), scheduledMeeting.getVenue(), scheduledMeeting.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(scheduleMeetingAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), institutionName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(scheduledMeeting.convertToDto(), HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format for meeting date , please use yyyy-MM-dd HH:mm:ss", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating the scheduled meeting", e);
        }
    }

    @Override
    public Mono<ResponseEntity> cancelScheduledMeeting(String scheduledMeetingId, String cancelerId, HttpServletRequest request) {
        try {
            ScheduledMeeting scheduledMeeting = findScheduledMeetingById(scheduledMeetingId);
            if (scheduledMeeting == null) {
                return Mono.just(new ResponseEntity<>("Scheduled meeting does not exist", HttpStatus.BAD_REQUEST));
            }
            AuthInfo canceler = getUser(cancelerId);
            if (canceler == null) {
                return Mono.just(new ResponseEntity<>("Canceling user does not exist", HttpStatus.BAD_REQUEST));
            }
            String canceledMeetingStatusId = ScheduledMeetingStatusReferenceData.CANCELED_STATUS_ID;
            scheduledMeeting.setScheduledMeetingStatusId(canceledMeetingStatusId);
            scheduledMeeting.setCancelerId(cancelerId);
            saveScheduledMeeting(scheduledMeeting);

            ArrayList<AuthInfo> recipients = scheduledMeeting.getRecipients();
            String institutionName = scheduledMeeting.getInstitutionName();
            String lslbMailSubject = String.format("Update on meeting with %s", institutionName);

            scheduledMeetingMailSenderAsync.sendEmailToMeetingCreator("scheduled-meetings/ScheduledMeeting-CancelNotification-LSLB", lslbMailSubject, scheduledMeeting);
            scheduledMeetingMailSenderAsync.sendEmailToMeetingRecipients("scheduled-meetings/ScheduledMeeting-CancelNotification-LSLB", lslbMailSubject, scheduledMeeting, recipients);
            scheduledMeetingMailSenderAsync.sendEmailToMeetingInvitedOperators("scheduled-meetings/ScheduledMeeting-CancelNotification-Operator", "Update on meeting with Lagos State Lotteries Board", scheduledMeeting, null);

            String verbiage = String.format("Canceled Scheduled Meeting, Institution Name-> %s , Meeting Date -> %s, Venue -> %s, meetingId -> %s",
                    institutionName, scheduledMeeting.getMeetingDateString(), scheduledMeeting.getVenue(), scheduledMeeting.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(scheduleMeetingAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), institutionName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(scheduledMeeting.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while canceling the scheduled meeting", e);
        }
    }

    @Override
    public Mono<ResponseEntity> completeScheduledMeeting(String scheduledMeetingId, HttpServletRequest request) {
        try {
            ScheduledMeeting scheduledMeeting = findScheduledMeetingById(scheduledMeetingId);
            if (scheduledMeeting == null) {
                return Mono.just(new ResponseEntity<>("Scheduled meeting does not exist", HttpStatus.BAD_REQUEST));
            }
            String completedMeetingStatusId = ScheduledMeetingStatusReferenceData.COMPLETED_STATUS_ID;
            scheduledMeeting.setScheduledMeetingStatusId(completedMeetingStatusId);
            saveScheduledMeeting(scheduledMeeting);
            String institutionName = scheduledMeeting.getInstitutionName();
            String verbiage = String.format("Completed Scheduled Meeting, Institution Name-> %s , Meeting Date -> %s, Venue -> %s, Id -> %s",
                    institutionName, scheduledMeeting.getMeetingDateString(), scheduledMeeting.getVenue(), scheduledMeeting.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(scheduleMeetingAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), institutionName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(scheduledMeeting.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while completing the scheduled meeting", e);
        }
    }

    @Override
    public Mono<ResponseEntity> updateScheduledMeting(ScheduledMeetingUpdateDto scheduledMeetingUpdateDto, HttpServletRequest request) {
        try {
            ScheduledMeeting existingScheduledMeeting = findScheduledMeetingById(scheduledMeetingUpdateDto.getId());
            if (existingScheduledMeeting == null) {
                return Mono.just(new ResponseEntity<>("Scheduled meeting does not exist", HttpStatus.BAD_REQUEST));
            }
            Institution invitedInstitution = existingScheduledMeeting.getInstitution();
            Set<String> updatedRecipientIds = scheduledMeetingUpdateDto.getRecipients();
            LocalDateTime newMeetingDate = FORMATTER.parseLocalDateTime(scheduledMeetingUpdateDto.getMeetingDate());
            Set<String> existingRecipientIds = existingScheduledMeeting.getRecipientIds();

            Set<String> recipientsForUpdateMail = new HashSet<>();
            Set<String> recipientsForNewInviteMail = new HashSet<>();
            Set<String> recipientsForRemoveMail = new HashSet<>();

            for (String updatedRecipientId : updatedRecipientIds) {
                AuthInfo user = getUser(updatedRecipientId);
                if (user == null) {
                    return Mono.just(new ResponseEntity<>(String.format("User with id %s not found", updatedRecipientId), HttpStatus.BAD_REQUEST));
                }
                if (existingRecipientIds.contains(updatedRecipientId)) {
                    recipientsForUpdateMail.add(updatedRecipientId);
                } else {
                    recipientsForNewInviteMail.add(updatedRecipientId);
                }
            }

            for (String existingRecipientId : existingRecipientIds) {
                if (!updatedRecipientIds.contains(existingRecipientId)) {
                    recipientsForRemoveMail.add(existingRecipientId);
                }
            }
            existingScheduledMeeting.setMeetingSubject(scheduledMeetingUpdateDto.getMeetingTitle());
            existingScheduledMeeting.setVenue(scheduledMeetingUpdateDto.getVenue());
            existingScheduledMeeting.setMeetingDescription(scheduledMeetingUpdateDto.getAdditionalNotes());
            existingScheduledMeeting.setMeetingDate(newMeetingDate);
            existingScheduledMeeting.setRecipientIds(scheduledMeetingUpdateDto.getRecipients());
            existingScheduledMeeting.setNextPostMeetingReminderDate(newMeetingDate.plusDays(POST_MEETING_REMINDER_DAYS));
            existingScheduledMeeting.setMeetingReminderDate(newMeetingDate.minusDays(NUMBER_OF_DAYS_BEFORE_MEETING_REMINDER));
            existingScheduledMeeting.setReminderSent(false);
            saveScheduledMeeting(existingScheduledMeeting);

//            String lslbMailSubject = String.format("Update on Scheduled meeting with %s", invitedInstitution.getInstitutionName());
//            scheduledMeetingMailSenderAsync.sendEmailToMeetingCreator("scheduled-meetings/ScheduledMeeting-UpdateNotification-Creator", lslbMailSubject, existingScheduledMeeting);
//            scheduledMeetingMailSenderAsync.sendEmailToMeetingInvitedOperators("scheduled-meetings/ScheduledMeeting-UpdateNotification-Operator", "Update on meeting with Lagos State Lotteries Board", existingScheduledMeeting, null);
//            scheduledMeetingMailSenderAsync.sendEmailToMeetingRecipients("scheduled-meetings/ScheduledMeeting-UpdateNotification-Recipient", lslbMailSubject, existingScheduledMeeting, authInfoService.getUsersFromUserIds(recipientsForUpdateMail));
//            scheduledMeetingMailSenderAsync.sendEmailToMeetingRecipients("scheduled-meetings/ScheduledMeeting-UpdateNotificationRemove-Recipient", lslbMailSubject, existingScheduledMeeting, authInfoService.getUsersFromUserIds(recipientsForRemoveMail));
//            scheduledMeetingMailSenderAsync.sendEmailToMeetingRecipients("scheduled-meetings/ScheduledMeeting-InitialNotification-Recipient", String.format("Meeting with %s", invitedInstitution.getInstitutionName()), existingScheduledMeeting, authInfoService.getUsersFromUserIds(recipientsForNewInviteMail));
            sendUpdatedMeetingNotifications(existingScheduledMeeting, invitedInstitution, recipientsForUpdateMail, recipientsForRemoveMail, recipientsForNewInviteMail);

            String verbiage = String.format("Updated Scheduled Meeting, Institution Name-> %s , Meeting Date -> %s, Venue -> %s, Id -> %s ",
                    invitedInstitution.getInstitutionName(), existingScheduledMeeting.getMeetingDateString(), existingScheduledMeeting.getVenue(), existingScheduledMeeting.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(scheduleMeetingAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), invitedInstitution.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(existingScheduledMeeting.convertToDto(), HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format for meeting date , please use yyyy-MM-dd HH:mm:ss", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating the scheduled meeting", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getScheduledMeetingById(String meetingId) {
        try {
            ScheduledMeeting existingScheduledMeeting = findScheduledMeetingById(meetingId);
            if (existingScheduledMeeting == null) {
                return Mono.just(new ResponseEntity<>("Scheduled meeting does not exist", HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(existingScheduledMeeting.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while  getting meeting by id", e);
        }
    }

    @Override
    public Mono<ResponseEntity> addCommentsToMeeting(String meetingId, AddCommentDto addCommentDto, HttpServletRequest request) {
        try {
            ScheduledMeeting existingScheduledMeeting = findScheduledMeetingById(meetingId);
            if (existingScheduledMeeting == null) {
                return Mono.just(new ResponseEntity<>("Scheduled meeting does not exist", HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            CommentDto commentDto = new CommentDto();
            commentDto.setComment(addCommentDto.getComment());
            commentDto.setCommentTime(LocalDateTime.now().toString("HH:mm:ss a"));
            commentDto.setCommentDate(LocalDateTime.now().toString("dd-MM-yyyy"));
            commentDto.setUserFullName(loggedInUser.getFullName());
            existingScheduledMeeting.getComments().add(commentDto);
            saveScheduledMeeting(existingScheduledMeeting);

            String institutionName = existingScheduledMeeting.getInstitutionName();
            String verbiage = String.format("Added comment to Scheduled Meeting, Institution Name-> %s ,Comment -> %s,  Id -> %s ",
                    institutionName, addCommentDto.getComment(), existingScheduledMeeting.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(scheduleMeetingAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), institutionName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>("Comment added successfully", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comments", e);
        }
    }

    @Override
    public ScheduledMeeting findScheduledMeetingByEntityId(String entityId) {
        if (StringUtils.isEmpty(entityId)) {
            return null;
        }
        return (ScheduledMeeting) mongoRepositoryReactive.find(Query.query(Criteria.where("entityId").is(entityId)), ScheduledMeeting.class).block();
    }

    private ScheduledMeeting fromCreateDto(ScheduledMeetingCreateDto scheduledMeetingCreateDto) {
        ScheduledMeeting scheduledMeeting = new ScheduledMeeting();
        scheduledMeeting.setInstitutionId(scheduledMeetingCreateDto.getInstitutionId());
        scheduledMeeting.setId(UUID.randomUUID().toString());
        scheduledMeeting.setVenue(scheduledMeetingCreateDto.getVenue());
        scheduledMeeting.setMeetingSubject(scheduledMeetingCreateDto.getMeetingTitle());
        scheduledMeeting.setCreatorId(scheduledMeetingCreateDto.getCreatorId());
        scheduledMeeting.setRecipientIds(scheduledMeetingCreateDto.getRecipients());
        scheduledMeeting.setEntityId(scheduledMeetingCreateDto.getEntityId());
        scheduledMeeting.setMeetingPurposeId(scheduledMeetingCreateDto.getMeetingPurposeId());
        String pendingScheduledMeetingStatusId = ScheduledMeetingStatusReferenceData.PENDING_STATUS_ID;
        scheduledMeeting.setScheduledMeetingStatusId(pendingScheduledMeetingStatusId);
        LocalDateTime meetingDate = FORMATTER.parseLocalDateTime(scheduledMeetingCreateDto.getMeetingDate());
        scheduledMeeting.setMeetingDate(meetingDate);
        scheduledMeeting.setNextPostMeetingReminderDate(meetingDate.plusDays(POST_MEETING_REMINDER_DAYS));
        scheduledMeeting.setMeetingReminderDate(meetingDate.minusDays(NUMBER_OF_DAYS_BEFORE_MEETING_REMINDER));
        return scheduledMeeting;
    }

    private void saveScheduledMeeting(ScheduledMeeting scheduledMeeting) {
        mongoRepositoryReactive.saveOrUpdate(scheduledMeeting);
    }

    private ScheduledMeeting findScheduledMeetingById(String scheduledMeetingId) {
        return (ScheduledMeeting) mongoRepositoryReactive.findById(scheduledMeetingId, ScheduledMeeting.class).block();
    }

    private AuthInfo getUser(String userId) {
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
    }

    private Institution getInstitution(String institutionId) {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    private void sendInitialMeetingNotifications(ScheduledMeeting scheduledMeeting, ArrayList<AuthInfo> recipients, Institution invitedInstitution) {
        String operatorTemplateName = "";
        Institution fromInstitution = null;
        String creatorMailSubject = String.format("Scheduled meeting with %s", invitedInstitution);
        if (scheduledMeeting.isForLicenseApplicant()) {
            operatorTemplateName = "scheduled-meetings/ScheduledMeeting-InitialNotification-ApplicantOperator";
        }
        if (scheduledMeeting.isForLicenseTransferee()) {
            operatorTemplateName = "scheduled-meetings/ScheduledMeeting-InitialNotification-TransfereeOperator";
        }
        if (scheduledMeeting.isForLicenseTransferror()) {
            operatorTemplateName = "scheduled-meetings/ScheduledMeeting-InitialNotification-TransferorOperator";
        }
        LicenseTransfer licenseTransfer = scheduledMeeting.getLicenseTransfer();
        if (licenseTransfer != null) {
            fromInstitution = licenseTransfer.getFromInstitution();
        }
        scheduledMeetingMailSenderAsync.sendEmailToMeetingCreator("scheduled-meetings/ScheduledMeeting-InitialNotification-Creator", creatorMailSubject, scheduledMeeting);
        scheduledMeetingMailSenderAsync.sendEmailToMeetingInvitedOperators(operatorTemplateName, "Meeting Invite With Lagos State Lotteries Board", scheduledMeeting, fromInstitution);
        scheduledMeetingMailSenderAsync.sendEmailToMeetingRecipients("scheduled-meetings/ScheduledMeeting-InitialNotification-Recipient", creatorMailSubject, scheduledMeeting, recipients);
    }

    private void sendUpdatedMeetingNotifications(ScheduledMeeting existingScheduledMeeting, Institution invitedInstitution, Set<String> recipientsForUpdateMail, Set<String> recipientsForRemoveMail,
                                                 Set<String> recipientsForNewInviteMail) {
        String operatorTemplateName = "";
        Institution fromInstitution = null;
        if (existingScheduledMeeting.isForLicenseApplicant()) {
            operatorTemplateName = "scheduled-meetings/ScheduledMeeting-UpdateNotification-ApplicantOperator";
        }
        if (existingScheduledMeeting.isForLicenseTransferee()) {
            operatorTemplateName = "scheduled-meetings/ScheduledMeeting-UpdateNotification-TransfereeOperator";
        }
        if (existingScheduledMeeting.isForLicenseTransferror()) {
            operatorTemplateName = "scheduled-meetings/ScheduledMeeting-UpdateNotification-TransferorOperator";
        }
        LicenseTransfer licenseTransfer = existingScheduledMeeting.getLicenseTransfer();
        if (licenseTransfer != null) {
            fromInstitution = licenseTransfer.getFromInstitution();
        }

        String lslbMailSubject = String.format("Update on Scheduled meeting with %s", invitedInstitution.getInstitutionName());
        scheduledMeetingMailSenderAsync.sendEmailToMeetingCreator("scheduled-meetings/ScheduledMeeting-UpdateNotification-Creator", lslbMailSubject, existingScheduledMeeting);
        scheduledMeetingMailSenderAsync.sendEmailToMeetingInvitedOperators(operatorTemplateName, "Update on meeting with Lagos State Lotteries Board", existingScheduledMeeting, fromInstitution);
        scheduledMeetingMailSenderAsync.sendEmailToMeetingRecipients("scheduled-meetings/ScheduledMeeting-UpdateNotification-Recipient", lslbMailSubject, existingScheduledMeeting, authInfoService.getUsersFromUserIds(recipientsForUpdateMail));
        scheduledMeetingMailSenderAsync.sendEmailToMeetingRecipients("scheduled-meetings/ScheduledMeeting-UpdateNotificationRemove-Recipient", lslbMailSubject, existingScheduledMeeting, authInfoService.getUsersFromUserIds(recipientsForRemoveMail));
        scheduledMeetingMailSenderAsync.sendEmailToMeetingRecipients("scheduled-meetings/ScheduledMeeting-InitialNotification-Recipient", String.format("Meeting with %s", invitedInstitution.getInstitutionName()), existingScheduledMeeting, authInfoService.getUsersFromUserIds(recipientsForNewInviteMail));
    }
}
