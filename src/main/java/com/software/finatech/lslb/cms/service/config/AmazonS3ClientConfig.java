//package com.software.finatech.lslb.cms.service.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.AwsCredentials;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3AsyncClient;
//import software.amazon.awssdk.services.s3.S3Client;
//
///**
// * @author adeyi.adebolu
// * created on 30/04/2019
// */
//
//@Configuration
//public class AmazonS3ClientConfig {
//
//    @Value("${amazon.s3.access-key-id}")
//    private String accessKeyId;
//    @Value("${amazon.s3.secret-access-key}")
//    private String secretAccessKey;
//
//    @Bean
//    public S3AsyncClient client() {
//        return S3AsyncClient
//                .builder()
//                .credentialsProvider(() -> new AwsCredentials(accessKeyId, secretAccessKey))
//                .region(Region.EU_WEST_1)
//                .build();
//    }
//
//    @Bean
//    public S3Client syncClient() {
//        return S3Client.builder()
//                .credentialsProvider(() -> new AwsCredentials(accessKeyId, secretAccessKey))
//                .region(Region.EU_WEST_1)
//                .build();
//    }
//}
