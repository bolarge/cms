package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
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
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class LoggedCaseServiceImpl implements LoggedCaseService {
    private static final Logger logger = LoggerFactory.getLogger(LoggedCaseServiceImpl.class);
    private static final String loggedCaseAuditActionId = AuditActionReferenceData.CASE_ID;

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private LoggedCaseMailSenderAsync loggedCaseMailSenderAsync;
    private AuditLogHelper auditLogHelper;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private LicenseService licenseService;

    @Autowired
    public LoggedCaseServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                 LoggedCaseMailSenderAsync loggedCaseMailSenderAsync,
                                 AuditLogHelper auditLogHelper, SpringSecurityAuditorAware springSecurityAuditorAware,
                                 LicenseService licenseService) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.loggedCaseMailSenderAsync = loggedCaseMailSenderAsync;
        this.auditLogHelper = auditLogHelper;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.licenseService = licenseService;
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
                                                   String categoryId,
                                                   String typeId,
                                                   String gameTypeId,
                                                   String outcomeId,
                                                   String licenseTypeId,
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
            if (!StringUtils.isEmpty(loggedCaseStatusId)) {
                query.addCriteria(Criteria.where("loggedCaseStatusId").is(loggedCaseStatusId));
            }
            if (!StringUtils.isEmpty(categoryId)) {
                query.addCriteria(Criteria.where("caseAndComplainCategoryId").is(categoryId));
            }
            if (!StringUtils.isEmpty(typeId)) {
                query.addCriteria(Criteria.where("caseAndComplainTypeId").is(typeId));
            }
            if (!StringUtils.isEmpty(gameTypeId)) {
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            }
            if (!StringUtils.isEmpty(outcomeId)) {
                query.addCriteria(Criteria.where("loggedCaseOutcomeId").is(outcomeId));
            }
            if (!StringUtils.isEmpty(licenseTypeId)) {
                query.addCriteria(Criteria.where("licenseTypeId").is(licenseTypeId));
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
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting logged cases", e);
        }
    }

    @Override
    public Mono<ResponseEntity> createCase(LoggedCaseCreateDto loggedCaseCreateDto, HttpServletRequest request) {
        try {
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }
            if (!canUpdateCase(user)) {
                return Mono.just(new ResponseEntity<>("User  cannot create logged case, please check user role and user status", HttpStatus.BAD_REQUEST));
            }
            if ((!StringUtils.equals(CaseAndComplainTypeReferenceData.OTHERS_ID, loggedCaseCreateDto.getCaseAndComplainTypeId())
                    && !StringUtils.isEmpty(loggedCaseCreateDto.getOtherTypeName()))
                    ||
                    (!StringUtils.equals(CaseAndComplainCategoryReferenceData.OTHERS_ID, loggedCaseCreateDto.getCaseAndComplainCategoryId())
                            && !StringUtils.isEmpty(loggedCaseCreateDto.getOtherCategoryName()))) {
                return Mono.just(new ResponseEntity<>("Invalid Request", HttpStatus.BAD_REQUEST));
            }
            LoggedCase loggedCase = fromLoggedCaseCreateDto(loggedCaseCreateDto);
            mongoRepositoryReactive.saveOrUpdate(loggedCase);
            String verbiage = String.format("Created logged case ,Ticket id : -> %s, Type -> %s, Category -> %s , Logged Against -> %s",
                    loggedCase.getTicketId(), loggedCase.getCaseAndComplainType(), loggedCase.getCaseAndComplainCategory(), loggedCase.getLicenseType());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(loggedCaseAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), loggedCase.getReportedEntityName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            loggedCaseMailSenderAsync.sendNewCaseNotificationToLslbUsersThatCanReceive(loggedCase);

            //close attached entities(Report or customer complaint) attached to case
            closeAttachedEntities(loggedCase);
            return Mono.just(new ResponseEntity<>(loggedCase.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating case", e);
        }
    }

    @Override
    public Mono<ResponseEntity> addLoggedCaseAction(LoggedCaseActionCreateDto caseActionCreateDto, HttpServletRequest request) {
        try {
            String caseId = caseActionCreateDto.getCaseId();
            String caseStatusId = caseActionCreateDto.getCaseStatusId();
            LoggedCase existingCase = findCaseById(caseId);
            if (existingCase == null) {
                return Mono.just(new ResponseEntity<>(String.format("LoggedCase with is %s not found", caseId), HttpStatus.BAD_REQUEST));
            }

            LoggedCaseStatus oldCaseStatus = existingCase.getCaseStatus(existingCase.getLoggedCaseStatusId());
            if (existingCase.isClosed()) {
                return Mono.just(new ResponseEntity<>("The logged case is already closed", HttpStatus.BAD_REQUEST));
            }
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }
            if (!canUpdateCase(user)) {
                return Mono.just(new ResponseEntity<>("User cannot change logged case status, please check user role and user status", HttpStatus.BAD_REQUEST));
            }

            if (!LoggedCaseStatusReferenceData.getCaseStatusIds().contains(caseStatusId)) {
                return Mono.just(new ResponseEntity<>(String.format("LoggedCase status with id %s does not exist on system", caseStatusId), HttpStatus.BAD_REQUEST));
            }
            LoggedCaseAction caseAction = new LoggedCaseAction();
            caseAction.setActionTime(LocalDateTime.now());
            caseAction.setUserId(user.getId());
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
            LoggedCase existingCase = findCaseById(caseId);
            if (existingCase == null) {
                return Mono.just(new ResponseEntity<>(String.format("LoggedCase with is %s not found", caseId), HttpStatus.BAD_REQUEST));
            }
            if (existingCase.isClosed()) {
                return Mono.just(new ResponseEntity<>("The logged case is already closed", HttpStatus.BAD_REQUEST));
            }
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }
            if (!canUpdateCase(user)) {
                return Mono.just(new ResponseEntity<>("User cannot add comment to logged case,please check user role and user status", HttpStatus.BAD_REQUEST));
            }

            LoggedCaseComment caseComment = new LoggedCaseComment();
            caseComment.setUserId(user.getId());
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
        return ReferenceDataUtil.getAllEnumeratedEntity("LoggedCaseStatus");
    }

    @Override
    public LoggedCase findCaseById(String caseId) {
        if (StringUtils.isEmpty(caseId)) {
            return null;
        }
        return (LoggedCase) mongoRepositoryReactive.findById(caseId, LoggedCase.class).block();
    }

    @Override
    public Mono<ResponseEntity> getLoggedCaseFullDetail(String loggedCaseId) {
        try {
            LoggedCase loggedCase = findCaseById(loggedCaseId);
            if (loggedCase == null) {
                return Mono.just(new ResponseEntity<>(String.format("Case with id %s does not exist", loggedCaseId), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(loggedCase.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting logged case full detail", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllCaseAndComplainType() {
        return ReferenceDataUtil.getAllEnumeratedEntity("CaseAndComplainType");
    }

    @Override
    public Mono<ResponseEntity> getAllCaseAndComplainCategory() {
        return ReferenceDataUtil.getAllEnumeratedEntity("CaseAndComplainCategory");
    }

    @Override
    public Mono<ResponseEntity> getAllCaseOutcomes() {
        return ReferenceDataUtil.getAllEnumeratedEntity("LoggedCaseOutcome");
    }

    @Override
    public Mono<ResponseEntity> takeActionOnCase(CaseOutcomeRequest caseActionRequest, HttpServletRequest request) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            LoggedCase loggedCase = findCaseById(caseActionRequest.getLoggedCaseId());
            if (loggedCase == null) {
                return Mono.just(new ResponseEntity<>(String.format("Logged case with id %s not found", caseActionRequest.getLoggedCaseId()), HttpStatus.BAD_REQUEST));
            }
            if (loggedCase.isClosed()) {
                return Mono.just(new ResponseEntity<>("The case is already closed", HttpStatus.BAD_REQUEST));
            }
            LoggedCaseOutcome loggedCaseOutcome = findLoggedCaseOutcome(caseActionRequest.getCaseOutcomeId());
            if (loggedCaseOutcome == null) {
                return Mono.just(new ResponseEntity<>(String.format("Logged case outcome with id %s not found",
                        caseActionRequest.getCaseOutcomeId()), HttpStatus.BAD_REQUEST));
            }

            makeOutComeEffectOnOperatorLicense(loggedCase, caseActionRequest);
            loggedCase.setLoggedCaseOutcomeId(caseActionRequest.getCaseOutcomeId());
            loggedCase.setLoggedCaseStatusId(LoggedCaseStatusReferenceData.CLOSED_ID);
            loggedCase.setOutcomeReason(caseActionRequest.getReason());

            LoggedCaseAction action = new LoggedCaseAction();
            action.setActionTime(LocalDateTime.now());
            action.setUserId(loggedInUser.getId());
            action.setLslbCaseOutcomeId(caseActionRequest.getCaseOutcomeId());
            loggedCase.getCaseActions().add(action);
            mongoRepositoryReactive.saveOrUpdate(loggedCase);

            String verbiage = String.format("Made Outcome on Logged Case, Ticket id: -> %s, Outcome -> %s",
                    loggedCase.getTicketId(), loggedCaseOutcome);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(loggedCaseAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), loggedCase.getReportedEntityName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(loggedCase.convertToDto(), HttpStatus.OK));
        } catch (LicenseServiceException e) {
            return Mono.just(new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while taking action on case", e);
        }
    }

    private void makeOutComeEffectOnOperatorLicense(LoggedCase loggedCase, CaseOutcomeRequest caseOutcomeRequest) throws LicenseServiceException {
        License license = new License();
        if (loggedCase.isOutcomeLicenseTerminated() ||
                loggedCase.isOutcomeLicenseSuspended() ||
                loggedCase.isOutcomeLicenseRevoked()) {
            license = licenseService.findPresentLicenseForCase(loggedCase);
            if (license == null) {
                throw new LicenseServiceException("Offender does not have license in category, Kindly close the case");
            }
        }
        if (loggedCase.isOutcomeLicenseRevoked()) {
            licenseService.changeLicenseStatusForCaseOutcome(license,LicenseStatusReferenceData.LICENSE_REVOKED_ID);
            loggedCaseMailSenderAsync.sendOutcomeNotificationToOffender(loggedCase);
        }
        if (loggedCase.isOutcomeLicenseSuspended()) {
            licenseService.changeLicenseStatusForCaseOutcome(license,LicenseStatusReferenceData.LICENSE_SUSPENDED_ID);
            loggedCaseMailSenderAsync.sendOutcomeNotificationToOffender(loggedCase);
        }
        if (loggedCase.isOutcomeLicenseTerminated()) {
            licenseService.changeLicenseStatusForCaseOutcome(license,LicenseStatusReferenceData.LICENSE_TERMINATED_ID);
            loggedCaseMailSenderAsync.sendOutcomeNotificationToOffender(loggedCase);
        }
        if (loggedCase.isOutcomePenalty()) {
            loggedCaseMailSenderAsync.sendPenaltyMailToOffender(loggedCase, caseOutcomeRequest.getCasePenaltyParams());
        }
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
        newCase.setGameTypeId(caseCreateDto.getGameTypeId());
        newCase.setDateTimeReported(LocalDateTime.now());
        newCase.setLicenseTypeId(caseCreateDto.getLicenseTypeId());
        newCase.setCaseAndComplainCategoryId(caseCreateDto.getCaseAndComplainCategoryId());
        newCase.setCaseAndComplainTypeId(caseCreateDto.getCaseAndComplainTypeId());
        newCase.setGamingMachineId(caseCreateDto.getGamingMachineId());
        newCase.setGamingTerminalId(caseCreateDto.getGamingTerminalId());
        newCase.setOtherCategoryName(caseCreateDto.getOtherCategoryName());
        newCase.setOtherTypeName(caseCreateDto.getOtherTypeName());
        newCase.setCustomerComplaintId(caseCreateDto.getCustomerComplaintId());
        newCase.setLoggedReportId(caseCreateDto.getLoggedReportId());
        return newCase;
    }

    private String generateTicketId() {
        int randomNumber = NumberUtil.getRandomNumberInRange(100, 3000);
        LocalDateTime presentDateTime = LocalDateTime.now();
        return String.format("LS-CA-%s%s%s%s", randomNumber, presentDateTime.getHourOfDay(), presentDateTime.getMinuteOfHour(), presentDateTime.getSecondOfMinute());
    }

    private boolean canUpdateCase(AuthInfo user) {
        if (user == null) {
            return false;
        }
        return user.getEnabled() && getValidRoleIds().contains(user.getAuthRoleId());
    }

    private LoggedCaseOutcome findLoggedCaseOutcome(String id) {
        Collection<EnumeratedFact> factObjects = ReferenceDataUtil.getAllEnumeratedFacts("LoggedCaseOutcome");
        if (!factObjects.isEmpty()) {
            for (FactObject factObject : factObjects) {
                LoggedCaseOutcome loggedCaseOutcome = (LoggedCaseOutcome) factObject;
                if (StringUtils.equals(id, loggedCaseOutcome.getId())) {
                    return loggedCaseOutcome;
                }
            }
        }
        return null;
    }

    private List<String> getValidRoleIds() {
        List<String> validRolesIds = LSLBAuthRoleReferenceData.getLslbRoles();
        validRolesIds.add(AuthRoleReferenceData.SUPER_ADMIN_ID);
        return validRolesIds;
    }

    private void closeAttachedEntities(LoggedCase loggedCase) {
        InspectionForm inspectionForm = loggedCase.getInspectionForm();
        if (inspectionForm != null) {
            inspectionForm.setStatus(InspectionStatusReferenceData.CLOSED);
            inspectionForm.setLoggedCaseId(loggedCase.getId());
            mongoRepositoryReactive.saveOrUpdate(inspectionForm);
        }

        CustomerComplain customerComplain = loggedCase.getCustomerComplaint();
        if (customerComplain != null) {
            customerComplain.setLoggedCaseId(loggedCase.getId());
            customerComplain.setCustomerComplainStatusId(CustomerComplainStatusReferenceData.CLOSED_ID);
            mongoRepositoryReactive.saveOrUpdate(customerComplain);
        }
    }
}
