package com.roumada.swiftscore.unit.logic.resolver.victor;


import com.roumada.swiftscore.logic.match.resolvers.victor.VictorResolverFactory;
import com.roumada.swiftscore.model.FootballClub;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VictorResolverFactoryTests {

    @Test
    @DisplayName("Resolve victor - home side victory - should resolve in favor of home side")
    void resolveVictor_homeSideVictory_shouldResolveInFavorOfHomeSide() {
        // arrange
        FootballMatch fm = new FootballMatch(
                new FootballClub("FC1", 1),
                new FootballClub("FC2", 0.5));
        fm.setHomeSideStatistics(new FootballMatchStatistics(1L));
        fm.setAwaySideStatistics(new FootballMatchStatistics(2L));
        fm.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);

        // act
        VictorResolverFactory.getFor(fm.getMatchResult()).resolve(fm);

        // assert
        assertEquals(FootballMatchStatistics.MatchStatisticsResult.VICTORY, fm.getHomeSideStatistics().getResult());
        assertEquals(FootballMatchStatistics.MatchStatisticsResult.LOSS, fm.getAwaySideStatistics().getResult());
    }

    @Test
    @DisplayName("Resolve victor - away side victory - should resolve in favor of away side")
    void resolveVictor_awaySideVictory_shouldResolveInFavorOfAwaySide() {
        // arrange
        FootballMatch fm = new FootballMatch(
                new FootballClub("FC1", 0.4),
                new FootballClub("FC2", 0.5));
        fm.setHomeSideStatistics(new FootballMatchStatistics(1L));
        fm.setAwaySideStatistics(new FootballMatchStatistics(2L));
        fm.setMatchResult(FootballMatch.MatchResult.AWAY_SIDE_VICTORY);

        // act
        VictorResolverFactory.getFor(fm.getMatchResult()).resolve(fm);

        // assert
        assertEquals(FootballMatchStatistics.MatchStatisticsResult.LOSS, fm.getHomeSideStatistics().getResult());
        assertEquals(FootballMatchStatistics.MatchStatisticsResult.VICTORY, fm.getAwaySideStatistics().getResult());
    }

    @Test
    @DisplayName("Resolve victor - draw - should resolve as a draw")
    void resolveVictor_draw_shouldResolveAsADraw() {
        // arrange
        FootballMatch fm = new FootballMatch(
                new FootballClub("FC1", 0.5),
                new FootballClub("FC2", 0.5));
        fm.setHomeSideStatistics(new FootballMatchStatistics(1L));
        fm.setAwaySideStatistics(new FootballMatchStatistics(2L));
        fm.setMatchResult(FootballMatch.MatchResult.DRAW);

        // act
        VictorResolverFactory.getFor(fm.getMatchResult()).resolve(fm);

        // assert
        assertEquals(FootballMatchStatistics.MatchStatisticsResult.DRAW, fm.getHomeSideStatistics().getResult());
        assertEquals(FootballMatchStatistics.MatchStatisticsResult.DRAW, fm.getAwaySideStatistics().getResult());
    }

    @Test
    @DisplayName("Resolve victor - unresolved - should throw an exception")
    void resolveVictor_unresolved_shouldThrowAnException() {
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
            VictorResolverFactory.getFor(mr);
        });

        // assert
        assertEquals("A VictorResolver cannot be assigned to a FootballMatch with a non-match finished status.", exception.getMessage());
    }
}
