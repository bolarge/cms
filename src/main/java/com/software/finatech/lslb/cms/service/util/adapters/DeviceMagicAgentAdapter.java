package com.software.finatech.lslb.cms.service.util.adapters;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.util.EnvironmentUtils;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.adapters.model.DeviceMagicAgent;
import com.software.finatech.lslb.cms.service.util.adapters.model.DeviceMagicAgentInstitutionCategoryDetails;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.software.finatech.lslb.cms.service.util.NumberUtil.generateAgentId;

@Component
public class DeviceMagicAgentAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DeviceMagicAgentAdapter.class);
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    private EnvironmentUtils environmentUtils;


    public void saveDeviceMagicAgentToAgentDb(DeviceMagicAgent deviceMagicAgent) {
        if (StringUtils.isEmpty(deviceMagicAgent.getEmail())) {
            logger.info("Submission with id {} does not have email, skipping creation", deviceMagicAgent.getSubmissionId());
            return;
        }
        Agent agent = findAgentBySubmissionId(deviceMagicAgent.getSubmissionId());
        boolean isNewAgent = false;
        if (agent == null) {
            isNewAgent = true;
            agent = new Agent();
            agent.setId(UUID.randomUUID().toString());
        }
        agent.setSubmissionId(deviceMagicAgent.getSubmissionId());
        agent.setFullName(String.format("%s %s", deviceMagicAgent.getFirstName(), deviceMagicAgent.getLastName()));
        agent.setFirstName(deviceMagicAgent.getFirstName());
        agent.setLastName(deviceMagicAgent.getLastName());
        agent.setEmailAddress(deviceMagicAgent.getEmail());
        agent.setPhoneNumber(deviceMagicAgent.getPhoneNumber1());
        if (!StringUtils.isEmpty(deviceMagicAgent.getPhoneNumber1())) {
            agent.getPhoneNumbers().add(deviceMagicAgent.getPhoneNumber1());
        }
        if (!StringUtils.isEmpty(deviceMagicAgent.getPhoneNumber2())) {
            agent.getPhoneNumbers().add(deviceMagicAgent.getPhoneNumber2());
        }
        agent.setMeansOfId(deviceMagicAgent.getMeansOfId());
        agent.setTitle(deviceMagicAgent.getTitle());
        agent.setBvn(deviceMagicAgent.getBvn());
        agent.setAgentId(generateAgentId());
        agent.setAgentStatusId(AgentStatusReferenceData.ACTIVE_ID);
        agent.setEnabled(true);
        agent.setIdNumber(deviceMagicAgent.getIdNumber());
        String residentialAddress = buildAddress(deviceMagicAgent.getResidentialAddressStreet(),
                deviceMagicAgent.getResidentialAddressCity(), deviceMagicAgent.getResidentialAddressState());
        if (!StringUtils.isEmpty(residentialAddress)) {
            residentialAddress = residentialAddress.replace("\"", "");
            agent.setResidentialAddress(residentialAddress);
        }
        for (DeviceMagicAgentInstitutionCategoryDetails institutionCategoryDetails : deviceMagicAgent.getInstitutionCategoryDetailsList()) {
            AgentInstitution agentInstitution = new AgentInstitution();
            String address = buildAddress(institutionCategoryDetails.getBusinessAddressStreet1(),
                    institutionCategoryDetails.getBusinessAddressCity1(), institutionCategoryDetails.getBusinessAddressState1());
            if (!StringUtils.isEmpty(address)) {
                address = address.replace("\"", "");
                agentInstitution.getBusinessAddressList().add(address);
                agent.getBusinessAddresses().add(address);
            }
            address = buildAddress(institutionCategoryDetails.getBusinessAddressStreet2(),
                    institutionCategoryDetails.getBusinessAddressCity2(), institutionCategoryDetails.getBusinessAddressState2());
            if (!StringUtils.isEmpty(address)) {
                address = address.replace("\"", "");
                agentInstitution.getBusinessAddressList().add(address);
                agent.getBusinessAddresses().add(address);
            }
            address = buildAddress(institutionCategoryDetails.getBusinessAddressStreet3(),
                    institutionCategoryDetails.getBusinessAddressCity3(), institutionCategoryDetails.getBusinessAddressState3());
            if (!StringUtils.isEmpty(address)) {
                address = address.replace("\"", "");
                agentInstitution.getBusinessAddressList().add(address);
                agent.getBusinessAddresses().add(address);
            }
            address = buildAddress(institutionCategoryDetails.getBusinessAddressStreet4(),
                    institutionCategoryDetails.getBusinessAddressCity4(), institutionCategoryDetails.getBusinessAddressState4());
            boolean addressIsEmpty = StringUtils.isEmpty(address);
            boolean addressIsNotEmpty = !addressIsEmpty;
            if (addressIsNotEmpty) {
                address = address.replace("\"", "");
                agentInstitution.getBusinessAddressList().add(address);
                agent.getBusinessAddresses().add(address);
            }
            String institutionId = findInstitutionIdByOperatorId(institutionCategoryDetails.getOperatorId());
            if (StringUtils.isEmpty(institutionId)) {
                logger.info("No Institution found for operator id {} for submission id {}, Skipping agent creation ", institutionCategoryDetails.getOperatorId(), deviceMagicAgent.getSubmissionId());
                return;
            }
            agent.getInstitutionIds().add(institutionId);
            agentInstitution.setInstitutionId(institutionId);
            String[] gameTypeStrings = institutionCategoryDetails.getGamingCategory().split(",");
            for (String gameTypeString : gameTypeStrings) {
                GameType gameType = getGameTypeFromDeviceMagicName(gameTypeString);
                if (gameType != null) {
                    agent.getGameTypeIds().add(gameType.getId());
                    agentInstitution.getGameTypeIds().add(gameType.getId());
                } else {
                    logger.info("GameType with name /String {} not found", gameTypeString);
                }
            }
            agent.getAgentInstitutions().add(agentInstitution);
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/YYYY");
        String dob = deviceMagicAgent.getDateOfBirth();
        try {

            if (!StringUtils.isEmpty(dob)) {
                agent.setDob(dateTimeFormatter.parseLocalDate(dob));
            }
        } catch (Exception e) {
            try {
                dateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd");
                LocalDate dateOfBirth = dateTimeFormatter.parseLocalDate(deviceMagicAgent.getDateOfBirth());
                agent.setDob(dateOfBirth);
            } catch (Exception ex) {
                logger.error("An error occurred while parsing agent date ", ex);
            }
        }
        Gender gender = getGenderFromDeviceMagicName(deviceMagicAgent.getGender());
        if (gender != null) {
            agent.setGenderId(gender.getId());
        }
        agent.setTitle(deviceMagicAgent.getTitle());
        agent.setSkipVigipay(true);
        agent.setFromLiveData(true);
        mongoRepositoryReactive.saveOrUpdate(agent);
        if (isNewAgent) {
            saveDocumentForAgent(deviceMagicAgent, agent);
            logger.info("Saved Agent for submission {} , Agent Id {}", deviceMagicAgent.getSubmissionId(), agent.getId());
        } else {
            logger.info("Updated Agent for submission {} , Agent Id {}", deviceMagicAgent.getSubmissionId(), agent.getId());
        }
    }

    private void saveDocumentForAgent(DeviceMagicAgent deviceMagicAgent, Agent agent) {
        String submissionId = deviceMagicAgent.getSubmissionId();
        try {
            //String filePath = String.format("classpath:Binary/%s/Picture.JPEG", submissionId);
            //File file = ResourceUtils.getFile(filePath);
            Document document = new Document();
            document.setId(UUID.randomUUID().toString().replace("-", ""));
            document.setEntityId(agent.getId());
            document.setCurrent(true);
            document.setDescription("Agent Passport");
            document.setDocumentTypeId(DocumentTypeReferenceData.AGENT_PASSPORT_ID);
            document.setEntity(agent.getId());
            document.setEntryDate(LocalDateTime.now());
            document.setFilename("Picture.JPEG");
            document.setOriginalFilename("Picture.JPEG");
            document.setArchive(false);
            document.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
            document.setAgentId(agent.getId());
            try {
                document.setAwsObjectKey(getAgentPassportKey(deviceMagicAgent));
                /**
                 *    Old method that saves in DB
                 */
//                DocumentBinary documentBinary = new DocumentBinary();
//                documentBinary.setFile(new Binary(BsonBinarySubType.BINARY, Files.toByteArray(file)));
//                documentBinary.setDocumentId(document.getId());
//                mongoRepositoryReactive.saveOrUpdate(documentBinary);
            } catch (IOException e) {
                logger.error("An error occurred while setting bytes of document");
            }
            mongoRepositoryReactive.saveOrUpdate(document);
            logger.info("Saved passport for agent with submission {}", submissionId);
        } catch (Exception e) {
            logger.error(String.format("An error occurred while saving agent passport for agent %s", submissionId), e);
        }
    }

    private String buildAddress(String streetAddress, String city, String state) {
        if (!StringUtils.isEmpty(streetAddress) && !StringUtils.isEmpty(city)
                && !StringUtils.isEmpty(state)) {
            return String.format("%s, %s, %s", streetAddress.replace(".", ""), city, state);
        }
        return null;
    }

    public String findInstitutionIdByOperatorId(String operatorId) {
        if (StringUtils.isEmpty(operatorId)) {
            return null;
        }
        if (StringUtils.equalsIgnoreCase("1960 BET", operatorId)) {
            operatorId = "1960bet";
        }
        if (StringUtils.equalsIgnoreCase("FORTUNE BET", operatorId)) {
            operatorId = "Fortunebet";
        }
        if (StringUtils.equalsIgnoreCase("WINNERS GOLDEN BET", operatorId)) {
            operatorId = "WGB";
        }
        if (StringUtils.equalsIgnoreCase("WINNERS GOLDEN CHANCE", operatorId)) {
            operatorId = "Golden Chance";
        }
        if (StringUtils.equalsIgnoreCase("BONANZA WIN", operatorId)) {
            operatorId = "BonanzaWin";
        }
        if (StringUtils.equalsIgnoreCase("SPORTY BET", operatorId)) {
            operatorId = "Sportybet";
        }
        if (StringUtils.equalsIgnoreCase("POWER BET", operatorId)) {
            operatorId = "Supabet";
        }
        if (StringUtils.equalsIgnoreCase("GIVE n TAKE", operatorId)) {
            operatorId = "Give 'n' Take";
        }
        if (StringUtils.equalsIgnoreCase("Access Bet", operatorId)) {
            operatorId = "Access Bet";
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("tradeName").regex(operatorId, "i"));
        InstitutionCategoryDetails institutionCategoryDetails = (InstitutionCategoryDetails) mongoRepositoryReactive.find(query, InstitutionCategoryDetails.class).block();
        if (institutionCategoryDetails != null) {
            return institutionCategoryDetails.getInstitutionId();
        }

        return null;
    }

    private GameType getGameTypeFromDeviceMagicName(String gameTypeString) {
        gameTypeString = gameTypeString.replace("\"", "");
        Map<String, FactObject> gameTypeMap = Mapstore.STORE.get("GameType");
        if (gameTypeMap != null) {
            if (StringUtils.equals("Sports Bet", gameTypeString)) {
                return (GameType) gameTypeMap.get(GameTypeReferenceData.OSB_GAME_TYPE_ID);
            }
            if (StringUtils.equals("Gaming Machine", gameTypeString)) {
                return (GameType) gameTypeMap.get(GameTypeReferenceData.GAMING_MACHINE_ID);
            }
            if (StringUtils.equals("POL", gameTypeString)) {
                return (GameType) gameTypeMap.get(GameTypeReferenceData.POL_GAME_TYPE_ID);
            }
        }
        return null;
    }


    private Gender getGenderFromDeviceMagicName(String genderString) {
        Map<String, FactObject> genderMap = Mapstore.STORE.get("Gender");
        if (StringUtils.equals("Male", genderString)) {
            return (Gender) genderMap.get(GenderReferenceData.MALE_ID);
        }
        if (StringUtils.equals("Female", genderString)) {
            return (Gender) genderMap.get(GenderReferenceData.FEMALE_ID);
        }
        return null;
    }

    private Agent findAgentBySubmissionId(String submissionId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("submissionId").is(submissionId));
        return (Agent) mongoRepositoryReactive.find(query, Agent.class).block();
    }

    private String getAgentPassportKey(DeviceMagicAgent deviceMagicAgent) throws IOException {
        if (environmentUtils.isTestEnvironment()) {
            return String.format("lslb-cms/test/Agent_Passports/%s/Picture.JPEG", deviceMagicAgent.getSubmissionId());
        }
        if (environmentUtils.isStagingEnvironment()) {
            return String.format("lslb-cms/staging/Agent_Passports/%s/Picture.JPEG", deviceMagicAgent.getSubmissionId());
        }
        if (environmentUtils.isProductionEnvironment()) {
            return String.format("lslb-cms/production/Agent_Passports/%s/Picture.JPEG", deviceMagicAgent.getSubmissionId());
        }
        if (environmentUtils.isDevelopmentEnvironment()) {
            return String.format("lslb-cms/development/Agent_Passports/%s/Picture.JPEG", deviceMagicAgent.getSubmissionId());
        }
        throw new IOException("Invalid Environment");
    }
}
