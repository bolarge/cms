package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.util.adapters.DeviceMagicAgent;
import com.software.finatech.lslb.cms.service.util.adapters.DeviceMagicAgentAdapter;
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
                    DeviceMagicAgent deviceMagicAgent = deviceMagicAgentMap.get(bvn);
                    if (deviceMagicAgent == null) {
                        deviceMagicAgent = new DeviceMagicAgent();
                    }
                    deviceMagicAgent.getOperatorIds().add(columns[6]);
                    if (deviceMagicAgent.getOperatorIds().size() > 1) {
                        logger.info("Agent {} has more than one operator id", columns[4]);
                    }

                    deviceMagicAgentMap.put(bvn, deviceMagicAgent);
                }
            }
        } catch (Exception e) {

        }
    }
}