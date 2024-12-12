package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class FootballClubDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubDataLayer dataLayer;

    @Test
    @DisplayName("Should save a football club to the database")
    void shouldSaveToDatabase() {
        // arrange
        var fc = FootballClub.builder().name("Norf FC").victoryChance(0.33f).build();
        var saved = dataLayer.save(fc);

        // act
        var optionalFC = dataLayer.findById(saved.getId());

        // assert
        assertThat(optionalFC).isPresent();
        var retrievedFC = optionalFC.get();
        assertThat(retrievedFC.getName()).isEqualTo(fc.getName());
    }
}

