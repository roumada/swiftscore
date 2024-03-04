package com.roumada.swiftscore.competition.manager;

import com.roumada.swiftscore.competition.operator.MatchWeekOperator;
import com.roumada.swiftscore.competition.schedule.MatchWeeksGenerator;
import com.roumada.swiftscore.match.MatchWeekSimulator;
import com.roumada.swiftscore.match.simulators.SimpleMatchSimulator;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchWeekManagerTests {

    private MatchWeekManager matchWeekManager;

    @Test
    @DisplayName("Should correctly simulate a match week with a simple match simulator")
    void shouldCorrectlySimulateAMatchWeekForSimpleMatchSimulator(){
        // arrange
        matchWeekManager = new MatchWeekManager(
                new MatchWeekOperator(MatchWeeksGenerator.generateForRoundRobinLeague(FootballClubTestUtils.generateFootballClubs())),
                MatchWeekSimulator.withMatchSimulator(new SimpleMatchSimulator()));

        // act
        matchWeekManager.simulateMatchWeek();

        // assert
        assertThat(matchWeekManager.getPreviousMatchWeek().matches())
                .filteredOn(match -> !match.getMatchStatus().equals(FootballMatch.Status.UNFINISHED))
                .hasSize(FootballClubTestUtils.generateFootballClubs().size() / 2);
        assertThat(matchWeekManager.getCurrentMatchWeek().matches())
                .filteredOn(match -> match.getMatchStatus().equals(FootballMatch.Status.UNFINISHED))
                .hasSize(FootballClubTestUtils.generateFootballClubs().size() / 2);
    }
}
