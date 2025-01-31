package com.roumada.swiftscore.logic.match.resolver;

import com.roumada.swiftscore.model.match.FootballMatch;

public class ScoreResolverFactory {

    private ScoreResolverFactory() {
    }

    public static Resolver getFor(FootballMatch.MatchResult matchResult) {
        switch (matchResult) {
            case HOME_SIDE_VICTORY -> {
                return new HomeSideVictorResolver();
            }
            case AWAY_SIDE_VICTORY -> {
                return new AwaySideVictorResolver();
            }
            case DRAW -> {
                return new DrawResolver();
            }
            default ->
                    throw new IllegalArgumentException("A ScoreResolver cannot be assigned to a FootballMatch with a non-match finished status.");
        }
    }
}
