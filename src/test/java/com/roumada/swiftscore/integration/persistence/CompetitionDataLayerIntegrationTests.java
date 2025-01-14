package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.SimulatorValues;
import com.roumada.swiftscore.model.match.Competition;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitionDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private CompetitionDataLayer competitionDataLayer;
    @Autowired
    private FootballClubDataLayer footballClubDataLayer;

    @Test
    @DisplayName("Should save and find competition to/from database")
    void shouldSaveAndFindCompetitionToAndFromDatabase() {
        // arrange
        var fcs = footballClubDataLayer.saveAll(FootballClubTestUtils.getTwoFootballClubs());
        var competition = new Competition(new SimulatorValues(0), fcs, Collections.emptyList());

        // act
        var id = competitionDataLayer.saveCompetition(competition).getId();

        // assert
        assertTrue(competitionDataLayer.findCompetitionById(id).isPresent());
        assertFalse(competitionDataLayer.findCompetitionById(0L).isPresent());
    }
}
