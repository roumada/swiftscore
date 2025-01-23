package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.SimulationValues;
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
        var competition = Competition.builder()
                .name("Competition")
                .type(Competition.CompetitionType.LEAGUE)
                .simulationValues(new SimulationValues(0))
                .participants(fcs)
                .rounds(Collections.emptyList())
                .build();

        // act
        var id = competitionDataLayer.save(competition).getId();

        // assert
        assertTrue(competitionDataLayer.findCompetitionById(id).isPresent());
        assertFalse(competitionDataLayer.findCompetitionById(0L).isPresent());
    }
}
