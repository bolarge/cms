package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.AgentService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.LicenseValidatorUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AgentCreationNotifierAsync;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;
import static com.software.finatech.lslb.cms.service.util.NumberUtil.generateAgentId;

@Service
public class AgentServiceImpl implements AgentService {
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private LicenseValidatorUtil licenseValidatorUtil;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;
    private AgentCreationNotifierAsync agentCreationNotifierAsync;
    private AuthInfoService authInfoService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final Logger logger = LoggerFactory.getLogger(AgentServiceImpl.class);
    private static final String agentAuditActionId = AuditActionReferenceData.AGENT_ID;


    @Autowired
    public AgentServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                            LicenseValidatorUtil licenseValidatorUtil,
                            SpringSecurityAuditorAware springSecurityAuditorAware,
                            AuditLogHelper auditLogHelper,
                            AgentCreationNotifierAsync agentCreationNotifierAsync,
                            AuthInfoService authInfoService) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.licenseValidatorUtil = licenseValidatorUtil;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
        this.agentCreationNotifierAsync = agentCreationNotifierAsync;
        this.authInfoService = authInfoService;
    }

    @Override
    public Mono<ResponseEntity> findAllAgents(int page,
                                              int pageSize,
                                              String sortDirection,
                                              String sortProperty,
                                              String institutionIds,
                                              String gameTypeIds,
                                              String agentStatusId,
                                              HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionIds)) {
                List<String> institutionIdList = Arrays.asList(institutionIds.split(","));
                query.addCriteria(Criteria.where("institutionIds").in(institutionIdList));
            }
            if (!StringUtils.isEmpty(gameTypeIds)) {
                List<String> gameTypeIdList = Arrays.asList(gameTypeIds.split("\\s*,\\s*"));
                query.addCriteria(Criteria.where("gameTypeIds").in(gameTypeIdList));
            }
            if (!StringUtils.isEmpty(agentStatusId)) {
                query.addCriteria(Criteria.where("agentStatusId").is(agentStatusId));
            }

            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, Agent.class).block();
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

            ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
            if (agents.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<AgentDto> agentDtos = new ArrayList<>();

            agents.forEach(agent -> {
                agentDtos.add(agent.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(agentDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get agents";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public Mono<ResponseEntity> findAgentsBySearchKey(String searchKey) {
        try {
            Query query = new Query();
            Criteria criteria = new Criteria();
            if (!StringUtils.isEmpty(searchKey)) {
                criteria.orOperator(Criteria.where("agentId").regex(searchKey, "i"), Criteria.where("fullName").regex(searchKey, "i"));
            }
            query.addCriteria(criteria);
            query.with(PageRequest.of(0, 20));
            ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
            if (agents == null || agents.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<AgentDto> agentDtos = new ArrayList<>();
            agents.forEach(agent -> {
                AgentDto dto = new AgentDto();
                dto.setId(agent.getId());
                dto.setFullName(agent.getFullName());
                agentDtos.add(dto);
            });
            return Mono.just(new ResponseEntity<>(agentDtos, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while searching agents by key", e);
        }
    }

    //Creates an agent approval request to create  a new agent
    @Override
    public Mono<ResponseEntity> createAgent(AgentCreateDto agentCreateDto, HttpServletRequest request) {
        try {
            Mono<ResponseEntity> validateCreateAgent = validateCreateAgent(agentCreateDto);
            if (validateCreateAgent != null) {
                return validateCreateAgent;
            }
            PendingAgent agent = fromCreateAgentDto(agentCreateDto);
            mongoRepositoryReactive.saveOrUpdate(agent);
            AgentApprovalRequest agentApprovalRequest = fromAgentCreateDto(agentCreateDto, agent);
            mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);

            String verbiage = String.format("Created agent approval request ->  Type :%s, Agent Name -> %s",
                    agentApprovalRequest.getAgentApprovalRequestTypeName(), agent.getFullName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(agentAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), agentApprovalRequest.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            agentCreationNotifierAsync.sendNewAgentApprovalRequestToLSLBAdmin(agentApprovalRequest);
            return Mono.just(new ResponseEntity<>(agent.convertToDto(), HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format for date of birth , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "an error occurred when creating agent", e);
        }
    }

    private AgentApprovalRequest fromAgentCreateDto(AgentCreateDto agentCreateDto, PendingAgent pendingAgent) {
        AgentApprovalRequest agentApprovalRequest = new AgentApprovalRequest();
        agentApprovalRequest.setId(UUID.randomUUID().toString());
        agentApprovalRequest.setPendingAgentId(pendingAgent.getId());
        agentApprovalRequest.setGameTypeId(agentCreateDto.getGameTypeId());
        agentApprovalRequest.setInstitutionId(agentCreateDto.getInstitutionId());
        agentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
        agentApprovalRequest.setAgentApprovalRequestTypeId(AgentApprovalRequestTypeReferenceData.CREATE_AGENT_ID);
        return agentApprovalRequest;
    }


    @Override
    public Agent findAgentById(String agentId) {
        return (Agent) mongoRepositoryReactive.findById(agentId, Agent.class).block();
    }

    @Override
    public Mono<ResponseEntity> updateAgent(AgentUpdateDto agentUpdateDto, HttpServletRequest request) {
        try {
            String agentId = agentUpdateDto.getId();
            Agent agent = findAgentById(agentId);
            if (agent == null) {
                return Mono.just(new ResponseEntity<>(String.format("No agent found with id %s", agentId), HttpStatus.BAD_REQUEST));
            }

            Pair<String, String> oldPhoneAndAddress = new ImmutablePair<>(agent.getPhoneNumber(), agent.getResidentialAddress());
            agent.setResidentialAddress(agentUpdateDto.getResidentialAddress());
            agent.setPhoneNumber(agentUpdateDto.getPhoneNumber());
            saveAgent(agent);

            Pair<String, String> newPhoneAndAddress = new ImmutablePair<>(agent.getPhoneNumber(), agent.getResidentialAddress());
            String verbiage = String.format("Updated Agent Details -> Agent Id: %s , Agent Name -> %s", agent.getAgentId(), agent.getFullName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(agentAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            agentCreationNotifierAsync.sentAgentUpdateEmailToAgentInstitutions(agent, oldPhoneAndAddress, newPhoneAndAddress);
            return Mono.just(new ResponseEntity<>(agent.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating agent", e);
        }
    }


    //creates an agent approval request to add agent to institution
    @Override
    public Mono<ResponseEntity> createAgentUnderInstitution(AgentInstitutionCreateDto agentInstitutionCreateDto, HttpServletRequest request) {
        try {
            String institutionId = agentInstitutionCreateDto.getInstitutionId();
            String gameTypeId = agentInstitutionCreateDto.getGameTypeId();
            Mono<ResponseEntity> validateInstitutionLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
            if (validateInstitutionLicenseResponse != null) {
                return validateInstitutionLicenseResponse;
            }
            Query query = new Query();
            query.addCriteria(Criteria.where("emailAddress").is(agentInstitutionCreateDto.getAgentEmailAddress()));
            query.addCriteria(Criteria.where("enabled").is(true));
            Agent agentWithEmail = (Agent) mongoRepositoryReactive.find(query, Agent.class).block();
            if (agentWithEmail == null) {
                return Mono.just(new ResponseEntity<>("Agent does not exist, please create agent with full details", HttpStatus.NOT_FOUND));
            }

            AgentApprovalRequest agentApprovalRequest = fromAgentInstitutionCreate(agentInstitutionCreateDto, agentWithEmail);
            mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);

            String verbiage = String.format("Created Agent approval request ->  Type :%s", agentApprovalRequest.getAgentApprovalRequestTypeName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(agentAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), agentApprovalRequest.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            agentCreationNotifierAsync.sendNewAgentApprovalRequestToLSLBAdmin(agentApprovalRequest);
            return Mono.just(new ResponseEntity<>("Agent creation has been submitted for approval", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while trying to create agent under institution", e);
        }
    }

    @Override
    public Mono<ResponseEntity> validateAgentProfileOnSystem(AgentValidationDto agentValidationDto) {
        try {
            String email = agentValidationDto.getEmail();
            String agentId = agentValidationDto.getAgentId();
            String institutionId = agentValidationDto.getInstitutionId();
            String gameTypeId = agentValidationDto.getGameTypeId();
            Mono<ResponseEntity> validateInstitutionLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
            if (validateInstitutionLicenseResponse != null) {
                return validateInstitutionLicenseResponse;
            }
            Query query = Query.query(new Criteria().orOperator(Criteria.where("emailAddress").is(email), Criteria.where("agentId").is(agentId)));
            Agent agent = (Agent) mongoRepositoryReactive.find(query, Agent.class).block();
            if (agent == null) {
                return Mono.just(new ResponseEntity<>("No Record found", HttpStatus.NOT_FOUND));
            }
            return Mono.just(new ResponseEntity<>(agent.convertToFullDetailDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while validating agent", e);
        }
    }

    private AgentApprovalRequest fromAgentInstitutionCreate(AgentInstitutionCreateDto agentInstitutionCreateDto, Agent agent) {
        AgentApprovalRequest agentApprovalRequest = new AgentApprovalRequest();
        agentApprovalRequest.setId(UUID.randomUUID().toString());
        agentApprovalRequest.setInstitutionId(agentInstitutionCreateDto.getInstitutionId());
        agentApprovalRequest.setAgentId(agent.getId());
        agentApprovalRequest.setGameTypeId(agentInstitutionCreateDto.getGameTypeId());
        agentApprovalRequest.setBusinessAddressList(agentInstitutionCreateDto.getBusinessAddressList());
        agentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
        agentApprovalRequest.setAgentApprovalRequestTypeId(AgentApprovalRequestTypeReferenceData.ADD_INSTITUTION_TO_AGENT_ID);
        return agentApprovalRequest;
    }


    @Override
    public void saveAgent(Agent agent) {
        mongoRepositoryReactive.saveOrUpdate(agent);
    }

    /**
     * Checks if agent has an email that is not existing,
     * also check if institutions attached to agent has license status proper for gametypes
     *
     * @param agentCreateDto
     * @return
     */
    private Mono<ResponseEntity> validateCreateAgent(AgentCreateDto agentCreateDto) {
        //check if agent exists with agent email
        String email = agentCreateDto.getEmailAddress();
        Query queryForAgentWithEmail = new Query();
        queryForAgentWithEmail.addCriteria(Criteria.where("emailAddress").is(email));
        Agent agentWithEmail = (Agent) mongoRepositoryReactive.find(queryForAgentWithEmail, Agent.class).block();
        if (agentWithEmail != null) {
            return Mono.just(new ResponseEntity<>(String.format("An agent already exist with the email address %s", email), HttpStatus.BAD_REQUEST));
        }
        //check if agent exist with bvn
        String bvn = agentCreateDto.getBvn();
        Query queryForAgentWithBvn = Query.query(Criteria.where("bvn").is(bvn));
        Agent agentWithBvn = (Agent) mongoRepositoryReactive.find(queryForAgentWithBvn, Agent.class).block();
        if (agentWithBvn != null) {
            return Mono.just(new ResponseEntity<>(String.format("An agent already exist with the bvn %s", bvn), HttpStatus.BAD_REQUEST));
        }
        Mono<ResponseEntity> validateLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(agentCreateDto.getInstitutionId(), agentCreateDto.getGameTypeId());
        if (validateLicenseResponse != null) {
            return validateLicenseResponse;
        }
        AuthInfo userWithAgentEmail = authInfoService.findActiveUserWithEmail(email);
        if (userWithAgentEmail != null) {
            return Mono.just(new ResponseEntity<>("A user already exit with agent email", HttpStatus.BAD_REQUEST));
        }
        PendingAgent pendingAgentWithEmail = findPendingApprovalAgentWithEmail(email);
        if (pendingAgentWithEmail != null) {
            return Mono.just(new ResponseEntity<>("An Agent is already pending approval with the same email address", HttpStatus.BAD_REQUEST));
        }
        return null;
    }

    private PendingAgent fromCreateAgentDto(AgentCreateDto agentCreateDto) {
        String gameTypeId = agentCreateDto.getGameTypeId();
        String institutionId = agentCreateDto.getInstitutionId();
        PendingAgent agent = new PendingAgent();
        agent.setId(UUID.randomUUID().toString());
        agent.setFirstName(agentCreateDto.getFirstName());
        agent.setLastName(agentCreateDto.getLastName());
        agent.setEmailAddress(agentCreateDto.getEmailAddress());
        agent.setPhoneNumber(agentCreateDto.getPhoneNumber());
        agent.setBvn(agentCreateDto.getBvn());
        agent.setFullName(agentCreateDto.getFullName());
        agent.setMeansOfId(agentCreateDto.getMeansOfId());
        agent.setIdNumber(agentCreateDto.getIdNumber());
        agent.setResidentialAddress(agentCreateDto.getResidentialAddress());
        agent.setDob(new LocalDate(agentCreateDto.getDateOfBirth()));
        agent.setDateOfBirth(agentCreateDto.getDateOfBirth());
        Set<String> gameTypeIds = new HashSet<>();
        Set<String> institutionIds = new HashSet<>();
        AgentInstitution agentInstitution = new AgentInstitution();
        gameTypeIds.add(gameTypeId);
        institutionIds.add(institutionId);
        agentInstitution.setBusinessAddressList(agentCreateDto.getBusinessAddressList());
         agentInstitution.getGameTypeIds().add(gameTypeId);
        agentInstitution.setInstitutionId(institutionId);
        List<AgentInstitution> agentInstitutions = new ArrayList<>();
        agentInstitutions.add(agentInstitution);
        agent.setEnabled(false);
        agent.setGameTypeIds(gameTypeIds);
        agent.setInstitutionIds(institutionIds);
        agent.setAgentInstitutions(agentInstitutions);
        agent.setBusinessAddresses(agentInstitution.getBusinessAddressList());
        agent.setAgentId(generateAgentId());
        agent.setAgentStatusId(AgentStatusReferenceData.IN_ACTIVE_ID);
        agent.setMiddleName(agentCreateDto.getMiddleName());
        return agent;
    }


    @Override
    public Mono<ResponseEntity> getAgentFullDetailById(String agentId) {
        try {
            Agent agent = findAgentById(agentId);
            if (agent == null) {
                return Mono.just(new ResponseEntity<>(String.format("Agent with id %s does not exist", agentId), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(agent.convertToFullDetailDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting agent by id", e);
        }
    }

    @Override
    public Mono<ResponseEntity> createUserForAgent(String agentId) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            Agent agent = findAgentById(agentId);
            if (agent == null) {
                return Mono.just(new ResponseEntity<>(String.format("Agent with id %s not found", agentId), HttpStatus.BAD_REQUEST));
            }
            PendingAuthInfo pendingAuthInfo = new PendingAuthInfo();
            pendingAuthInfo.setId(UUID.randomUUID().toString());
            pendingAuthInfo.setAuthRoleId(LSLBAuthRoleReferenceData.AGENT_ROLE_ID);
            pendingAuthInfo.setPhoneNumber(agent.getPhoneNumber());
            pendingAuthInfo.setEmailAddress(agent.getEmailAddress());
            pendingAuthInfo.setFirstName(agent.getFirstName());
            pendingAuthInfo.setFullName(agent.getFullName());
            pendingAuthInfo.setLastName(agent.getLastName());
            pendingAuthInfo.setTitle(agent.getTitle());
            pendingAuthInfo.setAgentId(agentId);

            UserApprovalRequest approvalRequest = new UserApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setUserApprovalRequestTypeId(UserApprovalRequestTypeReferenceData.CREATE_USER_ID);
            approvalRequest.setPendingAuthInfoId(pendingAuthInfo.getId());
            approvalRequest.setInitiatorId(loggedInUser.getId());
            approvalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
            mongoRepositoryReactive.saveOrUpdate(pendingAuthInfo);
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);
            return Mono.just(new ResponseEntity<>(approvalRequest.convertToHalfDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating user for agent", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllAgentStatus() {
        return getAllEnumeratedEntity("AgentStatus",AgentStatus.class);
    }

    @Override
    public Mono<ResponseEntity> getAllAgentGender() {
        return getAllEnumeratedEntity("Gender", Gender.class);
    }

    @Override
    public Agent findAgentByAgentNumber(String agentNumber) {
        return (Agent) mongoRepositoryReactive.find(Query.query(Criteria.where("agentId").is(agentNumber)), Agent.class).block();
    }

    private PendingAgent findPendingApprovalAgentWithEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("emailAddress").is(email));
        query.addCriteria(Criteria.where("approvalRequestStatusId").is(ApprovalRequestStatusReferenceData.PENDING_ID));
        return (PendingAgent) mongoRepositoryReactive.find(query, PendingAgent.class).block();
    }
}
