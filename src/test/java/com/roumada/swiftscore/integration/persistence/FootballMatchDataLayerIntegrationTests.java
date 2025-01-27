package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FootballMatchDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballMatchDataLayer dataLayer;
    @Autowired
    private FootballClubDataLayer fcDataLayer;

    @Test
    @DisplayName("Should save a football match to the database")
    void shouldSaveAMatchToDatabase() {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fcDataLayer.save(fc1);
        fcDataLayer.save(fc2);
        var match = new FootballMatch(fc1, fc2);

        // act
        var saved = dataLayer.save(match);
        var optionalMatch = dataLayer.findMatchById(saved.getId());

        // assert
        assertThat(optionalMatch).isPresent();
        var retrievedMatch = optionalMatch.get();
        assertThat(retrievedMatch.getId()).isEqualTo(saved.getId());
        assertEquals(retrievedMatch.getId(), retrievedMatch.getId());
    }
}
