package com.roumada.swiftscore.integration;

import com.roumada.swiftscore.TestDataLoader;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.persistence.repository.FootballMatchRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractBaseIntegrationTest {

    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    static {
        mongoDBContainer.start();
    }

    @Autowired
    TestDataLoader dataLoader;
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    CompetitionRoundRepository competitionRoundRepository;
    @Autowired
    FootballClubRepository footballClubRepository;
    @Autowired
    FootballMatchRepository footballMatchRepository;

    @DynamicPropertySource
    static void containersProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    protected void loadFCs() {
        dataLoader.saveFCs();
    }

    protected void loadCompetitionsWithFcs() {
        loadFCs();
        dataLoader.saveCompetitions();
    }

    protected Competition loadCompetitionWithFcs(){
        loadFCs();
        return dataLoader.saveCompetition();
    }

    @AfterEach
    void clear() {
        competitionRepository.deleteAll();
        competitionRoundRepository.deleteAll();
        footballClubRepository.deleteAll();
        footballMatchRepository.deleteAll();
    }
}