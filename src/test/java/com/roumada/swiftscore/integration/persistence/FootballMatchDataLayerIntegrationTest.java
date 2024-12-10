package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class FootballMatchDataLayerIntegrationTest extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballMatchDataLayer dataLayer;

    @Test
    @DisplayName("Should save a football match to the database")
    void shouldSaveAMatchToDatabase() {
        // arrange
        var stats1 = new FootballMatchStatistics(null);
        var stats2 = new FootballMatchStatistics(null);
        var match = new FootballMatch(stats1, stats2);

        // act
        var saved = dataLayer.saveMatch(match);
        var optionalMatch = dataLayer.findMatchById(saved.getId());

        // assert
        assertThat(optionalMatch).isPresent();
        var retrievedMatch = optionalMatch.get();
        assertThat(retrievedMatch.getId()).isEqualTo(saved.getId());
    }
}
