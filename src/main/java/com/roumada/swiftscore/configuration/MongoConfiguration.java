package com.roumada.swiftscore.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackages = "com.roumada.swiftscore.repository")
public class MongoConfiguration extends AbstractMongoClientConfiguration {
    @Override
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    public MongoClient mongoClient(){
        ConnectionString cs = new ConnectionString("mongodb://localhost:27017/test");
        MongoClientSettings mcs = MongoClientSettings.builder()
                .applyConnectionString(cs)
                .build();

        return MongoClients.create(mcs);
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.roumada.swiftscore");
    }
}
