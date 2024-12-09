package com.roumada.swiftscore.unit.competition.manager;

import com.roumada.swiftscore.competition.manager.CompetitionRoundManager;
import com.roumada.swiftscore.competition.operator.CompetitionRoundOperator;
import com.roumada.swiftscore.competition.schedule.CompetitionRoundsGenerator;
import com.roumada.swiftscore.competition.CompetitionRoundSimulator;
import com.roumada.swiftscore.match.simulators.SimpleMatchSimulator;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompetitionRoundManagerTests {

    @Test
    @DisplayName("Should correctly simulate a competition round with a simple match simulator")
    void shouldCorrectlySimulateACompetitionRoundForSimpleMatchSimulator() {
        // arrange
        CompetitionRoundManager competitionRoundManager = CompetitionRoundManager.builder()
                .competitionRoundOperator(new CompetitionRoundOperator(
                        CompetitionRoundsGenerator.generate(FootballClubTestUtils.generateFootballClubs())))
                .competitionRoundSimulator(CompetitionRoundSimulator.withMatchSimulator(new SimpleMatchSimulator()))
                .build();

        // act
        competitionRoundManager.simulateRound();

        // assert
        assertThat(competitionRoundManager.getPreviousCompetitionRound().getMatches())
                .filteredOn(match -> !match.getMatchResult().equals(FootballMatch.Result.UNFINISHED))
                .hasSize(FootballClubTestUtils.generateFootballClubs().size() / 2);
        assertThat(competitionRoundManager.getCurrentCompetitionRound().getMatches())
                .filteredOn(match -> match.getMatchResult().equals(FootballMatch.Result.UNFINISHED))
                .hasSize(FootballClubTestUtils.generateFootballClubs().size() / 2);
        List<String> homeSideFCs = competitionRoundManager.getPreviousCompetitionRound().getMatches().stream()
                .map(match -> match.getHomeSideStatistics().getFootballClub().getName())
                .toList();
        List<String> awaySideFCs = competitionRoundManager.getPreviousCompetitionRound().getMatches().stream()
                .map(match -> match.getAwaySideStatistics().getFootballClub().getName())
                .toList();
        assertThat(List.of(homeSideFCs, awaySideFCs)).doesNotHaveDuplicates();
    }
}
