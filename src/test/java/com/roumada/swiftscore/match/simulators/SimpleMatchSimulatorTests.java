package com.roumada.swiftscore.match.simulators;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballClubMatchStatistics;
import com.roumada.swiftscore.model.match.FootballMatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.roumada.swiftscore.model.match.FootballMatch.Status.*;
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

        FootballMatch footballMatch = new FootballMatch(new FootballClubMatchStatistics(footballClub1),
                new FootballClubMatchStatistics(footballClub2));

        // act
        matchSimulator.simulateMatch(footballMatch);

        // assert
        assertEquals(HOME_SIDE_VICTORY, footballMatch.getMatchStatus());
        assertTrue(footballMatch.getHomeSideStatistics().getGoalsScored() >
                footballMatch.getAwaySideStatistics().getGoalsScored());
    }

    @Test
    @DisplayName("Should end with victory for away team")
    void simulateMatch_shouldGrantVictoryToAwayTeam() {
        // arrange
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(0.2f).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(0.5f).build();

        FootballMatch footballMatch = new FootballMatch(new FootballClubMatchStatistics(footballClub1),
                new FootballClubMatchStatistics(footballClub2));

        // act
        matchSimulator.simulateMatch(footballMatch);

        // assert
        assertEquals(AWAY_SIDE_VICTORY, footballMatch.getMatchStatus());
        assertTrue(footballMatch.getHomeSideStatistics().getGoalsScored() <
                footballMatch.getAwaySideStatistics().getGoalsScored());
    }

    @Test
    @DisplayName("Should end with a draw for equal victory chances")
    void simulateMatch_shouldResultInDraw() {
        // arrange
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(0.5f).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(0.5f).build();

        FootballMatch footballMatch = new FootballMatch(new FootballClubMatchStatistics(footballClub1),
                new FootballClubMatchStatistics(footballClub2));

        // act
        matchSimulator.simulateMatch(footballMatch);

        // assert
        assertEquals(DRAW, footballMatch.getMatchStatus());
        assertEquals(footballMatch.getHomeSideStatistics().getGoalsScored(),
                footballMatch.getAwaySideStatistics().getGoalsScored());
    }
}
