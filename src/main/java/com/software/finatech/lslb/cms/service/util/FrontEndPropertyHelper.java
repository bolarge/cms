package com.software.finatech.lslb.cms.service.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
public class FrontEndPropertyHelper {

    @Value("${racs.ui.host}")
    private String frontEndHost;
    @Value("${racs.ui.port}")
    private String frontEndPort;
    private String frontEndUrl;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void initialize() {
        String[] activeProfileArray = environment.getActiveProfiles();
        List<String> activeProfiles = Arrays.asList(activeProfileArray);
        frontEndUrl = frontEndHost;
        if (!StringUtils.isEmpty(frontEndPort)) {
            frontEndUrl = String.format("%s:%s", frontEndHost, frontEndPort);
        }
        if (activeProfiles.contains("development") ||
                activeProfiles.contains("test") ||
                activeProfiles.contains("staging")) {
            frontEndUrl = String.format("http://%s", frontEndUrl);
        }

        if (activeProfiles.contains("production")) {
            frontEndUrl = String.format("https://%s", frontEndUrl);
        }
    }

    public String getFrontEndUrl() {
        return frontEndUrl;
    }
}
