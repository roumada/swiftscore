package com.roumada.swiftscore.unit.logic.match.simulators;

import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import com.roumada.swiftscore.logic.match.simulators.MatchSimulator;
import com.roumada.swiftscore.logic.match.simulators.SimpleVarianceMatchSimulator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.roumada.swiftscore.model.match.FootballMatch.Result.DRAW;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SimpleVarianceMatchSimulatorTests {

    private final MatchSimulator matchSimulator = SimpleVarianceMatchSimulator.withVariance(0.5f);

    @Test
    @DisplayName("Should not end with a draw")
    void simulateMatch_shouldNotEndWithADraw() {
        // arrange
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(0.5f).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(0.5f).build();

        FootballMatch footballMatch = new FootballMatch(new FootballMatchStatistics(footballClub1),
                new FootballMatchStatistics(footballClub2));

        // act
        matchSimulator.simulateMatch(footballMatch);

        // assert
        assertNotEquals(DRAW, footballMatch.getMatchResult());
    }
}
