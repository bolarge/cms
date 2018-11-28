package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.util.adapters.DeviceMagicAgent;
import com.software.finatech.lslb.cms.service.util.adapters.DeviceMagicAgentAdapter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExistingAgentLoader {
    private static final Logger logger = LoggerFactory.getLogger(ExistingAgentLoader.class);
    @Autowired
    private DeviceMagicAgentAdapter deviceMagicAgentAdapter;

    private DeviceMagicAgent agentFromFile(File file) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(file));
        if (jsonObject != null) {
            return deviceMagicAgentAdapter.fromJsonObject(jsonObject);
        }
        return null;
    }

    public void loadExistingAgents() {
        try {
            File folder = ResourceUtils.getFile("classpath:agent-data/json");
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        loadAgentForFile(listOfFile);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred while parsing file", e);
        }
    }

    private void loadAgentForFile(File file) throws IOException, ParseException {
        DeviceMagicAgent deviceMagicAgent = agentFromFile(file);
        String submissionId = file.getName().replace(".json", "");
        if (deviceMagicAgent == null) {
            logger.info("No Device Magic Agent for submission {}", submissionId);
            return;
        }
        deviceMagicAgent.setSubmissionId(submissionId);
        deviceMagicAgentAdapter.saveDeviceMagicAgentToAgentDb(deviceMagicAgent);
    }

    public void loadAgentsFromCSV(MultipartFile multipartFile) throws LicenseServiceException {
        if (multipartFile.isEmpty()) {
            throw new LicenseServiceException("File is empty");
        }
        Map<String, DeviceMagicAgent> deviceMagicAgentMap = new HashMap<>();
        try {
            String completeData = new String(multipartFile.getBytes());
            String[] rows = completeData.split("\\r?\\n");
            for (int i = 2; i < rows.length; i++) {
                String[] columns = rows[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
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
                        deviceMagicAgent.setOperatorId(columns[6]);
                        //     deviceMagicAgent.getOperatorIds().add(columns[6]);
                        deviceMagicAgent.setGamingCategopry(columns[7]);
                        deviceMagicAgent.setGender(columns[8]);
                        deviceMagicAgent.setTitle(columns[9]);
                        deviceMagicAgent.setFirstName(columns[10]);
                        deviceMagicAgent.setLastName(columns[11]);
                        deviceMagicAgent.setDateOfBirth(columns[12]);
                        deviceMagicAgent.setPhoneNumber1(columns[13]);
                        deviceMagicAgent.setPhoneNumber2(columns[14]);
                        deviceMagicAgent.setResidentialAddressStreet(columns[15]);
                        deviceMagicAgent.setResindetialAddressCity(columns[16]);
                        deviceMagicAgent.setResidentialAddressState(columns[17]);
                        deviceMagicAgent.setBusinessAddressStreet1(columns[18]);
                        deviceMagicAgent.setBusinessAddressCity1(columns[19]);
                        deviceMagicAgent.setBusinessAddressState1(columns[20]);
                        deviceMagicAgent.setBusinessAddressStreet2(columns[21]);
                        deviceMagicAgent.setBusinessAddressCity2(columns[22]);
                        deviceMagicAgent.setBusinessAddressState2(columns[23]);
                        deviceMagicAgent.setBusinessAddressStreet3(columns[24]);
                        deviceMagicAgent.setBusinessAddressCity3(columns[25]);
                        deviceMagicAgent.setBusinessAddressState3(columns[26]);
                        deviceMagicAgent.setBusinessAddressStreet4(columns[27]);
                        deviceMagicAgent.setBusinessAddressCity4(columns[28]);
                        deviceMagicAgent.setBusinessAddressState4(columns[29]);
                        deviceMagicAgent.setEmail(email);
                        deviceMagicAgent.setBvn(bvn);
                        deviceMagicAgent.setMeansOfId(columns[32]);
                        deviceMagicAgent.setIdNumber(columns[33]);
                    }
                    deviceMagicAgent.getOperatorIds().add(columns[6]);
                    if (deviceMagicAgent.getOperatorIds().size() > 1) {
                        logger.info("Agent {} has more than one operator id", columns[4]);
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
}