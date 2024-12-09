package com.roumada.swiftscore.unit.match.simulators;

import com.roumada.swiftscore.match.simulators.MatchSimulator;
import com.roumada.swiftscore.match.simulators.SimpleMatchSimulator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.model.match.FootballMatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.roumada.swiftscore.model.match.FootballMatch.Result.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleMatchSimulatorTests {

    private final MatchSimulator matchSimulator = new SimpleMatchSimulator();
    @Test
    @DisplayName("Should end with victory for home team")
    void simulateMatch_shouldGrantVictoryToHomeTeam() {
        // arrange
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(0.5f).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(0.2f).build();

        FootballMatch footballMatch = new FootballMatch(new FootballMatchStatistics(footballClub1),
                new FootballMatchStatistics(footballClub2));

        // act
        matchSimulator.simulateMatch(footballMatch);

        // assert
        assertEquals(HOME_SIDE_VICTORY, footballMatch.getMatchResult());
        assertTrue(footballMatch.getHomeSideStatistics().getGoalsScored() >
                footballMatch.getAwaySideStatistics().getGoalsScored());
    }

    @Test
    @DisplayName("Should end with victory for away team")
    void simulateMatch_shouldGrantVictoryToAwayTeam() {
        // arrange
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(0.2f).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(0.5f).build();

        FootballMatch footballMatch = new FootballMatch(new FootballMatchStatistics(footballClub1),
                new FootballMatchStatistics(footballClub2));

        // act
        matchSimulator.simulateMatch(footballMatch);

        // assert
        assertEquals(AWAY_SIDE_VICTORY, footballMatch.getMatchResult());
        assertTrue(footballMatch.getHomeSideStatistics().getGoalsScored() <
                footballMatch.getAwaySideStatistics().getGoalsScored());
    }

    @Test
    @DisplayName("Should end with a draw for equal victory chances")
    void simulateMatch_shouldResultInDraw() {
        // arrange
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(0.5f).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(0.5f).build();

        FootballMatch footballMatch = new FootballMatch(new FootballMatchStatistics(footballClub1),
                new FootballMatchStatistics(footballClub2));

        // act
        matchSimulator.simulateMatch(footballMatch);

        // assert
        assertEquals(DRAW, footballMatch.getMatchResult());
        assertEquals(footballMatch.getHomeSideStatistics().getGoalsScored(),
                footballMatch.getAwaySideStatistics().getGoalsScored());
    }
}
