package com.software.finatech.lslb.cms.service.config;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.*;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
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

    @Value("${mongodb.hosts}")
    private String mongoHosts;
    @Value("${mongodb.replicaSet}")
    private String replicaSet;
    @Autowired
    private Environment environment;

    //@Value("${mongodb.port}")
    //private String mongoPort;

    @Value("${mongodb.database}")
    private String mongoDatabase;
    @Value("${mongodb.authDb}")
    private String authDatabase;
    @Value("${mongodb.username}")
    private String username;

    @Value("${mongodb.password}")
    private String password;

    @Bean
    public MongoClientOptions mongoOptions() {
        return MongoClientOptions.builder()
                .threadsAllowedToBlockForConnectionMultiplier(2)
                .maxConnectionIdleTime(1).connectionsPerHost(1)
                .minConnectionsPerHost(1).socketTimeout(2000).build();
    }

    @Bean("mongoClient")
    @Primary
    public MongoClient mongoClient() {
        StringBuffer uri = new StringBuffer();
        uri.append("mongodb://");
        if ((password != null && !password.isEmpty()) && (username != null && !username.isEmpty())) {
            uri.append(username);
            uri.append(":");
            uri.append(password);
            uri.append("@");
        }

        uri.append(mongoHosts);
        uri.append("/");

        if (replicaSet != null && !replicaSet.isEmpty()) {
            uri.append("?replicaSet=");
            uri.append(replicaSet);
            uri.append("&connectTimeoutMS=300000");
        } else {
            uri.append("?connectTimeoutMS=300000");
        }

        if(authDatabase != null && !authDatabase.isEmpty()) {
            uri.append("&authSource=");
            uri.append(authDatabase);
        }

        uri.append("&maxPoolSize=10000");
        uri.append("&socketTimeoutMS=300000");

        //logger.info(uri.toString());
        ConnectionString connectionString = new ConnectionString(uri.toString());

        //MongoCredential.createCredential();

        MongoClientSettings.Builder builder = MongoClientSettings.builder()
                //.readPreference(ReadPreference.primary())
                .clusterSettings(ClusterSettings.builder().applyConnectionString(connectionString).build())
                .connectionPoolSettings(ConnectionPoolSettings.builder().applyConnectionString(connectionString).build())
                .serverSettings(ServerSettings.builder().applyConnectionString(connectionString).build())
                .credentialList(connectionString.getCredentialList())
                .sslSettings(SslSettings.builder().applyConnectionString(connectionString).build())
                .socketSettings(SocketSettings.builder().applyConnectionString(connectionString).build());

        MongoClientSettings settings = builder.codecRegistry(com.mongodb.MongoClient.getDefaultCodecRegistry())
                .build();

        return MongoClients.create(settings);
        // return MongoClients.create("mongodb://localhost");
    }

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

        uri.append(mongoHosts);
        uri.append("/");
        if (replicaSet != null && !replicaSet.isEmpty()) {
            uri.append("?replicaSet=");
            uri.append(replicaSet);
            uri.append("&connectTimeoutMS=300000");
        } else {
            uri.append("?connectTimeoutMS=300000");
        }

        if(authDatabase != null && !authDatabase.isEmpty()) {
            uri.append("&authSource=");
            uri.append(authDatabase);
        }

        uri.append("&maxPoolSize=10000");
        uri.append("&socketTimeoutMS=300000");
        String connectionString = uri.toString();

        /*com.mongodb.MongoClient client = new com.mongodb.MongoClient(Arrays.asList(
                new ServerAddress("34.255.164.20", 27017),
                new ServerAddress("52.215.123.210", 27017)),
                MongoClientOptions.builder().serverSelectionTimeout(30000).connectTimeout(30000).build());*/
        //logger.info("Mongo template:   "+uri.toString());
        return new com.mongodb.MongoClient(new MongoClientURI(connectionString));
        //return client;
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
