package com.roumada.swiftscore.unit.logic.resolver.score;


import com.roumada.swiftscore.logic.match.resolvers.score.ScoreResolverFactory;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreResolverFactoryTests {

    @Test
    @DisplayName("Resolve score - home side victory - should resolve in favor of home side")
    void resolveScore_homeSideVictory_shouldResolveInFavorOfHomeSide() {
        // arrange
        FootballMatch fm = new FootballMatch(
                new FootballClub("FC1", 1),
                new FootballClub("FC2", 0.5));
        fm.setHomeSideStatistics(new FootballMatchStatistics(1L));
        fm.setAwaySideStatistics(new FootballMatchStatistics(2L));
        fm.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);

        // act
        ScoreResolverFactory.getFor(fm.getMatchResult()).resolve(fm);

        // assert
        assertTrue(fm.getHomeSideStatistics().getGoalsScored() > fm.getAwaySideStatistics().getGoalsScored());
    }

    @Test
    @DisplayName("Resolve score - away side victory - should resolve in favor of away side")
    void resolveScore_awaySideVictory_shouldResolveInFavorOfAwaySide() {
        // arrange
        FootballMatch fm = new FootballMatch(
                new FootballClub("FC1", 0.4),
                new FootballClub("FC2", 0.5));
        fm.setHomeSideStatistics(new FootballMatchStatistics(1L));
        fm.setAwaySideStatistics(new FootballMatchStatistics(2L));
        fm.setMatchResult(FootballMatch.MatchResult.AWAY_SIDE_VICTORY);

        // act
        ScoreResolverFactory.getFor(fm.getMatchResult()).resolve(fm);

        // assert
        assertTrue(fm.getHomeSideStatistics().getGoalsScored() < fm.getAwaySideStatistics().getGoalsScored());
    }

    @Test
    @DisplayName("Resolve score - draw - should resolve as a draw")
    void resolveScore_draw_shouldResolveAsADraw() {
        // arrange
        FootballMatch fm = new FootballMatch(
                new FootballClub("FC1", 0.5),
                new FootballClub("FC2", 0.5));
        fm.setHomeSideStatistics(new FootballMatchStatistics(1L));
        fm.setAwaySideStatistics(new FootballMatchStatistics(2L));
        fm.setMatchResult(FootballMatch.MatchResult.DRAW);

        // act
        ScoreResolverFactory.getFor(fm.getMatchResult()).resolve(fm);

        // assert
        assertEquals(fm.getHomeSideStatistics().getGoalsScored(), fm.getAwaySideStatistics().getGoalsScored());
    }

    @Test
    @DisplayName("Resolve score - unresolved - should throw an exception")
    void resolveScore_unresolved_shouldThrowAnException() {
        // arrange
        FootballMatch fm = new FootballMatch(
                new FootballClub("FC1", 0.5),
                new FootballClub("FC2", 0.5));
        fm.setHomeSideStatistics(new FootballMatchStatistics(1L));
        fm.setAwaySideStatistics(new FootballMatchStatistics(2L));
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
