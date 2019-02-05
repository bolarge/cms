package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.util.adapters.DeviceMagicAgentAdapter;
import com.software.finatech.lslb.cms.service.util.adapters.model.DeviceMagicAgent;
import com.software.finatech.lslb.cms.service.util.adapters.model.DeviceMagicAgentInstitutionCategoryDetails;
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
            //return deviceMagicAgentAdapter.fromJsonObject(jsonObject);
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
}