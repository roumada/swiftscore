package com.roumada.swiftscore.integration.persistence;

import com.roumada.swiftscore.integration.AbstractBaseIntegrationTest;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.persistence.FootballMatchDataLayer;
import com.roumada.swiftscore.persistence.repository.FootballClubRepository;
import com.roumada.swiftscore.persistence.repository.FootballMatchRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FootballMatchDataLayerIntegrationTests extends AbstractBaseIntegrationTest {

    @Autowired
    private FootballClubRepository fcr;
    @Autowired
    private FootballMatchRepository fmr;

    @Autowired
    private FootballMatchDataLayer dataLayer;


    @Test
    @DisplayName("Save match - should save")
    void saveMatch_shouldSave() {
        // arrange
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fcr.save(fc1);
        fcr.save(fc2);
        var match = new FootballMatch(fc1, fc2);

        // act
        var saved = dataLayer.save(match);

        // assert
        var optionalMatch = dataLayer.findMatchById(saved.getId());
        assertThat(optionalMatch).isPresent();
        var retrievedMatch = optionalMatch.get();
        assertThat(retrievedMatch.getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Find matches for club in competition - include unresolved - should find all types of matches")
    void findAllMatchesForClubInCompetition_includeUnresolved_findAllTypes() {
        // arrange
        var competitionId = 1L;
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fcr.save(fc1);
        fcr.save(fc2);
        var match = new FootballMatch(fc1, fc2);
        match.setMatchResult(FootballMatch.MatchResult.UNFINISHED);
        match.setCompetitionId(competitionId);
        var match2 = new FootballMatch(fc1, fc2);
        match2.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match2.setCompetitionId(competitionId);
        var match3 = new FootballMatch(fc1, fc2);
        match3.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match3.setCompetitionId(2L);
        fmr.saveAll(List.of(match, match2, match3));

        // act
        var matches = dataLayer.findAllMatchesForClubInCompetition(competitionId, fc1.getId(), 0, true);

        // assert
        assertEquals(2, matches.size());
    }

    @Test
    @DisplayName("Find matches for club in competition - exclude unresolved - should find only resolved matches")
    void findAllMatchesForClubInCompetition_excludeUnresolved_findResolvedMatchesOnly() {
        // arrange
        var competitionId = 1L;
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fcr.save(fc1);
        fcr.save(fc2);
        var match = new FootballMatch(fc1, fc2);
        match.setMatchResult(FootballMatch.MatchResult.UNFINISHED);
        match.setCompetitionId(competitionId);
        var match2 = new FootballMatch(fc1, fc2);
        match2.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match2.setCompetitionId(competitionId);
        var match3 = new FootballMatch(fc1, fc2);
        match3.setMatchResult(FootballMatch.MatchResult.AWAY_SIDE_VICTORY);
        match3.setCompetitionId(competitionId);
        var match4 = new FootballMatch(fc1, fc2);
        match4.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match4.setCompetitionId(2L);
        fmr.saveAll(List.of(match, match2, match3, match4));

        // act
        var matches = dataLayer.findAllMatchesForClubInCompetition(competitionId, fc1.getId(), 0, false);

        // assert
        assertThat(matches).hasSize(2);
        assertTrue(matches.stream().noneMatch(m -> m.getMatchResult().equals(FootballMatch.MatchResult.UNFINISHED)));
    }

    @Test
    @DisplayName("Find matches for club - include unresolved - should find all types of matches")
    void findAllMatchesForClub_includeUnresolved_findAllTypes() {
        // arrange
        var competitionId = 1L;
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fcr.save(fc1);
        fcr.save(fc2);
        var match = new FootballMatch(fc1, fc2);
        match.setMatchResult(FootballMatch.MatchResult.UNFINISHED);
        match.setCompetitionId(competitionId);
        var match2 = new FootballMatch(fc1, fc2);
        match2.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match2.setCompetitionId(competitionId);
        var match3 = new FootballMatch(fc1, fc2);
        match3.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match3.setCompetitionId(2L);
        fmr.saveAll(List.of(match, match2, match3));

        // act
        var matches = dataLayer.findAllMatchesForClub(fc1.getId(), 0, 5,true);

        // assert
        assertThat(matches).hasSize(3);
    }

    @Test
    @DisplayName("Find matches for club - exclude unresolved - should find all types of matches")
    void findAllMatchesForClub_excludeUnresolved_findAllResolved() {
        // arrange
        var competitionId = 1L;
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fcr.save(fc1);
        fcr.save(fc2);
        var match = new FootballMatch(fc1, fc2);
        match.setMatchResult(FootballMatch.MatchResult.UNFINISHED);
        match.setCompetitionId(competitionId);
        var match2 = new FootballMatch(fc1, fc2);
        match2.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match2.setCompetitionId(competitionId);
        var match3 = new FootballMatch(fc1, fc2);
        match3.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match3.setCompetitionId(2L);
        fmr.saveAll(List.of(match, match2, match3));

        // act
        var matches = dataLayer.findAllMatchesForClub(fc1.getId(), 0, 5,false);

        // assert
        assertThat(matches).hasSize(2);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 7, 7",
            "1, 4, 4",
            "2, 4, 2",
            "3, 4, 0",
            "0, 11, 10",
    })
    @DisplayName("Find matches for club - various page numbers and sizes, include unresolved - should find adequate amount")
    void findAllMatchesForClub_variousSizes_findAdequateAmount(int number, int size, int expectedAmount) {
        // arrange
        var competitionId = 1L;
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fcr.save(fc1);
        fcr.save(fc2);
        var match = new FootballMatch(fc1, fc2);
        match.setMatchResult(FootballMatch.MatchResult.UNFINISHED);
        match.setCompetitionId(competitionId);
        var matchCopies = new ArrayList<>(Collections.nCopies(10, match));
        var deepCopies = CollectionUtils.collect(matchCopies, x-> new FootballMatch(fc1, fc2));
        fmr.saveAll(deepCopies);

        // act
        var result = dataLayer.findAllMatchesForClub(fc1.getId(), number, size,true);

        // assert
        assertThat(result).hasSize(expectedAmount);
    }

    @Test
    @DisplayName("Delete matches by competition ID - should delete")
    void deleteMatchesByCompetitionID_shouldDelete() {
        // arrange
        var competitionId = 1L;
        var fc1 = FootballClub.builder().name("FC1").victoryChance(0.3f).build();
        var fc2 = FootballClub.builder().name("FC2").victoryChance(0.3f).build();
        fcr.save(fc1);
        fcr.save(fc2);
        var match = new FootballMatch(fc1, fc2);
        match.setMatchResult(FootballMatch.MatchResult.UNFINISHED);
        match.setCompetitionId(competitionId);
        var match2 = new FootballMatch(fc1, fc2);
        match2.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match2.setCompetitionId(competitionId);
        var match3 = new FootballMatch(fc1, fc2);
        match3.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        match3.setCompetitionId(2L);
        var savedMatchId = fmr.save(match).getId();
        var savedMatch2Id = fmr.save(match2).getId();
        var savedMatch3Id = fmr.save(match3).getId();

        // act
        dataLayer.deleteByCompetitionId(competitionId);

        // assert
        assertThat(dataLayer.findMatchById(savedMatchId)).isEmpty();
        assertThat(dataLayer.findMatchById(savedMatch2Id)).isEmpty();
        assertThat(dataLayer.findMatchById(savedMatch3Id)).isPresent();
    }
}
