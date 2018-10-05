package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AgentApprovalRequest;
import com.software.finatech.lslb.cms.service.domain.AgentInstitution;
import com.software.finatech.lslb.cms.service.dto.AgentCreateDto;
import com.software.finatech.lslb.cms.service.dto.AgentDto;
import com.software.finatech.lslb.cms.service.dto.AgentInstitutionCreateDto;
import com.software.finatech.lslb.cms.service.dto.AgentUpdateDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AgentApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AgentService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.LicenseValidatorUtil;
import com.software.finatech.lslb.cms.service.util.AgentUserCreator;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class AgentServiceImpl implements AgentService {
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private AuthInfoService authInfoService;
    private LicenseValidatorUtil licenseValidatorUtil;
    private AgentUserCreator agentUserCreatorAsync;

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final Logger logger = LoggerFactory.getLogger(AgentServiceImpl.class);


    @Autowired
    public AgentServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                            AuthInfoService authInfoService,
                            LicenseValidatorUtil licenseValidatorUtil,
                            AgentUserCreator agentUserCreatorAsync) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.authInfoService = authInfoService;
        this.licenseValidatorUtil = licenseValidatorUtil;
        this.agentUserCreatorAsync = agentUserCreatorAsync;
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

    @Override
    public Mono<ResponseEntity> createAgent(AgentCreateDto agentCreateDto) {
        try {
            Mono<ResponseEntity> validateCreateAgent = validateCreateAgent(agentCreateDto);
            if (validateCreateAgent != null) {
                return validateCreateAgent;
            }
            Agent agent = fromCreateAgentDto(agentCreateDto);
            saveAgent(agent);
            AgentApprovalRequest agentApprovalRequest = fromAgentCreateDto(agentCreateDto, agent);
            mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
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
    public Mono<ResponseEntity> updateAgent(AgentUpdateDto agentUpdateDto) {
        try {
            String agentId = agentUpdateDto.getId();
            Agent agent = findById(agentId);
            if (agent == null) {
                return Mono.just(new ResponseEntity<>(String.format("No agent found with id %s", agentId), HttpStatus.BAD_REQUEST));
            }
            if (!StringUtils.equals(agent.getEmailAddress(), agentUpdateDto.getEmailAddress())) {
                String email = agentUpdateDto.getEmailAddress();
                Query queryForAgentWithExistingEmail = new Query();
                queryForAgentWithExistingEmail.addCriteria(Criteria.where("emailAddress").is(email));
                Agent existingAgentWithEmail = (Agent) mongoRepositoryReactive.find(queryForAgentWithExistingEmail, Agent.class).block();
                if (existingAgentWithEmail != null) {
                    return Mono.just(new ResponseEntity<>(String.format("There is an existing agent with email %s", email), HttpStatus.BAD_REQUEST));
                }
            }

            LocalDate dateOfBirth = FORMATTER.parseLocalDate(agentUpdateDto.getDateOfBirth());
            agent.setDateOfBirth(dateOfBirth);
            agent.setResidentialAddress(agentUpdateDto.getResidentialAddress());
            agent.setEmailAddress(agentUpdateDto.getEmailAddress());
            agent.setMeansOfId(agentUpdateDto.getMeansOfId());
            agent.setIdNumber(agentUpdateDto.getIdNumber());
            agent.setBvn(agentUpdateDto.getBvn());
            agent.setLastName(agentUpdateDto.getLastName());
            agent.setFirstName(agentUpdateDto.getFirstName());
            agent.setPhoneNumber(agentUpdateDto.getPhoneNumber());
            saveAgent(agent);
            return Mono.just(new ResponseEntity<>(agent.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating agent", e);
        }
    }

    @Override
    public Mono<ResponseEntity> createAgentUnderInstitution(AgentInstitutionCreateDto agentInstitutionCreateDto) {
        try {
            String institutionId = agentInstitutionCreateDto.getInstitutionId();
            String gameTypeId = agentInstitutionCreateDto.getGameTypeId();

            //TODO: MAKE SURE VALIDATION IS MADE FOR INSTITUTION LICENSE AND GAME TYPE
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
            return Mono.just(new ResponseEntity<>("Agent creation has been submitted for approval", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while trying to create agent under institution", e);
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
        Agent agent = (Agent) mongoRepositoryReactive.find(queryForAgentWithEmail, Agent.class).block();
        if (agent != null) {
            return Mono.just(new ResponseEntity<>(String.format("An agent already exist with the email address %s", email), HttpStatus.BAD_REQUEST));
        }
        //TODO: VALIDATE THE INSTITUTION CREATING AN AGENT IF IT HAS LICENCE FOR THE CATEGORY
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
        return agent;
    }

    @Override
    public List<Agent> getAllUncreatedOnVGPay() {
        Query query = new Query();
        query.addCriteria(Criteria.where("customerCreatedOnVGPay").is(false));
        return (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
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
}
