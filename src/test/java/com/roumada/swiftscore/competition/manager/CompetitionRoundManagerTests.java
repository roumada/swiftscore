package com.roumada.swiftscore.competition.manager;

import com.roumada.swiftscore.competition.operator.CompetitionRoundOperator;
import com.roumada.swiftscore.competition.schedule.CompetitionRoundsGenerator;
import com.roumada.swiftscore.match.CompetitionRoundSimulator;
import com.roumada.swiftscore.match.simulators.SimpleMatchSimulator;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompetitionRoundManagerTests {

    @Test
    @DisplayName("Should correctly simulate a match week with a simple match simulator")
    void shouldCorrectlySimulateAMatchWeekForSimpleMatchSimulator(){
        // arrange
        CompetitionRoundManager competitionRoundManager = new CompetitionRoundManager(
                new CompetitionRoundOperator(CompetitionRoundsGenerator.generateForLeague(FootballClubTestUtils.generateFootballClubs())),
                CompetitionRoundSimulator.withMatchSimulator(new SimpleMatchSimulator()));

        // act
        competitionRoundManager.simulateRound();

        // assert
        assertThat(competitionRoundManager.getPreviousCompetitionRound().matches())
                .filteredOn(match -> !match.getMatchStatus().equals(FootballMatch.Status.UNFINISHED))
                .hasSize(FootballClubTestUtils.generateFootballClubs().size() / 2);
        assertThat(competitionRoundManager.getCurrentCompetitionRound().matches())
                .filteredOn(match -> match.getMatchStatus().equals(FootballMatch.Status.UNFINISHED))
                .hasSize(FootballClubTestUtils.generateFootballClubs().size() / 2);
    }
}
