package com.roumada.swiftscore.unit.logic.competition;

import com.roumada.swiftscore.logic.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.match.simulator.SimpleVarianceMatchSimulator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.SimulationValues;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.roumada.swiftscore.model.match.FootballMatch.MatchResult.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitionRoundSimulatorTests {

    private final CompetitionRoundSimulator competitionRoundSimulator =
            CompetitionRoundSimulator.withMatchSimulator(SimpleVarianceMatchSimulator.withValues(new SimulationValues(0.0)));

    @Test
    @DisplayName("Should simulate an entire match week and change match statuses")
    void shouldSimulateEntireMatchWeek() {
        // arrange
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(0.5f).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(0.2f).build();
        CompetitionRound competitionRound = prepareRound(footballClub1, footballClub2);

        // act
        competitionRoundSimulator.simulate(competitionRound);

        // assert
        assertEquals(HOME_SIDE_VICTORY, competitionRound.getMatches().get(0).getMatchResult());
        assertTrue(competitionRound.getMatches().get(0).getHomeSideGoalsScored() >
                competitionRound.getMatches().get(0).getAwaySideGoalsScored());
        assertEquals(AWAY_SIDE_VICTORY, competitionRound.getMatches().get(1).getMatchResult());
        assertTrue(competitionRound.getMatches().get(1).getHomeSideGoalsScored() <
                competitionRound.getMatches().get(1).getAwaySideGoalsScored());
        assertEquals(DRAW, competitionRound.getMatches().get(2).getMatchResult());
        assertEquals(competitionRound.getMatches().get(2).getHomeSideGoalsScored(),
                competitionRound.getMatches().get(2).getAwaySideGoalsScored());
    }

    private static CompetitionRound prepareRound(FootballClub footballClub1, FootballClub footballClub2) {
        FootballMatch footballMatch1 = new FootballMatch(footballClub1, footballClub2);
        FootballMatch footballMatch2 = new FootballMatch(footballClub2, footballClub1);
        FootballMatch footballMatch3 = new FootballMatch(footballClub2, footballClub2);

        return new CompetitionRound(null, 1, List.of(footballMatch1, footballMatch2, footballMatch3));
    }
}
