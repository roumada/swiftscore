package com.roumada.swiftscore.unit.logic.match.resolver.score;


import com.roumada.swiftscore.logic.match.resolver.ScoreResolverFactory;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResolverFactoryTests {

    @Test
    @DisplayName("Resolve score - home side victory - should resolve in favor of home side")
    void resolveScore_homeSideVictory_shouldResolveInFavorOfHomeSide() {
        // arrange
        FootballMatch fm = new FootballMatch(
                FootballClub.builder().name("FC1").victoryChance(1).build(),
                FootballClub.builder().name("FC2").victoryChance(0.5).build());
        fm.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);

        // act
        ScoreResolverFactory.getFor(fm.getMatchResult()).resolve(fm);

        // assert
        assertTrue(fm.getHomeSideGoalsScored() > fm.getAwaySideGoalsScored());
    }

    @Test
    @DisplayName("Resolve score - away side victory - should resolve in favor of away side")
    void resolveScore_awaySideVictory_shouldResolveInFavorOfAwaySide() {
        // arrange
        FootballMatch fm = new FootballMatch(
                FootballClub.builder().name("FC1").victoryChance(0.4).build(),
                FootballClub.builder().name("FC2").victoryChance(0.5).build());
        fm.setMatchResult(FootballMatch.MatchResult.AWAY_SIDE_VICTORY);

        // act
        ScoreResolverFactory.getFor(fm.getMatchResult()).resolve(fm);

        // assert
        assertTrue(fm.getHomeSideGoalsScored() < fm.getAwaySideGoalsScored());
    }

    @Test
    @DisplayName("Resolve score - draw - should resolve as a draw")
    void resolveScore_draw_shouldResolveAsADraw() {
        // arrange
        FootballMatch fm = new FootballMatch(
                FootballClub.builder().name("FC1").victoryChance(0.5).build(),
                FootballClub.builder().name("FC2").victoryChance(0.5).build());
        fm.setMatchResult(FootballMatch.MatchResult.DRAW);

        // act
        ScoreResolverFactory.getFor(fm.getMatchResult()).resolve(fm);

        // assert
        assertEquals(fm.getHomeSideGoalsScored(), fm.getAwaySideGoalsScored());
    }

    @Test
    @DisplayName("Resolve score - unresolved - should throw an exception")
    void resolveScore_unresolved_shouldThrowAnException() {
        // arrange
        FootballMatch fm = new FootballMatch(
                FootballClub.builder().name("FC1").victoryChance(0.5).build(),
                FootballClub.builder().name("FC2").victoryChance(0.5).build());
        fm.setMatchResult(FootballMatch.MatchResult.UNFINISHED);

        // act
        FootballMatch.MatchResult mr = fm.getMatchResult();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ScoreResolverFactory.getFor(mr);
        });

        // assert
        assertEquals("A ScoreResolver cannot be assigned to a FootballMatch with a non-match finished status.", exception.getMessage());
    }
}
