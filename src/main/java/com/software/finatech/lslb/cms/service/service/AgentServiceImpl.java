package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.controller.AuthInfoController;
import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AgentInstitution;
import com.software.finatech.lslb.cms.service.dto.AgentCreateDto;
import com.software.finatech.lslb.cms.service.dto.AgentDto;
import com.software.finatech.lslb.cms.service.dto.AgentUpdateDto;
import com.software.finatech.lslb.cms.service.dto.AuthInfoCreateDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AgentService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.LicenseValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
    private AuthInfoController authInfoController;
    private LicenseValidatorUtil licenseValidatorUtil;

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger logger = LoggerFactory.getLogger(AgentServiceImpl.class);

    @Autowired
    public AgentServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                            LicenseValidatorUtil licenseValidatorUtil,
                            AuthInfoService authInfoService,
                            AuthInfoController authInfoController) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.authInfoService = authInfoService;
        this.authInfoController = authInfoController;
        this.licenseValidatorUtil = licenseValidatorUtil;
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
            if (!StringUtils.isEmpty(institutionIds)) {
                List<String> institutionIdList = Arrays.asList(institutionIds.split("-"));
                query.addCriteria(Criteria.where("institutionIds").in(institutionIdList));
            }
            if (!StringUtils.isEmpty(gameTypeIds)) {
                List<String> gameTypeIdList = Arrays.asList(gameTypeIds.split("-"));
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
                sort = new Sort(Sort.Direction.DESC, "id");
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

            AuthInfoCreateDto agentUserCreateDto = createAuthInfoDtoFromAgent(agentCreateDto);
            authInfoService.createAuthInfo(agentUserCreateDto, authInfoController.getAppHostPort());
            return Mono.just(new ResponseEntity<>(agent, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format for date of birth , please use yyyy-MM-dd HH:mm:ss", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "an error occurred when creating agent", e);
        }

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

            Mono<ResponseEntity> validateAgentInstitutions = validateAgentInstitutions(agentUpdateDto.getAgentInstitutions());
            if (validateAgentInstitutions != null) {
                return validateAgentInstitutions;
            }

            DateTime dateOfBirth = FORMATTER.parseDateTime(agentUpdateDto.getDateOfBirth());
            agent.setDateOfBirth(dateOfBirth);
            agent.setResidentialAddress(agentUpdateDto.getResidentialAddress());
            agent.setEmailAddress(agentUpdateDto.getEmailAddress());
            agent.setBusinessAddresses(agentUpdateDto.getBusinessAddresses());
            agent.setMeansOfId(agentUpdateDto.getMeansOfId());
            agent.setIdNumber(agentUpdateDto.getIdNumber());
            agent.setBvn(agentUpdateDto.getBvn());
            agent.setPassportId(agentUpdateDto.getPassportId());
            agent.setLastName(agentUpdateDto.getLastName());
            agent.setFirstName(agentUpdateDto.getFirstName());
            agent.setPhoneNumber(agentUpdateDto.getPhoneNumber());
            Set<String> gameTypeIds = new HashSet<>();
            Set<String> institutionIds = new HashSet<>();
            for (AgentInstitution agentInstitution : agentUpdateDto.getAgentInstitutions()) {
                gameTypeIds.add(agentInstitution.getGameTypeId());
                institutionIds.add(agentInstitution.getInstitutionId());
            }
            agent.setGameTypeIds(gameTypeIds);
            agent.setInstitutionIds(institutionIds);
            agent.setAgentInstitutions(agentUpdateDto.getAgentInstitutions());
            saveAgent(agent);
            return Mono.just(new ResponseEntity<>(agent.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating agent", e);
        }
    }

    private void saveAgent(Agent agent) {
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
        //for all the agent institutions , check if the institution has a valid license for the gameType
        Mono<ResponseEntity> validateAgentInstitutionsResponse = validateAgentInstitutions(agentCreateDto.getAgentInstitutions());
        if (validateAgentInstitutionsResponse != null) {
            return validateAgentInstitutionsResponse;
        }

        return null;
    }


    /**
     * checks if the institution-andg-gametypes to be attached to an agent are valid
     *
     * @param agentInstitutions
     * @return
     */
    private Mono<ResponseEntity> validateAgentInstitutions(Set<AgentInstitution> agentInstitutions) {
        for (AgentInstitution agentInstitution : agentInstitutions) {
            String institutionId = agentInstitution.getInstitutionId();
            String gameTypeId = agentInstitution.getGameTypeId();
            Mono<ResponseEntity> validateLicenseResponse = licenseValidatorUtil.validateInstitutionGameTypeLicenseConfirmed(institutionId, gameTypeId);
            if (validateLicenseResponse != null) {
                return validateLicenseResponse;
            }
        }
        return null;
    }

    private Agent fromCreateAgentDto(AgentCreateDto agentCreateDto) {
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
        agent.setBusinessAddresses(agentCreateDto.getBusinessAddresses());
        DateTime dateOfBirth = FORMATTER.parseDateTime(agentCreateDto.getDateOfBirth());
        agent.setDateOfBirth(dateOfBirth);
        Set<String> gameTypeIds = new HashSet<>();
        Set<String> institutionIds = new HashSet<>();
        for (AgentInstitution agentInstitution : agentCreateDto.getAgentInstitutions()) {
            gameTypeIds.add(agentInstitution.getGameTypeId());
            institutionIds.add(agentInstitution.getInstitutionId());
        }
        agent.setGameTypeIds(gameTypeIds);
        agent.setInstitutionIds(institutionIds);
        agent.setAgentInstitutions(agentCreateDto.getAgentInstitutions());
        return agent;
    }

    private AuthInfoCreateDto createAuthInfoDtoFromAgent(AgentCreateDto agentCreateDto) {
        AuthInfoCreateDto authInfoCreateDto = new AuthInfoCreateDto();
        authInfoCreateDto.setAuthRoleId(LSLBAuthRoleReferenceData.AGENT_ROLE_ID);
        authInfoCreateDto.setEmailAddress(agentCreateDto.getEmailAddress());
        authInfoCreateDto.setPhoneNumber(agentCreateDto.getPhoneNumber());
        authInfoCreateDto.setFirstName(agentCreateDto.getFirstName());
        authInfoCreateDto.setLastName(agentCreateDto.getLastName());
        return authInfoCreateDto;
    }
}
