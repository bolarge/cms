package com.software.finatech.lslb.cms.service.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author adeyi.adebolu
 * created on 20/06/2019
 */

@Configuration
public class S3ClientConfig {


    @Value("${amazon.s3.access-key-id}")
    private String accessKeyId;
    @Value("${amazon.s3.secret-access-key}")
    private String secretAccessKey;

    @Bean
    public AmazonS3 amazonS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey)))
                .withRegion(Regions.EU_WEST_1).build();
    }
}
