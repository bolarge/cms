package com.software.finatech.lslb.cms.service.util.adapters;

import com.google.common.io.Files;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.DocumentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.GameTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.GenderReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.software.finatech.lslb.cms.service.util.NumberUtil.generateAgentId;

@Component
public class DeviceMagicAgentAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DeviceMagicAgentAdapter.class);
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    public void saveDeviceMagicAgentToAgentDb(DeviceMagicAgent deviceMagicAgent) {
        Agent agent = new Agent();
        agent.setId(UUID.randomUUID().toString());
        agent.setFullName(String.format("%s %s", deviceMagicAgent.getFirstName(), deviceMagicAgent.getLastName()));
        agent.setFirstName(deviceMagicAgent.getFirstName());
        agent.setLastName(deviceMagicAgent.getLastName());
        agent.setEmailAddress(deviceMagicAgent.getEmail());
        agent.setPhoneNumber(deviceMagicAgent.getPhoneNumber1());
        agent.getPhoneNumbers().add(deviceMagicAgent.getPhoneNumber1());
        agent.getPhoneNumbers().add(deviceMagicAgent.getPhoneNumber2());
        agent.setMeansOfId(deviceMagicAgent.getMeansOfId());
        agent.setTitle(deviceMagicAgent.getTitle());
        agent.setBvn(deviceMagicAgent.getBvn());
        agent.setAgentId(generateAgentId());
        agent.setIdNumber(deviceMagicAgent.getIdNumber());
        String address = buildAddress(deviceMagicAgent.getResidentialAddressStreet(),
                deviceMagicAgent.getResindetialAddressCity(), deviceMagicAgent.getResidentialAddressState());
        if (!StringUtils.isEmpty(address)) {
            agent.setResidentialAddress(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessAddressStreet1(),
                deviceMagicAgent.getBusinessAddressCity1(), deviceMagicAgent.getBusinessAddressState1());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessAddressStreet2(),
                deviceMagicAgent.getBusinessAddressCity2(), deviceMagicAgent.getBusinessAddressState2());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessAddressStreet3(),
                deviceMagicAgent.getBusinessAddressCity3(), deviceMagicAgent.getBusinessAddressState3());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessAddressStreet4(),
                deviceMagicAgent.getBusinessAddressCity4(), deviceMagicAgent.getBusinessAddressState4());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        String institutionId = findInstitutionIdByOperatorId(deviceMagicAgent.getOperatorId());
        if (StringUtils.isEmpty(institutionId)) {
            logger.info("No Institution found for operator id {} for submission id {}, Skipping agent creation ", deviceMagicAgent.getOperatorId(), deviceMagicAgent.getSubmissionId());
            return;
        }
        agent.getInstitutionIds().add(institutionId);
        String[] gameTypeStrings = deviceMagicAgent.getGamingCategopry().split(",");
        AgentInstitution agentInstitution = new AgentInstitution();
        for (String gameTypeString : gameTypeStrings) {
            GameType gameType = getGameTypeFromDeviceMagicName(gameTypeString);
            if (gameType != null) {
                agent.getGameTypeIds().add(gameType.getId());
                agentInstitution.getGameTypeIds().add(gameType.getId());
                agentInstitution.setBusinessAddressList(agent.getBusinessAddresses());
                agentInstitution.setInstitutionId(institutionId);
            }
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        String dob = deviceMagicAgent.getDateOfBirth();
        if (!StringUtils.isEmpty(dob)) {
            agent.setDateOfBirth(dateTimeFormatter.parseLocalDate(dob));
        }
        Gender gender = getGenderFromDeviceMagicName(deviceMagicAgent.getGender());
        if (gender != null) {
            agent.setGenderId(gender.getId());
        }
        agent.setTitle(deviceMagicAgent.getTitle());
        mongoRepositoryReactive.saveOrUpdate(agent);
        saveDocumentForAgent(deviceMagicAgent, agent);
        logger.info("Saved Agent for submission {} , Agent Id {}", deviceMagicAgent.getSubmissionId(), agent.getId());
    }


    private void saveDocumentForAgent(DeviceMagicAgent deviceMagicAgent, Agent agent) {
        String submissionId = deviceMagicAgent.getSubmissionId();
        try {
            String filePath = String.format("classpath:agent-data/pictures/%s/Picture.JPEG", submissionId);
            File file = ResourceUtils.getFile(filePath);
            Document document = new Document();
            document.setId(UUID.randomUUID().toString().replace("-", ""));
            document.setEntityId(agent.getId());
            document.setCurrent(true);
            document.setDescription("Agent Passport");
            document.setDocumentTypeId(DocumentTypeReferenceData.AGENT_PASSPORT_ID);
            document.setEntity(agent.getId());
            document.setEntryDate(LocalDateTime.now());
            document.setFilename(file.getName());
            document.setOriginalFilename("Picture.JPEG");
            document.setArchive(false);
            document.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
            document.setAgentId(agent.getId());
            try {
                document.setFile(new Binary(BsonBinarySubType.BINARY, Files.toByteArray(file)));
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

    private String findInstitutionIdByOperatorId(String operatorId) {
        if (StringUtils.isEmpty(operatorId)) {
            return null;
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

    public DeviceMagicAgent fromJsonObject(JSONObject jsonObject) {
        String VALUE = "value";
        String STREET_ADDRESS = "Street_Address";
        String STATE = "State";
        String CITY = "City";
        DeviceMagicAgent agent = new DeviceMagicAgent();
        try {
            JSONObject answerJsonObject = (JSONObject) jsonObject.get("answers");
            try {
                JSONObject operatorIdJsonObject = (JSONObject) answerJsonObject.get("Operator_ID");
                if (operatorIdJsonObject != null) {
                    String operatorId = (String) operatorIdJsonObject.get(VALUE);
                    agent.setOperatorId(operatorId);
                }
            } catch (Exception e) {
            }
            try {
                JSONObject gamingCategoryJsonObject = (JSONObject) answerJsonObject.get("Gaming_Category");
                if (gamingCategoryJsonObject != null) {
                    agent.setGamingCategopry((String) gamingCategoryJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }
            try {
                JSONObject firstNameJsonObject = (JSONObject) answerJsonObject.get("First_Name");
                if (firstNameJsonObject != null) {
                    agent.setFirstName((String) firstNameJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }
            try {
                JSONObject lastNameJsonObject = (JSONObject) answerJsonObject.get("Last_Name");
                if (lastNameJsonObject != null) {
                    agent.setLastName((String) lastNameJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }
            try {
                JSONObject titleJsonObject = (JSONObject) answerJsonObject.get("Title");
                if (titleJsonObject != null) {
                    agent.setTitle((String) titleJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject dateOfBirthJsonObject = (JSONObject) answerJsonObject.get("Date_Of_Birth");
                if (dateOfBirthJsonObject != null) {
                    agent.setDateOfBirth((String) dateOfBirthJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject phoneNumber1JsonObject = (JSONObject) answerJsonObject.get("Phone_No__1");
                if (phoneNumber1JsonObject != null) {
                    agent.setPhoneNumber1((String) phoneNumber1JsonObject.get(VALUE));
                }
            } catch (Exception e) {

            }

            try {
                JSONObject phoneNumber2JsonObject = (JSONObject) answerJsonObject.get("Phone_No__2");
                if (phoneNumber2JsonObject != null) {
                    agent.setPhoneNumber2((String) phoneNumber2JsonObject.get(VALUE));
                }
            } catch (Exception e) {

            }

            try {
                JSONObject residentialAddressJsonObject = (JSONObject) answerJsonObject.get("Residential_Address");
                if (residentialAddressJsonObject != null) {
                    JSONArray addressArray = (JSONArray) residentialAddressJsonObject.get("values");
                    JSONObject addressObject = (JSONObject) addressArray.get(0);
                    JSONObject streetAddressObject = (JSONObject) addressObject.get(STREET_ADDRESS);
                    if (streetAddressObject != null) {
                        agent.setResidentialAddressStreet((String) streetAddressObject.get(VALUE));
                    }

                    JSONObject stateObject = (JSONObject) addressObject.get(STATE);
                    if (stateObject != null) {
                        agent.setResidentialAddressState((String) stateObject.get(VALUE));
                    }

                    JSONObject cityObject = (JSONObject) addressObject.get(CITY);
                    if (cityObject != null) {
                        agent.setResindetialAddressCity((String) cityObject.get(VALUE));
                    }
                }
            } catch (Exception e) {
                logger.error("Error reading business address", e);
            }

            try {
                JSONObject businessAddress1JsonObject = (JSONObject) answerJsonObject.get("Business_Address_1");
                if (businessAddress1JsonObject != null) {
                    JSONArray businessAddressArray = (JSONArray) businessAddress1JsonObject.get("values");
                    JSONObject addressObject = (JSONObject) businessAddressArray.get(0);
                    JSONObject streetAddressObject = (JSONObject) addressObject.get(STREET_ADDRESS);
                    if (streetAddressObject != null) {
                        agent.setBusinessAddressStreet1((String) streetAddressObject.get(VALUE));
                    }

                    JSONObject stateObject = (JSONObject) addressObject.get(STATE);
                    if (stateObject != null) {
                        agent.setBusinessAddressState1((String) stateObject.get(VALUE));
                    }

                    JSONObject cityObject = (JSONObject) addressObject.get(CITY);
                    if (cityObject != null) {
                        agent.setBusinessAddressCity1((String) cityObject.get(VALUE));
                    }
                }
            } catch (Exception e) {
            }

            try {
                JSONObject businessAddress2JsonObject = (JSONObject) answerJsonObject.get("Business_Address_2");
                if (businessAddress2JsonObject != null) {
                    JSONArray businessAddressArray = (JSONArray) businessAddress2JsonObject.get("value");
                    JSONObject addressObject = (JSONObject) businessAddressArray.get(0);
                    JSONObject streetAddressObject = (JSONObject) addressObject.get(STREET_ADDRESS);
                    if (streetAddressObject != null) {
                        agent.setBusinessAddressStreet2((String) streetAddressObject.get(VALUE));
                    }

                    JSONObject stateObject = (JSONObject) addressObject.get(STATE);
                    if (stateObject != null) {
                        agent.setBusinessAddressState2((String) stateObject.get(VALUE));
                    }

                    JSONObject cityObject = (JSONObject) addressObject.get(CITY);
                    if (cityObject != null) {
                        agent.setBusinessAddressCity2((String) cityObject.get(VALUE));
                    }
                }
            } catch (Exception e) {
            }

            try {
                JSONObject businessAddress3JsonObject = (JSONObject) answerJsonObject.get("Business_Address_3");
                if (businessAddress3JsonObject != null) {
                    JSONArray businessAddressArray = (JSONArray) businessAddress3JsonObject.get("values");
                    JSONObject addressObject = (JSONObject) businessAddressArray.get(0);
                    JSONObject streetAddressObject = (JSONObject) addressObject.get(STREET_ADDRESS);
                    if (streetAddressObject != null) {
                        agent.setBusinessAddressStreet3((String) streetAddressObject.get(VALUE));
                    }

                    JSONObject stateObject = (JSONObject) addressObject.get(STATE);
                    if (stateObject != null) {
                        agent.setBusinessAddressState3((String) stateObject.get(VALUE));
                    }

                    JSONObject cityObject = (JSONObject) addressObject.get(CITY);
                    if (cityObject != null) {
                        agent.setBusinessAddressCity3((String) cityObject.get(VALUE));
                    }
                }
            } catch (Exception e) {
            }

            try {
                JSONObject businessAddress4JsonObject = (JSONObject) answerJsonObject.get("Business_Address_4");
                if (businessAddress4JsonObject != null) {
                    JSONArray businessAddressArray = (JSONArray) businessAddress4JsonObject.get("values");
                    JSONObject addressObject = (JSONObject) businessAddressArray.get(0);
                    JSONObject streetAddressObject = (JSONObject) addressObject.get(STREET_ADDRESS);
                    if (streetAddressObject != null) {
                        agent.setBusinessAddressStreet4((String) streetAddressObject.get(VALUE));
                    }

                    JSONObject stateObject = (JSONObject) addressObject.get(STATE);
                    if (stateObject != null) {
                        agent.setBusinessAddressState4((String) stateObject.get(VALUE));
                    }

                    JSONObject cityObject = (JSONObject) addressObject.get(CITY);
                    if (cityObject != null) {
                        agent.setBusinessAddressCity4((String) cityObject.get(VALUE));
                    }
                }
            } catch (Exception e) {
            }

            try {
                JSONObject emailJsonObject = (JSONObject) answerJsonObject.get("Email");
                if (emailJsonObject != null) {
                    agent.setEmail((String) emailJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject genderJsonObject = (JSONObject) answerJsonObject.get("Gender");
                if (genderJsonObject != null) {
                    agent.setGender((String) genderJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject bvnJsonObject = (JSONObject) answerJsonObject.get("BVN");
                if (bvnJsonObject != null) {
                    Long bvn = (Long)bvnJsonObject.get(VALUE);
                    if (bvn != null){
                        agent.setBvn(String.valueOf(bvn));
                    }
                }
            } catch (Exception e) {
            }

            try {
                JSONObject meansOfIdJsonObject = (JSONObject) answerJsonObject.get("Means_of_ID");
                if (meansOfIdJsonObject != null) {
                    agent.setMeansOfId((String) meansOfIdJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject idNumberJsonObject = (JSONObject) answerJsonObject.get("ID_Number");
                if (idNumberJsonObject != null) {
                    agent.setIdNumber((String) idNumberJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }

        } catch (Exception e) {
            logger.error("Json Object \"answer\" does not exist ");
        }
        return agent;
    }
}
