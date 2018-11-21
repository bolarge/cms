package com.software.finatech.lslb.cms.service.util.adapters;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.GameTypeReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class DeviceMagicAgentAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DeviceMagicAgentAdapter.class);

    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    public Agent convertDeviceMagicAgentToAgent(DeviceMagicAgent deviceMagicAgent) {
        Agent agent = new Agent();
        agent.setId(UUID.randomUUID().toString());
        agent.setFullName(String.format("%s %s", deviceMagicAgent.getFirstname(), deviceMagicAgent.getLastname()));
        agent.setFirstName(deviceMagicAgent.getFirstname());
        agent.setLastName(deviceMagicAgent.getLastname());
        agent.setEmailAddress(deviceMagicAgent.getEmail());
        agent.setPhoneNumber(deviceMagicAgent.getPhonenumber1());
        agent.getPhoneNumbers().add(deviceMagicAgent.getPhonenumber1());
        agent.getPhoneNumbers().add(deviceMagicAgent.getPhonenumber2());
        agent.setMeansOfId(deviceMagicAgent.getMeansofid());
        agent.setTitle(deviceMagicAgent.getTitle());
        agent.setIdNumber(deviceMagicAgent.getIdnumber());
        String address = buildAddress(deviceMagicAgent.getResidentialaddressstreet(),
                deviceMagicAgent.getResindetialaddresscity(), deviceMagicAgent.getResidentialaddressstate());
        if (!StringUtils.isEmpty(address)) {
            agent.setResidentialAddress(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessaddressstreet1(),
                deviceMagicAgent.getBusinessaddresscity1(), deviceMagicAgent.getBusinessaddressstate1());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessaddressstreet2(),
                deviceMagicAgent.getBusinessaddresscity2(), deviceMagicAgent.getBusinessaddressstate2());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessaddressstreet3(),
                deviceMagicAgent.getBusinessaddresscity3(), deviceMagicAgent.getBusinessaddressstate3());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessaddressstreet4(),
                deviceMagicAgent.getBusinessaddresscity4(), deviceMagicAgent.getBusinessaddressstate4());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        Institution institution = findInstitutionByName(deviceMagicAgent.getOperatorid(), mongoRepositoryReactive);
        if (institution == null) {
            institution = new Institution();
            institution.setId("1234");
        }
        //  if (institution != null) {
        agent.getInstitutionIds().add(institution.getId());
        String[] gameTypeStrings = deviceMagicAgent.getGamingcategopry().split(",");
        for (String gameTypeString : gameTypeStrings) {
            GameType gameType = getGameTypeFromDeviceMagicName(gameTypeString);
            if (gameType != null) {
                agent.getGameTypeIds().add(gameType.getId());
                AgentInstitution agentInstitution = new AgentInstitution();
               // agentInstitution.setGameTypeId(gameType.getId());
                agentInstitution.setBusinessAddressList(agent.getBusinessAddresses());
                agentInstitution.setInstitutionId(institution.getId());
            }
        }
        //    }
        return agent;
    }

    private String buildAddress(String streetAddress, String city, String state) {
        if (!StringUtils.isEmpty(streetAddress) && !StringUtils.isEmpty(city)
                && !StringUtils.isEmpty(state)) {
            return String.format("%s, %s, %s", streetAddress.replace(".", ""), city, state);
        }
        return null;
    }

    private Institution findInstitutionByName(String institutionName, MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        if (StringUtils.isEmpty(institutionName)) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionName").regex(institutionName, "i"));
        return (Institution) mongoRepositoryReactive.find(query,
                Institution.class).block();
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
                    agent.setOperatorid(operatorId);
                }
            } catch (Exception e) {
            }
            try {
                JSONObject gamingCategoryJsonObject = (JSONObject) answerJsonObject.get("Gaming_Category");
                if (gamingCategoryJsonObject != null) {
                    agent.setGamingcategopry((String) gamingCategoryJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }
            try {
                JSONObject firstNameJsonObject = (JSONObject) answerJsonObject.get("First_Name");
                if (firstNameJsonObject != null) {
                    agent.setFirstname((String) firstNameJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }
            try {
                JSONObject lastNameJsonObject = (JSONObject) answerJsonObject.get("Last_Name");
                if (lastNameJsonObject != null) {
                    agent.setLastname((String) lastNameJsonObject.get(VALUE));
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
                    agent.setDateofbirth((String) dateOfBirthJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject phoneNumber1JsonObject = (JSONObject) answerJsonObject.get("Phone_No__1");
                if (phoneNumber1JsonObject != null) {
                    agent.setPhonenumber1((String) phoneNumber1JsonObject.get(VALUE));
                }
            } catch (Exception e) {

            }

            try {
                JSONObject phoneNumber2JsonObject = (JSONObject) answerJsonObject.get("Phone_No__2");
                if (phoneNumber2JsonObject != null) {
                    agent.setPhonenumber2((String) phoneNumber2JsonObject.get(VALUE));
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
                        agent.setResidentialaddressstreet((String) streetAddressObject.get(VALUE));
                    }

                    JSONObject stateObject = (JSONObject) addressObject.get(STATE);
                    if (stateObject != null) {
                        agent.setResidentialaddressstate((String) stateObject.get(VALUE));
                    }

                    JSONObject cityObject = (JSONObject) addressObject.get(CITY);
                    if (cityObject != null) {
                        agent.setResindetialaddresscity((String) cityObject.get(VALUE));
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
                        agent.setBusinessaddressstreet1((String) streetAddressObject.get(VALUE));
                    }

                    JSONObject stateObject = (JSONObject) addressObject.get(STATE);
                    if (stateObject != null) {
                        agent.setBusinessaddressstate1((String) stateObject.get(VALUE));
                    }

                    JSONObject cityObject = (JSONObject) addressObject.get(CITY);
                    if (cityObject != null) {
                        agent.setBusinessaddresscity1((String) cityObject.get(VALUE));
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
                        agent.setBusinessaddressstreet2((String) streetAddressObject.get(VALUE));
                    }

                    JSONObject stateObject = (JSONObject) addressObject.get(STATE);
                    if (stateObject != null) {
                        agent.setBusinessaddressstate2((String) stateObject.get(VALUE));
                    }

                    JSONObject cityObject = (JSONObject) addressObject.get(CITY);
                    if (cityObject != null) {
                        agent.setBusinessaddresscity2((String) cityObject.get(VALUE));
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
                        agent.setBusinessaddressstreet3((String) streetAddressObject.get(VALUE));
                    }

                    JSONObject stateObject = (JSONObject) addressObject.get(STATE);
                    if (stateObject != null) {
                        agent.setBusinessaddressstate3((String) stateObject.get(VALUE));
                    }

                    JSONObject cityObject = (JSONObject) addressObject.get(CITY);
                    if (cityObject != null) {
                        agent.setBusinessaddresscity3((String) cityObject.get(VALUE));
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
                        agent.setBusinessaddressstreet4((String) streetAddressObject.get(VALUE));
                    }

                    JSONObject stateObject = (JSONObject) addressObject.get(STATE);
                    if (stateObject != null) {
                        agent.setBusinessaddressstate4((String) stateObject.get(VALUE));
                    }

                    JSONObject cityObject = (JSONObject) addressObject.get(CITY);
                    if (cityObject != null) {
                        agent.setBusinessaddresscity4((String) cityObject.get(VALUE));
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
                JSONObject bvnJsonObject = (JSONObject) answerJsonObject.get("BVN");
                if (bvnJsonObject != null) {
                    agent.setBvn((String) bvnJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject meansOfIdJsonObject = (JSONObject) answerJsonObject.get("Means_of_ID");
                if (meansOfIdJsonObject != null) {
                    agent.setMeansofid((String) meansOfIdJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject idNumberJsonObject = (JSONObject) answerJsonObject.get("ID_Number");
                if (idNumberJsonObject != null) {
                    agent.setIdnumber((String) idNumberJsonObject.get(VALUE));
                }
            } catch (Exception e) {
            }

        } catch (Exception e) {
            logger.error("Json Object \"answer\" does not exist ");
        }
        return agent;
    }

    private String findInstitutionIdByTradeName(String tradeName){
        Query query = new Query();
        query.addCriteria(Criteria.where("tradeName").is(tradeName));
        InstitutionCategoryDetails categoryDetails = (InstitutionCategoryDetails)mongoRepositoryReactive.find(query, InstitutionCategoryDetails.class).block();
        if (categoryDetails != null){
            return categoryDetails.getInstitutionId();
        }
        return null;
    }
}
