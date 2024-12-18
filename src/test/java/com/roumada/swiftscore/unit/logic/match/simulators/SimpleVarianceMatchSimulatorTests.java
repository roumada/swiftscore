package com.roumada.swiftscore.unit.logic.match.simulators;

import com.roumada.swiftscore.logic.match.simulators.MatchSimulator;
import com.roumada.swiftscore.logic.match.simulators.SimpleVarianceMatchSimulator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.roumada.swiftscore.model.match.FootballMatch.Result.DRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SimpleVarianceMatchSimulatorTests {

    @ParameterizedTest
    @CsvSource({
            "0.5, 0.5, DRAW",
            "0.5, 0.6, AWAY_SIDE_VICTORY",
            "0.6, 0.5, HOME_SIDE_VICTORY"
    })
    @DisplayName("Simulate match - with zero variance - should end with expected results")
    void simulateMatch_zeroVariance_shouldEndWithExpectedResults(double homeVictoryChance, double awayVictoryChance, FootballMatch.Result result) {
        // arrange
        final MatchSimulator matchSimulator = SimpleVarianceMatchSimulator.withVariance(0.0);
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(homeVictoryChance).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(awayVictoryChance).build();

        FootballMatch footballMatch = new FootballMatch(new FootballMatchStatistics(footballClub1),
                new FootballMatchStatistics(footballClub2));

        // act
        matchSimulator.simulateMatch(footballMatch);

        // assert
        assertEquals(result, footballMatch.getMatchResult());
    }

    @Test
    @DisplayName("Simulate match - with non-zero variance - should not end with a draw for identical victory chances")
    void simulateMatch_nonZeroVariance_shouldNotEndWithADraw() {
        // arrange
        final MatchSimulator matchSimulator = SimpleVarianceMatchSimulator.withVariance(0.5f);
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(0.5).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(0.5).build();

        FootballMatch footballMatch = new FootballMatch(new FootballMatchStatistics(footballClub1),
                new FootballMatchStatistics(footballClub2));

        // act
        matchSimulator.simulateMatch(footballMatch);

        // assert
        assertNotEquals(DRAW, footballMatch.getMatchResult());
    }
}
