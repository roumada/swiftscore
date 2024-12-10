package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FootballClubDataLayerIntegrationTest extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubRepository repository;

    @Test
    @DisplayName("Should save a football club to the database")
    void shoudSaveToDatabase() {
        FootballClub fc = FootballClub.builder().id(null).name("Norf FC").victoryChance(0.33f).build();

        FootballClub saved = repository.save(fc);
        Optional<FootballClub> optionalFC = repository.findById(saved.getId());

        assertThat(optionalFC).isPresent();

        FootballClub retrievedFC = optionalFC.get();
        assertThat(retrievedFC.getName()).isEqualTo(fc.getName());
    }
}

