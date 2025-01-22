package com.roumada.swiftscore.integration.service;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.dto.CompetitionRequestDTO;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.FootballClubDataLayer;
import com.roumada.swiftscore.service.CompetitionService;
import com.roumada.swiftscore.service.StatisticsService;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import com.roumada.swiftscore.util.PersistenceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.roumada.swiftscore.model.match.Competition.CompetitionType.LEAGUE;
import static org.junit.jupiter.api.Assertions.*;


class StatisticsServiceTests extends AbstractBaseIntegrationTest {

    @Autowired
    private StatisticsService service;

    @Autowired
    private CompetitionService compService;
    @Autowired
    private CompetitionDataLayer cdl;
    @Autowired
    private FootballClubDataLayer fcdl;

    @Test
    @DisplayName("Generate competition statistics - for a competition with two clubs after two match weeks simulated - " +
            "should return appropriate statistics")
    void generateCompetitionStatistics_forFullySimulatedTwoClubCompetition_shouldReturnSorted() {
        // arrange
        var ids = PersistenceTestUtils.getIdsOfSavedClubs(fcdl.saveAll(FootballClubTestUtils.getTwoFootballClubs()));
        var comp = compService.generateAndSave(new CompetitionRequestDTO(
                        "",
                        LEAGUE,
                        CountryCode.GB,
                        "2025-01-01",
                        "2025-12-30",
                        ids,
                        new SimulationValues(0)))
                .get();
        compService.simulateRound(comp);
        compService.simulateRound(comp);

        // act
        var standingsEither = service.getForCompetition(comp.getId());

        // assert
        assert (standingsEither).isRight();

        var standings = standingsEither.get();
        assertFalse(standings.isEmpty());
        assertEquals(2, standings.size());

        var standings1 = standings.get(0);
        var standings2 = standings.get(1);
        assertEquals("FC1", standings1.getFootballClubName());
        assertEquals(2, standings1.getWins());
        assertEquals(0, standings1.getDraws());
        assertEquals(0, standings1.getLosses());
        assertEquals(6, standings1.getPoints());
        assertTrue(standings1.getGoalsScored() > standings1.getGoalsConceded());
        assertEquals("FC2", standings2.getFootballClubName());
        assertEquals(0, standings2.getWins());
        assertEquals(0, standings2.getDraws());
        assertEquals(2, standings2.getLosses());
        assertEquals(0, standings2.getPoints());
        assertTrue(standings2.getGoalsScored() < standings2.getGoalsConceded());
    }
}
