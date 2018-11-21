package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.util.adapters.DeviceMagicAgent;
import com.software.finatech.lslb.cms.service.util.adapters.DeviceMagicAgentAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Component
public class ExistingAgentLoader {
    private static final Logger logger = LoggerFactory.getLogger(ExistingAgentLoader.class);
    @Autowired
    private DeviceMagicAgentAdapter deviceMagicAgentAdapter;
//    private String[] fileNames = new String[]{"30676121.json", "30676511.json"};

    private DeviceMagicAgent agentFromFile(File file) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(file));
        if (jsonObject != null) {
            return deviceMagicAgentAdapter.fromJsonObject(jsonObject);
        }
        return null;
    }

    @Async
    public void loadExistingAgents() {
        for (int i = 30670000; i < 33700000; i++) {
            loadAgentForFilePath(String.valueOf(i));
        }
    }

    private void loadAgentForFilePath(String filePath) {
        String resourceFileName = String.format("classpath:agent-data/json/%s.json", filePath);
        try {
            File file = ResourceUtils.getFile(resourceFileName);
            DeviceMagicAgent deviceMagicAgent = agentFromFile(file);
            deviceMagicAgent.setSubmissionId(filePath);
            if (deviceMagicAgent == null) {
                logger.info("No Device Magic Agent for submission {}", filePath);
                return;
            }
            deviceMagicAgentAdapter.saveDeviceMagicAgentToAgentDb(deviceMagicAgent);
        } catch (FileNotFoundException e) {
            //     logger.error("File with resource filepath {} not found , message -> {}", filePath, e.getMessage());
        } catch (Exception e) {
            logger.error("Error occurred ", e);
        }
    }

//
//
//    private void loadAgents() {
//        for (String fileName : fileNames) {
//            String resourceFileName = String.format("classpath:agent-data/json/%s", fileName);
//            try {
//                File file = ResourceUtils.getFile(resourceFileName);
//                DeviceMagicAgent deviceMagicAgent = agentFromFile(file);
//                if (deviceMagicAgent != null) {
//                    deviceMagicAgent.setImageFileName(fileName);
//                }
//                logger.info("Agent found \n\n\n\n {}", deviceMagicAgent);
//            } catch (Exception e) {
//                logger.error("Error occurred ", e);
//            }
//        }
//    }
}
