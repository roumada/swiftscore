package com.roumada.swiftscore.match;

import com.roumada.swiftscore.match.simulators.SimpleMatchSimulator;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballClubMatchStatistics;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.MatchWeek;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.roumada.swiftscore.model.match.FootballMatch.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchWeekExecutorTests {

    private final MatchWeekSimulator matchWeekSimulator = MatchWeekSimulator.withMatchSimulator(new SimpleMatchSimulator());

    @Test
    @DisplayName("Should simulate an entire match week and change match statuses")
    void shouldSimulateEntireMatchWeek() {
        // arrange
        FootballClub footballClub1 = FootballClub.builder().name("Football club 1").victoryChance(0.5f).build();
        FootballClub footballClub2 = FootballClub.builder().name("Football club 2").victoryChance(0.2f).build();
        MatchWeek matchWeek = prepareMatchWeek(footballClub1, footballClub2);

        // act
        matchWeekSimulator.simulate(matchWeek);

        // assert
        assertEquals(HOME_SIDE_VICTORY, matchWeek.matches().get(0).getMatchStatus());
        assertTrue(matchWeek.matches().get(0).getHomeSideStatistics().getGoalsScored() >
                matchWeek.matches().get(0).getAwaySideStatistics().getGoalsScored());
        assertEquals(AWAY_SIDE_VICTORY, matchWeek.matches().get(1).getMatchStatus());
        assertTrue(matchWeek.matches().get(1).getHomeSideStatistics().getGoalsScored() <
                matchWeek.matches().get(1).getAwaySideStatistics().getGoalsScored());
        assertEquals(DRAW, matchWeek.matches().get(2).getMatchStatus());
        assertEquals(matchWeek.matches().get(2).getHomeSideStatistics().getGoalsScored(),
                matchWeek.matches().get(2).getAwaySideStatistics().getGoalsScored());
    }

    private static MatchWeek prepareMatchWeek(FootballClub footballClub1, FootballClub footballClub2) {
        FootballMatch footballMatch1 = new FootballMatch(new FootballClubMatchStatistics(footballClub1),
                new FootballClubMatchStatistics(footballClub2));
        FootballMatch footballMatch2 = new FootballMatch(new FootballClubMatchStatistics(footballClub2),
                new FootballClubMatchStatistics(footballClub1));
        FootballMatch footballMatch3 = new FootballMatch(new FootballClubMatchStatistics(footballClub2),
                new FootballClubMatchStatistics(footballClub2));

        return new MatchWeek(1, List.of(footballMatch1, footballMatch2, footballMatch3));
    }
}
