package com.software.finatech.lslb.cms.service.util.adapters;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DeviceMagicAdapter {
    private Logger logger = LoggerFactory.getLogger(DeviceMagicAdapter.class);
    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    private DeviceMagicAgentAdapter deviceMagicAgentAdapter;

    public List<DeviceMagicAgent> getAgentsFromAPIResponse(String apiResponseJson) {
        String VALUE = "value";
        String STREET_ADDRESS = "Street_Address";
        String STATE = "State";
        String CITY = "City";

        List<DeviceMagicAgent> agentList = new ArrayList<>();
        JSONObject responseJsonObject = new JSONObject(apiResponseJson);
        JSONArray submissionsArray = responseJsonObject.getJSONArray("submissions");

        for (int i = 0; i < submissionsArray.length(); i++) {
            JSONObject submissionObjectAtIndex = submissionsArray.getJSONObject(i);
            JSONObject submissionObject = submissionObjectAtIndex.getJSONObject("submission");

            DeviceMagicAgent agent = new DeviceMagicAgent();

            try {
                JSONObject operatorIdJsonObject = submissionObject.getJSONObject("Operator_ID");
                if (operatorIdJsonObject != null) {
                    String operatorId = operatorIdJsonObject.getString(VALUE);
                    agent.setOperatorId(operatorId);
                }

            } catch (Exception e) {

            }

            try {
                JSONObject gamingCategoryJsonObject = submissionObject.getJSONObject("Gaming_Category");
                if (gamingCategoryJsonObject != null) {
                    agent.setGamingCategopry(gamingCategoryJsonObject.getString(VALUE));
                }
            } catch (Exception e) {

            }

            try {
                JSONObject firstNameJsonObject = submissionObject.getJSONObject("First_Name");
                if (firstNameJsonObject != null) {
                    agent.setFirstName(firstNameJsonObject.getString(VALUE));
                }
            } catch (Exception e) {

            }

            try {
                JSONObject lastNameJsonObject = submissionObject.getJSONObject("Last_Name");
                if (lastNameJsonObject != null) {
                    agent.setLastName(lastNameJsonObject.getString(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject titleJsonObject = submissionObject.getJSONObject("Title");
                if (titleJsonObject != null) {
                    agent.setTitle(titleJsonObject.getString(VALUE));
                }
            } catch (Exception e) {
            }


            try {
                JSONObject dateOfBirthJsonObject = submissionObject.getJSONObject("Date_Of_Birth");
                if (dateOfBirthJsonObject != null) {
                    agent.setDateOfBirth(dateOfBirthJsonObject.getString(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject phoneNumber1JsonObject = submissionObject.getJSONObject("Phone_No__1");
                if (phoneNumber1JsonObject != null) {
                    agent.setPhoneNumber1(phoneNumber1JsonObject.getString(VALUE));
                }
            } catch (Exception e) {

            }

            try {
                JSONObject phoneNumber2JsonObject = submissionObject.getJSONObject("Phone_No__2");
                if (phoneNumber2JsonObject != null) {
                    agent.setPhoneNumber2(phoneNumber2JsonObject.getString(VALUE));
                }
            } catch (Exception e) {

            }

            try {
                JSONObject residentialAddressJsonObject = submissionObject.getJSONObject("Residential_Address");
                if (residentialAddressJsonObject != null) {
                    JSONArray addressArray = residentialAddressJsonObject.getJSONArray(VALUE);
                    JSONObject addressObject = addressArray.getJSONObject(0);
                    JSONObject streetAddressObject = addressObject.getJSONObject(STREET_ADDRESS);
                    if (streetAddressObject != null) {
                        agent.setResidentialAddressStreet(streetAddressObject.getString(VALUE));
                    }

                    JSONObject stateObject = addressObject.getJSONObject(STATE);
                    if (stateObject != null) {
                        agent.setResidentialAddressState(stateObject.getString(VALUE));
                    }

                    JSONObject cityObject = addressObject.getJSONObject(CITY);
                    if (cityObject != null) {
                        agent.setResindetialAddressCity(cityObject.getString(VALUE));
                    }
                }
            } catch (Exception e) {
            }

            try {
                JSONObject businessAddress1JsonObject = submissionObject.getJSONObject("Business_Address_1");
                if (businessAddress1JsonObject != null) {
                    JSONArray businessAddressArray = businessAddress1JsonObject.getJSONArray(VALUE);
                    JSONObject addressObject = businessAddressArray.getJSONObject(0);
                    JSONObject streetAddressObject = addressObject.getJSONObject(STREET_ADDRESS);
                    if (streetAddressObject != null) {
                        agent.setBusinessAddressStreet1(streetAddressObject.getString(VALUE));
                    }

                    JSONObject stateObject = addressObject.getJSONObject(STATE);
                    if (stateObject != null) {
                        agent.setBusinessAddressState1(stateObject.getString(VALUE));
                    }

                    JSONObject cityObject = addressObject.getJSONObject(CITY);
                    if (cityObject != null) {
                        agent.setBusinessAddressCity1(cityObject.getString(VALUE));
                    }
                }
            } catch (Exception e) {
            }

            try {
                JSONObject businessAddress2JsonObject = submissionObject.getJSONObject("Business_Address_2");
                if (businessAddress2JsonObject != null) {
                    JSONArray businessAddressArray = businessAddress2JsonObject.getJSONArray(VALUE);
                    JSONObject addressObject = businessAddressArray.getJSONObject(0);
                    JSONObject streetAddressObject = addressObject.getJSONObject(STREET_ADDRESS);
                    if (streetAddressObject != null) {
                        agent.setBusinessAddressStreet2(streetAddressObject.getString(VALUE));
                    }

                    JSONObject stateObject = addressObject.getJSONObject(STATE);
                    if (stateObject != null) {
                        agent.setBusinessAddressState2(stateObject.getString(VALUE));
                    }

                    JSONObject cityObject = addressObject.getJSONObject(CITY);
                    if (cityObject != null) {
                        agent.setBusinessAddressCity2(cityObject.getString(VALUE));
                    }
                }
            } catch (Exception e) {
            }

            try {
                JSONObject businessAddress3JsonObject = submissionObject.getJSONObject("Business_Address_3");
                if (businessAddress3JsonObject != null) {
                    JSONArray businessAddressArray = businessAddress3JsonObject.getJSONArray(VALUE);
                    JSONObject addressObject = businessAddressArray.getJSONObject(0);
                    JSONObject streetAddressObject = addressObject.getJSONObject(STREET_ADDRESS);
                    if (streetAddressObject != null) {
                        agent.setBusinessAddressStreet3(streetAddressObject.getString(VALUE));
                    }

                    JSONObject stateObject = addressObject.getJSONObject(STATE);
                    if (stateObject != null) {
                        agent.setBusinessAddressState3(stateObject.getString(VALUE));
                    }

                    JSONObject cityObject = addressObject.getJSONObject(CITY);
                    if (cityObject != null) {
                        agent.setBusinessAddressCity3(cityObject.getString(VALUE));
                    }
                }
            } catch (Exception e) {
            }

            try {
                JSONObject businessAddress4JsonObject = submissionObject.getJSONObject("Business_Address_4");
                if (businessAddress4JsonObject != null) {
                    JSONArray businessAddressArray = businessAddress4JsonObject.getJSONArray(VALUE);
                    JSONObject addressObject = businessAddressArray.getJSONObject(0);
                    JSONObject streetAddressObject = addressObject.getJSONObject(STREET_ADDRESS);
                    if (streetAddressObject != null) {
                        agent.setBusinessAddressStreet4(streetAddressObject.getString(VALUE));
                    }

                    JSONObject stateObject = addressObject.getJSONObject(STATE);
                    if (stateObject != null) {
                        agent.setBusinessAddressState4(stateObject.getString(VALUE));
                    }

                    JSONObject cityObject = addressObject.getJSONObject(CITY);
                    if (cityObject != null) {
                        agent.setBusinessAddressCity4(cityObject.getString(VALUE));
                    }
                }
            } catch (Exception e) {
            }

            try {
                JSONObject emailJsonObject = submissionObject.getJSONObject("Email");
                if (emailJsonObject != null) {
                    agent.setEmail(emailJsonObject.getString(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject bvnJsonObject = submissionObject.getJSONObject("BVN");
                if (bvnJsonObject != null) {
                    agent.setBvn(bvnJsonObject.getString(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject meansOfIdJsonObject = submissionObject.getJSONObject("Means_of_ID");
                if (meansOfIdJsonObject != null) {
                    agent.setMeansOfId(meansOfIdJsonObject.getString(VALUE));
                }
            } catch (Exception e) {
            }

            try {
                JSONObject idNumberJsonObject = submissionObject.getJSONObject("ID_Number");
                if (idNumberJsonObject != null) {
                    agent.setIdNumber(idNumberJsonObject.getString(VALUE));
                }
            } catch (Exception e) {
            }


            agentList.add(agent);
        }
        return agentList;
    }

//    public void setAgentImagesBase64AndSave(List<DeviceMagicAgent> agentList) {
//        for (DeviceMagicAgent agent : agentList) {
//            RestTemplate restTemplate = new RestTemplate();
//            restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
//            headers.set("Authorization", "Basic WlRTVW9TTXFqVUZ1d3dILVNEMVo6eA==");
//            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.84 Safari/537.36");
//            headers.set("Postman-Token", "9cb4f3c5-8231-493c-8bf2-3b4b45500bb5");
//            headers.set("Cache-Control", "no-cache");
//            headers.setCacheControl("no-cache");
//
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//            HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
//            requestFactory.setHttpClient(httpClient);
//            //   restTemplate.setRequestFactory(requestFactory);
//            try {
//                ResponseEntity<byte[]> response = restTemplate.exchange(agent.getImageurl(), HttpMethod.GET,
//                        entity, byte[].class);
//
//                byte[] bytes = response.getBody();
//                HttpStatus httpStatus = response.getStatusCode();
//                logger.info("http status : {}", httpStatus);
//
//            } catch (Exception e) {
//                logger.error("Error fetching image ", e);
//            } finally {
//                logger.info("tunde");
//                //   mongoRepositoryReactive.saveOrUpdate(agent);
//            }
//        }
//    }


    private String getDeviceMagicResponseStrings() {
        String url = "https://www.devicemagic.com/api/forms/7306863/device_magic_database.json";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic WlRTVW9TTXFqVUZ1d3dILVNEMVo6eA==");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.84 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.info("response : {}", response);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error fetching users data from device magic", e);
            return null;
        }
    }

//    public void getAgentLists() {
//        String responseJson = getDeviceMagicResponseStrings();
//        if (responseJson != null) {
//            List<DeviceMagicAgent> deviceMagicAgents = getAgentsFromAPIResponse(responseJson);
//            List<Agent> agents = agentFromDeviceMagicAgents(deviceMagicAgents);
//            logger.info("{}", agents);
//        }
   // }


//    public DeviceMagicAgent fromJsonString(String jsonString) {
//        String VALUE = "value";
//        String STREET_ADDRESS = "Street_Address";
//        String STATE = "State";
//        String CITY = "City";
//        DeviceMagicAgent agent = new DeviceMagicAgent();
//        JSONObject jsonObject = new JSONObject(jsonString);
//        try {
//            JSONObject answerJsonObject = jsonObject.getJSONObject("answers");
//            try {
//                JSONObject operatorIdJsonObject = answerJsonObject.getJSONObject("Operator_ID");
//                if (operatorIdJsonObject != null) {
//                    String operatorId = operatorIdJsonObject.getString(VALUE);
//                    agent.setOperatorid(operatorId);
//                }
//            } catch (Exception e) {
//            }
//            try {
//                JSONObject gamingCategoryJsonObject = answerJsonObject.getJSONObject("Gaming_Category");
//                if (gamingCategoryJsonObject != null) {
//                    agent.setGamingcategopry(gamingCategoryJsonObject.getString(VALUE));
//                }
//            } catch (Exception e) {
//            }
//            try {
//                JSONObject firstNameJsonObject = answerJsonObject.getJSONObject("First_Name");
//                if (firstNameJsonObject != null) {
//                    agent.setFirstname(firstNameJsonObject.getString(VALUE));
//                }
//            } catch (Exception e) {
//
//            }
//
//            try {
//                JSONObject lastNameJsonObject = answerJsonObject.getJSONObject("Last_Name");
//                if (lastNameJsonObject != null) {
//                    agent.setLastname(lastNameJsonObject.getString(VALUE));
//                }
//            } catch (Exception e) {
//            }
//
//            try {
//                JSONObject titleJsonObject = answerJsonObject.getJSONObject("Title");
//                if (titleJsonObject != null) {
//                    agent.setTitle(titleJsonObject.getString(VALUE));
//                }
//            } catch (Exception e) {
//            }
//
//
//
//
//        } catch (Exception e) {
//            logger.error("Json Object \"answer\" does not exist ");
//        }
//        return agent;
//    }
}
