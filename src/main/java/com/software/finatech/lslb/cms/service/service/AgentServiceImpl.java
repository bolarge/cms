package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AgentApprovalRequest;
import com.software.finatech.lslb.cms.service.domain.AgentInstitution;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AgentApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AgentService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.LicenseValidatorUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AgentCreationNotifierAsync;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class AgentServiceImpl implements AgentService {
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private LicenseValidatorUtil licenseValidatorUtil;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;
    private AgentCreationNotifierAsync agentCreationNotifierAsync;

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final Logger logger = LoggerFactory.getLogger(AgentServiceImpl.class);
    private static final String agentAuditActionId = AuditActionReferenceData.AGENT_ID;


    @Autowired
    public AgentServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                            LicenseValidatorUtil licenseValidatorUtil,
                            SpringSecurityAuditorAware springSecurityAuditorAware,
                            AuditLogHelper auditLogHelper,
                            AgentCreationNotifierAsync agentCreationNotifierAsync) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.licenseValidatorUtil = licenseValidatorUtil;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
        this.agentCreationNotifierAsync = agentCreationNotifierAsync;
    }

    @Override
    public Mono<ResponseEntity> findAllAgents(int page,
                                              int pageSize,
                                              String sortDirection,
                                              String sortProperty,
                                              String institutionIds,
                                              String gameTypeIds,
                                              HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("enabled").is(true));
            if (!StringUtils.isEmpty(institutionIds)) {
                List<String> institutionIdList = Arrays.asList(institutionIds.split(","));
                query.addCriteria(Criteria.where("institutionIds").in(institutionIdList));
            }
            if (!StringUtils.isEmpty(gameTypeIds)) {
                List<String> gameTypeIdList = Arrays.asList(gameTypeIds.split("\\s*,\\s*"));
                query.addCriteria(Criteria.where("gameTypeIds").in(gameTypeIdList));
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
            if (agents == null || agents.isEmpty()) {
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


    //Creates an agent approval request to create  a new agent
    @Override
    public Mono<ResponseEntity> createAgent(AgentCreateDto agentCreateDto, HttpServletRequest request) {
        try {
            Mono<ResponseEntity> validateCreateAgent = validateCreateAgent(agentCreateDto);
            if (validateCreateAgent != null) {
                return validateCreateAgent;
            }
            Agent agent = fromCreateAgentDto(agentCreateDto);
            saveAgent(agent);
            AgentApprovalRequest agentApprovalRequest = fromAgentCreateDto(agentCreateDto, agent);
            mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);

            String verbiage = String.format("Created agent approval request ->  Type :%s", agentApprovalRequest.getAgentApprovalRequestTypeName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(agentAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), agentApprovalRequest.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            agentCreationNotifierAsync.sendNewAgentApprovalRequestToLSLBAdmin(agentApprovalRequest);
            return Mono.just(new ResponseEntity<>(agent.convertToDto(), HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format for date of birth , please use yyyy-MM-dd HH:mm:ss", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "an error occurred when creating agent", e);
        }

    }

    private AgentApprovalRequest fromAgentCreateDto(AgentCreateDto agentCreateDto, Agent agent) {
        AgentApprovalRequest agentApprovalRequest = new AgentApprovalRequest();
        agentApprovalRequest.setId(UUID.randomUUID().toString());
        agentApprovalRequest.setAgentId(agent.getId());
        agentApprovalRequest.setGameTypeId(agentCreateDto.getGameTypeId());
        agentApprovalRequest.setInstitutionId(agentCreateDto.getInstitutionId());
        agentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
        agentApprovalRequest.setAgentApprovalRequestTypeId(AgentApprovalRequestTypeReferenceData.CREATE_AGENT_ID);
        return agentApprovalRequest;
    }


    @Override
    public Agent findById(String agentId) {
        return (Agent) mongoRepositoryReactive.findById(agentId, Agent.class).block();
    }

    @Override
    public Mono<ResponseEntity> updateAgent(AgentUpdateDto agentUpdateDto, HttpServletRequest request) {
        try {
            String agentId = agentUpdateDto.getId();
            Agent agent = findById(agentId);
            if (agent == null) {
                return Mono.just(new ResponseEntity<>(String.format("No agent found with id %s", agentId), HttpStatus.BAD_REQUEST));
            }
            agent.setResidentialAddress(agentUpdateDto.getResidentialAddress());
            agent.setPhoneNumber(agentUpdateDto.getPhoneNumber());
            saveAgent(agent);

            String verbiage = String.format("Updated Agent Details -> Agent Id: %s ", agent.getAgentId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(agentAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

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
            if (agent != null) {
                return Mono.just(new ResponseEntity<>("No Record found", HttpStatus.NOT_FOUND));
            } else {
                return Mono.just(new ResponseEntity<>(agent.convertToFullDetailDto(), HttpStatus.OK));
            }
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
        return null;
    }

    private Agent fromCreateAgentDto(AgentCreateDto agentCreateDto) {
        String gameTypeId = agentCreateDto.getGameTypeId();
        String institutionId = agentCreateDto.getInstitutionId();
        Agent agent = new Agent();
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
        LocalDate dateOfBirth = FORMATTER.parseLocalDate(agentCreateDto.getDateOfBirth());
        agent.setDateOfBirth(dateOfBirth);
        Set<String> gameTypeIds = new HashSet<>();
        Set<String> institutionIds = new HashSet<>();
        AgentInstitution agentInstitution = new AgentInstitution();
        gameTypeIds.add(gameTypeId);
        institutionIds.add(institutionId);
        agentInstitution.setBusinessAddressList(agentCreateDto.getBusinessAddressList());
        agentInstitution.setGameTypeId(gameTypeId);
        agentInstitution.setInstitutionId(institutionId);
        List<AgentInstitution> agentInstitutions = new ArrayList<>();
        agentInstitutions.add(agentInstitution);
        agent.setEnabled(false);
        agent.setGameTypeIds(gameTypeIds);
        agent.setInstitutionIds(institutionIds);
        agent.setAgentInstitutions(agentInstitutions);
        agent.setBusinessAddresses(agentInstitution.getBusinessAddressList());
        agent.setAgentId(generateAgentId());
        return agent;
    }

    @Override
    public Mono<ResponseEntity> getAgentFullDetailById(String agentId) {
        try {
            Agent agent = findById(agentId);
            if (agent == null) {
                return Mono.just(new ResponseEntity<>(String.format("Agent with id %s does not exist", agentId), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(agent.convertToFullDetailDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting agent by id", e);
        }
    }

    private String generateAgentId() {
        return String.format("LAGOS-AG-%s", NumberUtil.getRandomNumberInRange(20, 45578994));
    }
}
