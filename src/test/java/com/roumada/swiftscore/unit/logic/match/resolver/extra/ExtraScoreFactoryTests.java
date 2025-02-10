package com.roumada.swiftscore.unit.logic.match.resolver.extra;

import com.roumada.swiftscore.logic.match.resolver.extra.ExtraScoreResolverFactory;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtraScoreFactoryTests {

    @Test
    @DisplayName("Resolve extra score - home side victory - should add extra goals to victor")
    void resolveExtraScore_homeSideVictory_shouldAddExtraGoalsToVictor() {
        // arrange
        FootballMatch fm = new FootballMatch(
                FootballClub.builder().name("FC1").victoryChance(1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.5).build());
        fm.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);

        // act
        var getResult = ExtraScoreResolverFactory.getFor(fm.getMatchResult());

        // assert
        assertTrue(getResult.isPresent());
        var resolver = getResult.get();

        // act
        resolver.resolve(fm);

        // assert
        assertTrue(fm.getExtraVictorGoals() > 0);
    }

    @Test
    @DisplayName("Resolve extras score - away side victory - should add extra goals to victor")
    void resolveExtraScore_awaySideVictory_shouldAddExtraGoalsToVictor() {
        // arrange
        FootballMatch fm = new FootballMatch(
                FootballClub.builder().name("FC1").victoryChance(1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.5).build());
        fm.setMatchResult(FootballMatch.MatchResult.AWAY_SIDE_VICTORY);

        // act
        var getResult = ExtraScoreResolverFactory.getFor(fm.getMatchResult());

        // assert
        assertTrue(getResult.isPresent());
        var resolver = getResult.get();

        // act
        resolver.resolve(fm);

        // assert
        assertTrue(fm.getExtraVictorGoals() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"DRAW", "UNFINISHED"})
    @DisplayName("Resolve extra score - remaining results - should not find a resolver")
    void resolveExtraScore_draw_shouldNotFindAResolver(FootballMatch.MatchResult result) {
        // arrange
        FootballMatch fm = new FootballMatch(
                FootballClub.builder().name("FC1").victoryChance(1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.5).build());
        fm.setMatchResult(result);

        // act
        var getResult = ExtraScoreResolverFactory.getFor(fm.getMatchResult());

        // assert
        assertTrue(getResult.isEmpty());
    }
}
