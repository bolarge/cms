package com.software.finatech.lslb.cms.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author adeyi.adebolu
 * created on 14/05/2019
 */
@Component
public class EnvironmentUtils {

    @Autowired
    private Environment environment;

    public boolean isTestEnvironment() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test");
    }

    public boolean isStagingEnvironment() {
        return Arrays.asList(environment.getActiveProfiles()).contains("staging");
    }

    public boolean isDevelopmentEnvironment() {
        return Arrays.asList(environment.getActiveProfiles()).contains("development");
    }

    public boolean isProductionEnvironment() {
        return Arrays.asList(environment.getActiveProfiles()).contains("production");
    }
}
