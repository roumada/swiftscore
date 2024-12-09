package com.roumada.swiftscore.integration.repository;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.repository.FootballClubRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FootballClubDataLayerIntegrationTest extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubRepository repository;

    @Test
    void test_givenFCRepository_whenSaveAndRetrieveClub_thenOK() {
        FootballClub fc = FootballClub.builder().id(1).name("Norf FC").victoryChance(0.33f).build();

        FootballClub saved = repository.save(fc);
        Optional<FootballClub> optionalFC = repository.findById(saved.getId());

        assertThat(optionalFC).isPresent();

        FootballClub retrievedFC = optionalFC.get();
        assertThat(retrievedFC.getName()).isEqualTo(fc.getName());
    }
}

