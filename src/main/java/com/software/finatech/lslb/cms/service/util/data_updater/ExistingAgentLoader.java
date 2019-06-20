package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AgentInstitution;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.model.migrations.NewMigratedAgent;
import com.software.finatech.lslb.cms.service.model.migrations.NewMigratedAgentInstitution;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AgentStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.AgentUserCreator;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.adapters.DeviceMagicAgentAdapter;
import com.software.finatech.lslb.cms.service.util.adapters.model.DeviceMagicAgent;
import com.software.finatech.lslb.cms.service.util.adapters.model.DeviceMagicAgentInstitutionCategoryDetails;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ExistingAgentLoader {
    private static final Logger logger = LoggerFactory.getLogger(ExistingAgentLoader.class);
    @Autowired
    private DeviceMagicAgentAdapter deviceMagicAgentAdapter;
    @Autowired
    private AgentUserCreator agentUserCreator;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    public void loadAgentsFromCSV(MultipartFile multipartFile) throws LicenseServiceException {
        if (multipartFile.isEmpty()) {
            throw new LicenseServiceException("File is empty");
        }
        Map<String, DeviceMagicAgent> deviceMagicAgentMap = new HashMap<>();
        try {
            String completeData = new String(multipartFile.getBytes());
            String[] rows = completeData.split("\\r?\\n");
            for (int i = 1; i < rows.length; i++) {
                String[] columns = rows[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                //   String[] columns = rows[i].split(",");
                ///length of columns
                if (columns.length < 36) {
                    throw new LicenseServiceException("File is less than 36 columns");
                } else {
                    String bvn = columns[31];
                    String email = columns[30];
                    String submissionId = columns[4];
                    String dateOfBirth = columns[12];
                    if (StringUtils.isEmpty(dateOfBirth)) {
                        logger.info("Agent with submission id {} has no date of birth", submissionId);
                    }

                    DeviceMagicAgent deviceMagicAgent = deviceMagicAgentMap.get(bvn);
                    if (deviceMagicAgent == null) {
                        deviceMagicAgent = new DeviceMagicAgent();
                        deviceMagicAgent.setBvn(bvn);
                        deviceMagicAgent.setSubmissionId(submissionId);
                        deviceMagicAgent.setGender(columns[8]);
                        deviceMagicAgent.setTitle(columns[9]);
                        deviceMagicAgent.setFirstName(columns[10]);
                        deviceMagicAgent.setLastName(columns[11]);
                        deviceMagicAgent.setDateOfBirth(columns[12]);
                        deviceMagicAgent.setPhoneNumber1(columns[13]);
                        deviceMagicAgent.setPhoneNumber2(columns[14]);
                        deviceMagicAgent.setResidentialAddressStreet(columns[15]);
                        deviceMagicAgent.setResidentialAddressCity(columns[16]);
                        deviceMagicAgent.setResidentialAddressState(columns[17]);
                        deviceMagicAgent.setEmail(email);
                        deviceMagicAgent.setBvn(bvn);
                        deviceMagicAgent.setMeansOfId(columns[32]);
                        deviceMagicAgent.setIdNumber(columns[33]);
                    }

                    DeviceMagicAgentInstitutionCategoryDetails institutionCategoryDetails = new DeviceMagicAgentInstitutionCategoryDetails();
                    institutionCategoryDetails.setOperatorId(columns[6]);
                    institutionCategoryDetails.setGamingCategory(columns[7]);
                    institutionCategoryDetails.setBusinessAddressStreet1(columns[18]);
                    institutionCategoryDetails.setBusinessAddressCity1(columns[19]);
                    institutionCategoryDetails.setBusinessAddressState1(columns[20]);
                    institutionCategoryDetails.setBusinessAddressStreet2(columns[21]);
                    institutionCategoryDetails.setBusinessAddressCity2(columns[22]);
                    institutionCategoryDetails.setBusinessAddressState2(columns[23]);
                    institutionCategoryDetails.setBusinessAddressStreet3(columns[24]);
                    institutionCategoryDetails.setBusinessAddressCity3(columns[25]);
                    institutionCategoryDetails.setBusinessAddressState3(columns[26]);
                    institutionCategoryDetails.setBusinessAddressStreet4(columns[27]);
                    institutionCategoryDetails.setBusinessAddressCity4(columns[28]);
                    institutionCategoryDetails.setBusinessAddressState4(columns[29]);
                    deviceMagicAgent.getInstitutionCategoryDetailsList().add(institutionCategoryDetails);
                    if (deviceMagicAgent.getInstitutionCategoryDetailsList().size() > 1) {
                        logger.info("AGENT WITH BVN {} has more than one operator", bvn);
                    }
                    deviceMagicAgentMap.put(bvn, deviceMagicAgent);
                }
            }

            for (DeviceMagicAgent deviceMagicAgent : deviceMagicAgentMap.values()) {
                deviceMagicAgentAdapter.saveDeviceMagicAgentToAgentDb(deviceMagicAgent);
            }
        } catch (Exception e) {
            logger.error("An error occurred while parsing file", e);
        }
    }


    public void createUserAndCustomerCodeForLiveAgents() {
        Query query = new Query();
        query.addCriteria(Criteria.where("fromLiveData").is(true));
        ArrayList<Agent> liveAgents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
        for (Agent liveAgent : liveAgents) {
            try {
                if (!liveAgent.hasUser()) {
                    agentUserCreator.createUserAndCustomerCodeForAgent(liveAgent, httpServletRequest);
                    logger.info("Created user for {}", liveAgent.getFullName());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    public void printAgents(MultipartFile multipartFile) throws LicenseServiceException {
        if (multipartFile.isEmpty()) {
            throw new LicenseServiceException("File is empty");
        }
        Map<String, DeviceMagicAgent> deviceMagicAgentMap = new HashMap<>();
        try {
            String completeData = new String(multipartFile.getBytes());
            String[] rows = completeData.split("\\r?\\n");
            for (int i = 1; i < rows.length; i++) {
                String[] columns = rows[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                //   String[] columns = rows[i].split(",");
                ///length of columns
                if (columns.length < 36) {
                    throw new LicenseServiceException("File is less than 36 columns");
                } else {
                    String operatorId = columns[6];
                    if (StringUtils.equalsIgnoreCase("BETKING", operatorId)) {
                        String bvn = columns[31];
                        String email = columns[30];
                        if (StringUtils.isEmpty(email)) {
                            logger.info("{} {} with submission id {}  with operator id {} has no email",
                                    columns[10], columns[11], columns[4], columns[6]);
                        }
                        if (StringUtils.isEmpty(bvn)) {
                            logger.info("{} {} with submission id {} has no bvn", columns[10], columns[11], columns[4]);
                        }
                        DeviceMagicAgent deviceMagicAgent = deviceMagicAgentMap.get(bvn);
                        if (deviceMagicAgent == null) {
                            deviceMagicAgent = new DeviceMagicAgent();
                            deviceMagicAgent.setBvn(bvn);
                        } else {
                            logger.info("Agent with bvn {} has more than one record for betking", bvn);
                        }
                        deviceMagicAgentMap.put(bvn, deviceMagicAgent);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred while parsing file", e);
        }
    }

    public Mono<ResponseEntity> loadAgentFromModel(NewMigratedAgent newMigratedAgent) {
        try {
            String emailAddress = newMigratedAgent.getEmailAddress();
            Query query = new Query();
            query.addCriteria(Criteria.where("emailAddress").is(emailAddress));
            Agent agent = (Agent) mongoRepositoryReactive.find(query, Agent.class).block();
            if (agent != null) {
                return ErrorResponseUtil.BadRequestResponse("An agent exists with the same email address");
            }
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.find(query, AuthInfo.class).block();
            if (authInfo != null) {
                return ErrorResponseUtil.BadRequestResponse("A user already exist with the same email address");
            }
            agent = new Agent();
            agent.setId(UUID.randomUUID().toString());
            agent.setBvn(newMigratedAgent.getBvn());
            agent.setAgentStatusId(AgentStatusReferenceData.ACTIVE_ID);
            agent.setPhoneNumber(newMigratedAgent.getPhoneNumber());
            agent.setPhoneNumbers(newMigratedAgent.getPhoneNumbers());
            agent.setAgentId(NumberUtil.generateAgentId());
            agent.setDateOfBirth(newMigratedAgent.getDateOfBirth());
            agent.setDob(new LocalDate(newMigratedAgent.getDateOfBirth()));
            agent.setEnabled(true);
            agent.setEmailAddress(newMigratedAgent.getEmailAddress());
            agent.setTitle(newMigratedAgent.getTitle());
            agent.setFromLiveData(true);
            agent.setIdNumber(newMigratedAgent.getIdNumber());
            agent.setMeansOfId(newMigratedAgent.getMeansOfIdentification());
            agent.setGenderId(newMigratedAgent.getGenderId());
            for (NewMigratedAgentInstitution newMigratedAgentInstitution : newMigratedAgent.getNewMigratedAgentInstitutions()) {
                AgentInstitution agentInstitution = new AgentInstitution();
                agentInstitution.setInstitutionId(newMigratedAgentInstitution.getInstitutionId());
                agentInstitution.setBusinessAddressList(newMigratedAgentInstitution.getBusinessAddress());
                agentInstitution.setGameTypeIds(newMigratedAgentInstitution.getGameTypeIds());
                agent.getGameTypeIds().addAll(newMigratedAgentInstitution.getGameTypeIds());
                agent.getBusinessAddresses().addAll(newMigratedAgentInstitution.getBusinessAddress());
                agent.getAgentInstitutions().add(agentInstitution);
            }
            mongoRepositoryReactive.saveOrUpdate(agent);
            return Mono.just(new ResponseEntity<>("Agent Created", HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return ErrorResponseUtil.BadRequestResponse("Invalid date format, use yyyy-MM-dd");
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while migrating new agent", e);
        }
    }

    public void clearAllVigipayCustomerCodes() {
        Query query = new Query();
        query.addCriteria(Criteria.where("vgPayCustomerCode").ne(null));
        ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
        for (Agent agent : agents) {
            agent.setVgPayCustomerCode(null);
            mongoRepositoryReactive.saveOrUpdate(agent);
        }
    }
}