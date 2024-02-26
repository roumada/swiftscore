package com.roumada.swiftscore.match.resolvers;

import com.roumada.swiftscore.model.match.FootballMatch;

public class MatchResolverFactory {

    private MatchResolverFactory(){}
    public static ScoreResolver getFor(FootballMatch footballMatch) {
        switch (footballMatch.getMatchStatus()) {
            case HOME_SIDE_VICTORY -> {
                return new HomeSideVictorScoreResolver();
            }
            case AWAY_SIDE_VICTORY -> {
                return new AwaySideVictorScoreResolver();
            }
            case DRAW -> {
                return new DrawScoreResolver();
            }
            default -> throw new IllegalArgumentException("A MatchResolver cannot be assigned to a FootballMatch with a non-match finished status.");
        }
    }
}
