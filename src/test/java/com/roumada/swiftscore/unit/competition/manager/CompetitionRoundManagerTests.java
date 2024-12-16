package com.roumada.swiftscore.unit.competition.manager;

import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.data.model.match.FootballMatch;
import com.roumada.swiftscore.logic.competition.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.competition.manager.CompetitionManager;
import com.roumada.swiftscore.logic.competition.schedule.CompetitionRoundsGenerator;
import com.roumada.swiftscore.logic.match.simulators.NoVarianceMatchSimulator;
import com.roumada.swiftscore.util.FootballClubTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompetitionRoundManagerTests {
//
//    @Test
//    @DisplayName("Should correctly simulate a competition round with a simple match simulator")
//    void shouldCorrectlySimulateACompetitionRoundForSimpleMatchSimulator() {
//        // arrange
//        CompetitionManager competitionManager = CompetitionManager.builder()
//                .competitionRoundOperator(new CompetitionRoundOperator(
//                        new Competition(FootballClubTestUtils.generateFootballClubs(),
//                                CompetitionRoundsGenerator.generate(FootballClubTestUtils.generateFootballClubs()),
//                                0.0f)))
//                .competitionRoundSimulator(CompetitionRoundSimulator.withMatchSimulator(new NoVarianceMatchSimulator()))
//                .build();
//
//        // act
//        competitionManager.simulateRound();
//
//        // assert
//        assertThat(competitionManager.getPreviousCompetitionRound().getMatches())
//                .filteredOn(match -> !match.getMatchResult().equals(FootballMatch.Result.UNFINISHED))
//                .hasSize(FootballClubTestUtils.generateFootballClubs().size() / 2);
//        assertThat(competitionManager.getCurrentCompetitionRound().getMatches())
//                .filteredOn(match -> match.getMatchResult().equals(FootballMatch.Result.UNFINISHED))
//                .hasSize(FootballClubTestUtils.generateFootballClubs().size() / 2);
//        List<String> homeSideFCs = competitionManager.getPreviousCompetitionRound().getMatches().stream()
//                .map(match -> match.getHomeSideStatistics().getFootballClub().getName())
//                .toList();
//        List<String> awaySideFCs = competitionManager.getPreviousCompetitionRound().getMatches().stream()
//                .map(match -> match.getAwaySideStatistics().getFootballClub().getName())
//                .toList();
//        assertThat(List.of(homeSideFCs, awaySideFCs)).doesNotHaveDuplicates();
//    }
}
