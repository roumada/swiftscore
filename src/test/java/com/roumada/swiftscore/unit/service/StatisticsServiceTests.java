package com.roumada.swiftscore.unit.service;

import com.neovisionaries.i18n.CountryCode;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.organization.Competition;
import com.roumada.swiftscore.model.organization.CompetitionRound;
import com.roumada.swiftscore.persistence.datalayer.CompetitionDataLayer;
import com.roumada.swiftscore.persistence.datalayer.FootballMatchDataLayer;
import com.roumada.swiftscore.service.CompetitionService;
import com.roumada.swiftscore.service.StatisticsService;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTests {

    @Mock
    private CompetitionService compService;
    @Mock
    private CompetitionDataLayer competitionDataLayer;
    @Mock
    private FootballMatchDataLayer matchDataLayer;
    @InjectMocks
    private StatisticsService service;

    @Test
    @DisplayName("Generate competition statistics - for a competition with two clubs after two match weeks simulated - " +
            "should return appropriate statistics")
    void generateCompetitionStatistics_forFullySimulatedTwoClubCompetition_shouldReturnSorted() {
        // arrange
        FootballClub fc1 = FootballClubTestUtils.getClub(false);
        fc1.setName("FC1");
        fc1.setId(1L);
        FootballClub fc2 = FootballClubTestUtils.getClub(false);
        fc2.setName("FC2");
        fc2.setId(2L);
        var match1 = new FootballMatch(fc1, fc2);
        match1.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match1.setHomeSideGoalsScored(5);
        match1.setAwaySideGoalsScored(2);
        var match2 = new FootballMatch(fc2, fc1);
        match2.setHomeSideGoalsScored(2);
        match2.setAwaySideGoalsScored(5);
        match2.setMatchResult(FootballMatch.MatchResult.AWAY_SIDE_VICTORY);
        var match3 = new FootballMatch(fc2, fc1);
        match3.setHomeSideGoalsScored(2);
        match3.setAwaySideGoalsScored(2);
        match3.setMatchResult(FootballMatch.MatchResult.DRAW);
        var round1 = CompetitionRound.builder().round(1).matches(List.of(match1)).build();
        var round2 = CompetitionRound.builder().round(1).matches(List.of(match2)).build();
        var round3 = CompetitionRound.builder().round(1).matches(List.of(match3)).build();
        Competition comp = Competition.builder()
                .country(CountryCode.GB)
                .participants(List.of(fc1, fc2))
                .rounds(List.of(round1, round2, round3)).build();
        comp.setId(1L);
        when(competitionDataLayer.findCompetitionById(any())).thenReturn(Optional.of(comp));
        when(matchDataLayer.findAllMatchesForClubInCompetition(1L, 1L, 0, false))
                .thenReturn(List.of(match1, match2, match3));
        when(matchDataLayer.findAllMatchesForClubInCompetition(1L, 2L, 0, false))
                .thenReturn(List.of(match1, match2, match3));

        // act
        var standingsEither = service.getForCompetition(comp.getId(), false);

        // assert
        assertTrue(standingsEither.isRight());

        var standings = standingsEither.get();
        assertFalse(standings.standings().isEmpty());
        assertEquals(2, standings.standings().size());

        var standings1 = standings.standings().get(0);
        var standings2 = standings.standings().get(1);
        assertEquals("FC1", standings1.getFootballClubName());
        assertEquals(2, standings1.getWins());
        assertEquals(1, standings1.getDraws());
        assertEquals(0, standings1.getLosses());
        assertEquals(7, standings1.getPoints());
        assertTrue(standings1.getGoalsScored() > standings1.getGoalsConceded());
        assertEquals("FC2", standings2.getFootballClubName());
        assertEquals(0, standings2.getWins());
        assertEquals(1, standings2.getDraws());
        assertEquals(2, standings2.getLosses());
        assertEquals(1, standings2.getPoints());
        assertTrue(standings2.getGoalsScored() < standings2.getGoalsConceded());
    }
}
