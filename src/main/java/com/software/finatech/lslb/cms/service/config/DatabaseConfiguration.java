package com.software.finatech.lslb.cms.service.config;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientURI;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.*;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by djaiyeola on 9/20/17.
 */
@Configuration
@EnableMongoAuditing
public class DatabaseConfiguration {
    @Value("${mongodb.host}")
    private String mongoHost;
    private static Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);
    @Autowired
    private Environment environment;

    @Value("${mongodb.port}")
    private String mongoPort;

    @Value("${mongodb.database}")
    private String mongoDatabase;
    @Value("${mongodb.username}")
    private String username;

    @Value("${mongodb.password}")
    private String password;

    @Bean("mongoClient")
    @Primary
    public MongoClient mongoClient() {
        //mongodb://cloud:Jdk19Version@54.191.139.8:27017
        StringBuffer uri = new StringBuffer();
        uri.append("mongodb://");
        if ((password != null && !password.isEmpty()) && (username != null && !username.isEmpty())) {
            uri.append(username);
            uri.append(":");
            uri.append(password);
            uri.append("@");
        }

        uri.append(mongoHost);
        uri.append(":");
        uri.append(mongoPort);
//        uri.append("/");
//        uri.append(mongoDatabase);

        //logger.info(uri.toString());
        ConnectionString connectionString = new ConnectionString(uri.toString());

        MongoClientSettings.Builder builder = MongoClientSettings.builder()
                .clusterSettings(ClusterSettings.builder().applyConnectionString(connectionString).build())
                .connectionPoolSettings(ConnectionPoolSettings.builder().applyConnectionString(connectionString).build())
                .serverSettings(ServerSettings.builder().applyConnectionString(connectionString).build())
                .credentialList(connectionString.getCredentialList())
                .sslSettings(SslSettings.builder().applyConnectionString(connectionString).build())
                .socketSettings(SocketSettings.builder().applyConnectionString(connectionString).build());

        MongoClientSettings settings = builder.codecRegistry(com.mongodb.MongoClient.getDefaultCodecRegistry()).build();

        return MongoClients.create(settings);
        // return MongoClients.create("mongodb://localhost");
    }

   /* @Bean("reactiveMongoClient")
    public MongoClient reactiveMongoClient() {
        StringBuffer uri = new StringBuffer();
        uri.append("mongodb://");
        if((password!=null && !password.isEmpty()) && (username!=null && !username.isEmpty())) {
            uri.append(username);
            uri.append(":");
            uri.append(password);
            uri.append("@");
        }

        uri.append(mongoHost);
        uri.append(":");
        uri.append(mongoPort);

        return MongoClients.create(uri.toString());
    }*/


    @Bean("reactiveMongoTemplate")
    ReactiveMongoTemplate reactiveMongoTemplate(MongoClient mongoClient, MappingMongoConverter mappingMongoConverter) {
        return new ReactiveMongoTemplate(new SimpleReactiveMongoDatabaseFactory(mongoClient, mongoDatabase),
                mappingMongoConverter);
    }

    @Bean
    public com.mongodb.MongoClient mongo() {
        //mongodb://cloud:Jdk19Version@54.191.139.8:27017
        StringBuffer uri = new StringBuffer();
        uri.append("mongodb://");
        if ((password != null && !password.isEmpty()) && (username != null && !username.isEmpty())) {
            uri.append(username);
            uri.append(":");
            uri.append(password);
            uri.append("@");
        }

        uri.append(mongoHost);
        uri.append(":");
        uri.append(mongoPort);
//        uri.append("/");
//        uri.append(mongoDatabase);


        //logger.info("Mongo template:   "+uri.toString());
        return new com.mongodb.MongoClient(new MongoClientURI(uri.toString()));
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongo(), mongoDatabase);
    }


    /*@Bean
    public AuditorAware<String> myAuditorProvider() {
        return new SpringSecurityAuditorAware();
    }*/

    private List<String> getActiveProfiles() {
        String[] activeProfileArray = environment.getActiveProfiles();
        return Arrays.asList(activeProfileArray);
    }
}
