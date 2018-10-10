package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LoggedCaseStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.LoggedCaseService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.LoggedCaseMailSenderAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class LoggedCaseServiceImpl implements LoggedCaseService {
    private static final Logger logger = LoggerFactory.getLogger(LoggedCaseServiceImpl.class);
    private static final String loggedCaseAuditActionId = AuditActionReferenceData.CASE_ID;

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private AuthInfoService authInfoService;
    private LoggedCaseMailSenderAsync loggedCaseMailSenderAsync;
    private AuditLogHelper auditLogHelper;
    private SpringSecurityAuditorAware springSecurityAuditorAware;

    @Autowired
    public LoggedCaseServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                 AuthInfoService authInfoService, LoggedCaseMailSenderAsync loggedCaseMailSenderAsync,
                                 AuditLogHelper auditLogHelper, SpringSecurityAuditorAware springSecurityAuditorAware) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.authInfoService = authInfoService;
        this.loggedCaseMailSenderAsync = loggedCaseMailSenderAsync;
        this.auditLogHelper = auditLogHelper;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
    }

    @Override
    public Mono<ResponseEntity> findAllLoggedCases(int page,
                                                   int pageSize,
                                                   String sortDirection,
                                                   String sortProperty,
                                                   String reporterId,
                                                   String institutionId,
                                                   String loggedCaseStatusId,
                                                   String agentId,
                                                   String startDate,
                                                   String endDate,
                                                   HttpServletResponse httpServletResponse) {
        try {

            Query query = new Query();
            if (!StringUtils.isEmpty(reporterId)) {
                query.addCriteria(Criteria.where("reporterId").is(reporterId));
            }
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(agentId)) {
                query.addCriteria(Criteria.where("agentId").is(agentId));
            }
            if (!StringUtils.isEmpty(agentId)) {
                query.addCriteria(Criteria.where("agentId").is(agentId));
            }
            if (!StringUtils.isEmpty(loggedCaseStatusId)) {
                query.addCriteria(Criteria.where("loggedCaseStatusId").is(loggedCaseStatusId));
            }
            if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                LocalDate fromDate = new LocalDate(startDate);
                LocalDate toDate = new LocalDate(endDate).plusDays(1);
                query.addCriteria(Criteria.where("dateTimeReported").gte(fromDate).lte(toDate));
            }
            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, LoggedCase.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
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

            ArrayList<LoggedCase> loggedCases = (ArrayList<LoggedCase>) mongoRepositoryReactive.findAll(query, LoggedCase.class).toStream().collect(Collectors.toList());
            if (loggedCases == null || loggedCases.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<LoggedCaseDto> loggedCaseDtos = new ArrayList<>();
            loggedCases.forEach(loggedCase -> {
                loggedCaseDtos.add(loggedCase.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(loggedCaseDtos, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting logged cases", e);
        }
    }

    @Override
    public Mono<ResponseEntity> createCase(LoggedCaseCreateDto loggedCaseCreateDto, HttpServletRequest request) {
        try {
            String userId = loggedCaseCreateDto.getUserId();
            if (!loggedCaseCreateDto.isValid()) {
                return Mono.just(new ResponseEntity<>("Please provide either agent id or institution id alone", HttpStatus.BAD_REQUEST));
            }
            AuthInfo user = authInfoService.getUserById(userId);
            if (user == null) {
                return Mono.just(new ResponseEntity<>(String.format("User with id %s does not exist", userId), HttpStatus.BAD_REQUEST));
            }
            if (!userCanUpdateCase(user)) {
                return Mono.just(new ResponseEntity<>("User  cannot create logged case, please check user role and user status", HttpStatus.BAD_REQUEST));
            }
            LoggedCase loggedCase = fromLoggedCaseCreateDto(loggedCaseCreateDto);
            mongoRepositoryReactive.saveOrUpdate(loggedCase);
            String verbiage = String.format("Created logged case ,Ticket id : -> %s ", loggedCase.getTicketId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(loggedCaseAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), loggedCase.getReportedEntityName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            loggedCaseMailSenderAsync.sendNewCaseNotificationToLslbUsersThatCanReceive(loggedCase);
            return Mono.just(new ResponseEntity<>(loggedCase.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating case", e);
        }
    }

    @Override
    public Mono<ResponseEntity> addLoggedCaseAction(LoggedCaseActionCreateDto caseActionCreateDto, HttpServletRequest request) {
        try {
            String caseId = caseActionCreateDto.getCaseId();
            String userId = caseActionCreateDto.getUserId();
            String caseStatusId = caseActionCreateDto.getCaseStatusId();
            LoggedCase existingCase = findCaseById(caseId);
            if (existingCase == null) {
                return Mono.just(new ResponseEntity<>(String.format("LoggedCase with is %s not found", caseId), HttpStatus.BAD_REQUEST));
            }

            LoggedCaseStatus oldCaseStatus = existingCase.getCaseStatus(existingCase.getLoggedCaseStatusId());
            if (existingCase.isClosed()) {
                return Mono.just(new ResponseEntity<>("The logged case is already closed", HttpStatus.BAD_REQUEST));
            }
            AuthInfo user = authInfoService.getUserById(userId);
            if (user == null) {
                return Mono.just(new ResponseEntity<>(String.format("User with is %s not found", userId), HttpStatus.BAD_REQUEST));
            }
            if (!userCanUpdateCase(user)) {
                return Mono.just(new ResponseEntity<>("User cannot change logged case status, please check user role and user status", HttpStatus.BAD_REQUEST));
            }

            if (!LoggedCaseStatusReferenceData.getCaseStatusIds().contains(caseStatusId)) {
                return Mono.just(new ResponseEntity<>(String.format("LoggedCase status with id %s does not exist on system", caseStatusId), HttpStatus.BAD_REQUEST));
            }
            LoggedCaseAction caseAction = new LoggedCaseAction();
            caseAction.setActionTime(LocalDateTime.now());
            caseAction.setUserId(userId);
            caseAction.setLslbCaseStatusId(caseStatusId);
            existingCase.setLoggedCaseStatusId(caseStatusId);
            existingCase.getCaseActions().add(caseAction);
            mongoRepositoryReactive.saveOrUpdate(existingCase);
            String verbiage = String.format("Changed logged case status , Ticket id:  -> %s ,Old status -> %s,  New Status -> %s, ",
                    existingCase.getTicketId(), oldCaseStatus, existingCase.getCaseStatus(existingCase.getLoggedCaseStatusId()));
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(loggedCaseAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), existingCase.getReportedEntityName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(existingCase.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding action to case", e);
        }
    }

    @Override
    public Mono<ResponseEntity> addLoggedCaseComment(LoggedCaseCommentCreateDto caseCommentCreateDto, HttpServletRequest request) {
        try {
            String caseId = caseCommentCreateDto.getCaseId();
            String userId = caseCommentCreateDto.getUserId();
            LoggedCase existingCase = findCaseById(caseId);
            if (existingCase == null) {
                return Mono.just(new ResponseEntity<>(String.format("LoggedCase with is %s not found", caseId), HttpStatus.BAD_REQUEST));
            }
            if (existingCase.isClosed()) {
                return Mono.just(new ResponseEntity<>("The logged case is already closed", HttpStatus.BAD_REQUEST));
            }
            AuthInfo user = authInfoService.getUserById(userId);
            if (user == null) {
                return Mono.just(new ResponseEntity<>(String.format("User with is %s not found", userId), HttpStatus.BAD_REQUEST));
            }
            if (!userCanUpdateCase(user)) {
                return Mono.just(new ResponseEntity<>("User cannot add comment to logged case,please check user role and user status", HttpStatus.BAD_REQUEST));
            }

            LoggedCaseComment caseComment = new LoggedCaseComment();
            caseComment.setUserId(userId);
            caseComment.setComment(caseCommentCreateDto.getComment());
            caseComment.setCommentTime(LocalDateTime.now());
            existingCase.getCaseComments().add(caseComment);
            mongoRepositoryReactive.saveOrUpdate(existingCase);

            String verbiage = String.format("Added Comment to Logged Case, Ticket id: -> %s ,Comment -> \"%s\"",
                    existingCase.getTicketId(), caseComment.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(loggedCaseAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), existingCase.getReportedEntityName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(existingCase.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment to logged case", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllLoggedCaseStatus() {
        try {
            ArrayList<LoggedCaseStatus> loggedCaseStatuses = (ArrayList<LoggedCaseStatus>) mongoRepositoryReactive
                    .findAll(new Query(), CustomerComplainStatus.class).toStream().collect(Collectors.toList());

            if (loggedCaseStatuses == null || loggedCaseStatuses.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.OK));
            }
            List<EnumeratedFactDto> enumeratedFactDtos = new ArrayList<>();
            loggedCaseStatuses.forEach(loggedCaseStatus -> {
                enumeratedFactDtos.add(loggedCaseStatus.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(enumeratedFactDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while getting all logged case statuses";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public LoggedCase findCaseById(String caseId) {
        if (StringUtils.isEmpty(caseId)) {
            return null;
        }
        return (LoggedCase) mongoRepositoryReactive.findById(caseId, LoggedCase.class).block();
    }

    private LoggedCase fromLoggedCaseCreateDto(LoggedCaseCreateDto caseCreateDto) {
        LoggedCase newCase = new LoggedCase();
        newCase.setId(UUID.randomUUID().toString());
        newCase.setReporterId(caseCreateDto.getUserId());
        newCase.setCaseDetails(caseCreateDto.getCaseDetails());
        newCase.setInstitutionId(caseCreateDto.getInstitutionId());
        newCase.setAgentId(caseCreateDto.getAgentId());
        newCase.setCaseSubject(caseCreateDto.getCaseSubject());
        newCase.setLoggedCaseStatusId(LoggedCaseStatusReferenceData.OPEN_ID);
        newCase.setTicketId(generateTicketId());
        newCase.setDateTimeReported(LocalDateTime.now());
        return newCase;
    }

    private String generateTicketId() {
        int randomNumber = NumberUtil.getRandomNumberInRange(100, 3000099);
        LocalDateTime presentDateTime = LocalDateTime.now();
        return String.format("LS-CA-%s%s%s", randomNumber, presentDateTime.getHourOfDay(), presentDateTime.getMinuteOfHour(), presentDateTime.getSecondOfMinute());
    }

    private boolean userCanUpdateCase(AuthInfo user) {
        return user.getEnabled() && getValidRoleIds().contains(user.getAuthRoleId());
    }

    private List<String> getValidRoleIds() {
        List<String> validRolesIds = LSLBAuthRoleReferenceData.getLslbRoles();
        validRolesIds.add(AuthRoleReferenceData.SUPER_ADMIN_ID);
        return validRolesIds;
    }
}
