package com.software.finatech.lslb.cms.service;

import com.software.finatech.lslb.cms.userservice.util.DatabaseLoaderUtils;
import com.software.finatech.lslb.cms.userservice.util.GlobalApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class LSLBCMSApplication {

    private static Logger logger = LoggerFactory.getLogger(LSLBCMSApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(LSLBCMSApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        logger.info("");

        ApplicationContext ctx = app.run(args);


        // Our global app context
        GlobalApplicationContext.ctx = ctx;

        //DateUtil.parseDate("2017-21-09 09:11:00.000000");


        DatabaseLoaderUtils databaseLoaderUtils = (DatabaseLoaderUtils) ctx.getBean("databaseLoaderUtils");
        databaseLoaderUtils.runSeedData();
        Environment env = ctx.getBean(Environment.class);
        if (Arrays.asList(env.getActiveProfiles()).contains("development")) {
            databaseLoaderUtils.runLoadTestData();
        }

        if (Arrays.asList(env.getActiveProfiles()).contains("development") || Arrays.asList(env.getActiveProfiles()).contains("test")) {
            databaseLoaderUtils.generateAuthTestData();
        }

        databaseLoaderUtils.runLoadData();
    }
}
