package com.roumada.swiftscore.unit.match;

import com.roumada.swiftscore.logic.competition.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.match.simulators.NoVarianceMatchSimulator;
import com.roumada.swiftscore.data.model.FootballClub;
import com.roumada.swiftscore.data.model.match.CompetitionRound;
import com.roumada.swiftscore.data.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.data.model.match.FootballMatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.roumada.swiftscore.data.model.match.FootballMatch.Result.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitionRoundExecutorTests {

    private final CompetitionRoundSimulator competitionRoundSimulator = CompetitionRoundSimulator.withMatchSimulator(new NoVarianceMatchSimulator());

    @Test
    @DisplayName("Should simulate an entire match week and change match statuses")
    void shouldSimulateEntireMatchWeek() {
        // arrange
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(0.5f).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(0.2f).build();
        CompetitionRound competitionRound = prepareMatchWeek(footballClub1, footballClub2);

        // act
        competitionRoundSimulator.simulate(competitionRound);

        // assert
        assertEquals(HOME_SIDE_VICTORY, competitionRound.getMatches().get(0).getMatchResult());
        assertTrue(competitionRound.getMatches().get(0).getHomeSideStatistics().getGoalsScored() >
                competitionRound.getMatches().get(0).getAwaySideStatistics().getGoalsScored());
        assertEquals(AWAY_SIDE_VICTORY, competitionRound.getMatches().get(1).getMatchResult());
        assertTrue(competitionRound.getMatches().get(1).getHomeSideStatistics().getGoalsScored() <
                competitionRound.getMatches().get(1).getAwaySideStatistics().getGoalsScored());
        assertEquals(DRAW, competitionRound.getMatches().get(2).getMatchResult());
        assertEquals(competitionRound.getMatches().get(2).getHomeSideStatistics().getGoalsScored(),
                competitionRound.getMatches().get(2).getAwaySideStatistics().getGoalsScored());
    }

    private static CompetitionRound prepareMatchWeek(FootballClub footballClub1, FootballClub footballClub2) {
        FootballMatch footballMatch1 = new FootballMatch(new FootballMatchStatistics(footballClub1),
                new FootballMatchStatistics(footballClub2));
        FootballMatch footballMatch2 = new FootballMatch(new FootballMatchStatistics(footballClub2),
                new FootballMatchStatistics(footballClub1));
        FootballMatch footballMatch3 = new FootballMatch(new FootballMatchStatistics(footballClub2),
                new FootballMatchStatistics(footballClub2));

        return new CompetitionRound(null, 1, List.of(footballMatch1, footballMatch2, footballMatch3));
    }
}
