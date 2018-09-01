package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.controller.AuthInfoController;
import com.software.finatech.lslb.cms.service.controller.BaseController;
import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AgentInstitution;
import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.*;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
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

@Service
public class AgentServiceImpl implements AgentService {
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private LicenseService licenseService;
    private InstitutionService institutionService;
    private GameTypeService gameTypeService;
    private AuthInfoService authInfoService;
    private AuthInfoController authInfoController;

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger logger = LoggerFactory.getLogger(AgentServiceImpl.class);

    @Autowired
    public AgentServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                            LicenseService licenseService,
                            InstitutionService institutionService,
                            GameTypeService gameTypeService,
                            AuthInfoService authInfoService,
                            AuthInfoController authInfoController) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.licenseService = licenseService;
        this.institutionService = institutionService;
        this.gameTypeService = gameTypeService;
        this.authInfoService = authInfoService;
        this.authInfoController = authInfoController;
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
            return ErrorResponseUtil.logAndReturnError(logger, errorMsg, e);
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
            return Mono.just(new ResponseEntity<>("Invalid Date format for meeting date , please use yyyy-MM-dd HH:mm:ss", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "an error occurred when creating agent", e);
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

        for (AgentInstitution agentInstitution : agentCreateDto.getAgentInstitutions()) {
            String institutionId = agentInstitution.getInstitutionId();
            String gameTypeId = agentInstitution.getGameTypeId();

            Object licenseDtoEntity = licenseService.findLicenseByInstitutionId(institutionId, gameTypeId).block().getBody();
            if (licenseDtoEntity instanceof List) {
                List<LicenseDto> licenseDtosList = (List<LicenseDto>) licenseDtoEntity;
                GameType gameType = gameTypeService.findById(gameTypeId);
                String gameTypeName = gameTypeId;
                if (gameType != null) {
                    gameTypeName = gameType.getDescription();
                }

                //check if he has existing license record
                if (licenseDtosList.isEmpty()) {
                    Institution institution = institutionService.findById(institutionId);
                    String institutionName = institutionId;
                    if (institution != null) {
                        institutionName = institution.getInstitutionName();
                    }

                    return Mono.just(new ResponseEntity<>(String.format("Institution %s does not have an existing license record for gameType %s", institutionName, gameTypeName), HttpStatus.BAD_REQUEST));
                }

                //check if he has  more than one license record (which is never meant to happen)
                if (licenseDtosList.size() > 1) {
                    Institution institution = institutionService.findById(institutionId);
                    String institutionName = institutionId;
                    if (institution != null) {
                        institutionName = institution.getInstitutionName();
                    }
                    return Mono.just(new ResponseEntity<>(String.format("Institution %s has more than one license record for gameType %s", institutionName, gameTypeName), HttpStatus.BAD_REQUEST));
                }

                //get the first and only record of the license and check if he has a proper LICENSED status for it
                LicenseDto licenseDto = licenseDtosList.get(0);
                LicenseStatusDto licenseStatusDto = licenseDto.getLicenseStatus();
                if (licenseStatusDto != null) {
                    String licensedStatusId = LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID;
                    if (!StringUtils.equals(licensedStatusId, licenseStatusDto.getId())) {
                        Institution institution = institutionService.findById(institutionId);
                        String institutionName = institutionId;
                        if (institution != null) {
                            institutionName = institution.getInstitutionName();
                        }
                        return Mono.just(new
                                ResponseEntity<>(String.format("Institution %s has license status %s for gameType %s , and it is meant to be LICENSED",
                                institutionName, gameTypeName, licenseStatusDto.getName()), HttpStatus.BAD_REQUEST));
                    }
                }
            } else {
                return Mono.just(new ResponseEntity<>("Bad license record found for institution game type", HttpStatus.BAD_REQUEST));
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
        for (AgentInstitution agentInstitution:agentCreateDto.getAgentInstitutions()) {
            gameTypeIds.add(agentInstitution.getGameTypeId());
            institutionIds.add(agentInstitution.getInstitutionId());
        }
        agent.setGameTypeIds(gameTypeIds);
        agent.setInstitutionIds(institutionIds);
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
