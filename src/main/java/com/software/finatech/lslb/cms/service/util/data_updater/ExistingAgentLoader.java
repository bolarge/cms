package com.software.finatech.lslb.cms.service.util.data_updater;

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

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Component
public class ExistingAgentLoader {
    private static final Logger logger = LoggerFactory.getLogger(ExistingAgentLoader.class);

    @Autowired
    private DeviceMagicAgentAdapter deviceMagicAgentAdapter;
    private String[] fileNames = new String[]{"30676121.json", "30676511.json"};

    // file =ResourceUtils.getFile("classpath:date-csv/Invoices.csv");

   // @PostConstruct
    public void init() {
        loadAgents();
    }


    private void loadAgents() {
        for (String fileName : fileNames) {
            String resourceFileName = String.format("classpath:agent-data/json/%s", fileName);
            try {
                File file = ResourceUtils.getFile(resourceFileName);
                DeviceMagicAgent deviceMagicAgent = agentFromFile(file);
                if (deviceMagicAgent != null) {
                    deviceMagicAgent.setImageFileName(fileName);
                }
                logger.info("Agent found \n\n\n\n {}", deviceMagicAgent);
            } catch (Exception e) {
                logger.error("Error occurred ", e);
            }
        }
    }

    private DeviceMagicAgent agentFromFile(File file) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(file));
        if (jsonObject != null) {
            return deviceMagicAgentAdapter.fromJsonObject(jsonObject);
        }
        return null;
    }
}
