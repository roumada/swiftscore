package com.roumada.swiftscore.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Configuration
@EnableMongoRepositories(basePackages = "com.roumada.swiftscore.persistence.repository")
@RequiredArgsConstructor
public class MongoConfiguration extends AbstractMongoClientConfiguration {
    private final Environment env;

    @Override
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString cs = new ConnectionString(Objects.requireNonNull(env.getProperty("spring.data.mongodb.uri")));
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
