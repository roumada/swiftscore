package com.roumada.swiftscore.logic.match.resolvers.score;

import com.roumada.swiftscore.model.match.FootballMatch;

public class ScoreResolverFactory {

    private ScoreResolverFactory(){}
    public static ScoreResolver getFor(FootballMatch.MatchResult matchResult) {
        switch (matchResult) {
            case HOME_SIDE_VICTORY -> {
                return new HomeSideVictorScoreResolver();
            }
            case AWAY_SIDE_VICTORY -> {
                return new AwaySideVictorScoreResolver();
            }
            case DRAW -> {
                return new DrawScoreResolver();
            }
            default -> throw new IllegalArgumentException("A ScoreResolver cannot be assigned to a FootballMatch with a non-match finished status.");
        }
    }
}
