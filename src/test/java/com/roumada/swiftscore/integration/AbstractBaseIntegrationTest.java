package com.roumada.swiftscore.integration;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.TestDataLoader;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.persistence.repository.CompetitionRepository;
import com.roumada.swiftscore.persistence.repository.CompetitionRoundRepository;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.persistence.repository.FootballMatchRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

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

    protected void loadFootballClubs() {
        dataLoader.saveFCs();
    }

    protected void loadCompetitionsWithFcs() {
        loadFootballClubs();
        dataLoader.saveCompetitions();
    }

    protected Competition loadCompetitionWithFcs() {
        loadFootballClubs();
        return dataLoader.saveCompetition();
    }

    protected List<Competition> getCompetitionsForCountry(CountryCode countryCode, int size) {
        return competitionRepository.findByCountry(countryCode, Pageable.ofSize(size)).getContent();
    }

    protected List<FootballClub> getFootballClubsForCountry(CountryCode countryCode, int size) {
        return footballClubRepository.findByCountry(countryCode, Pageable.ofSize(size)).getContent();
    }

    protected List<Long> getFootballClubIdsForCountry(CountryCode countryCode, int size) {
        return getFootballClubsForCountry(countryCode, size).stream().map(FootballClub::getId).toList();
    }

    @AfterEach
    void clear() {
        competitionRepository.deleteAll();
        competitionRoundRepository.deleteAll();
        footballClubRepository.deleteAll();
        footballMatchRepository.deleteAll();
    }
}