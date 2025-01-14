package com.roumada.swiftscore.logic.match.resolver.victor;

import com.roumada.swiftscore.model.match.FootballMatch;

public class VictorResolverFactory {

    private VictorResolverFactory() {
    }

    public static VictorResolver getFor(FootballMatch.MatchResult result) {
        switch (result) {
            case HOME_SIDE_VICTORY -> {
                return new HomeSideVictorResolver();
            }
            case AWAY_SIDE_VICTORY -> {
                return new AwaySideVictorResolver();
            }
            case DRAW -> {
                return new DrawVictorResolver();
            }
            default ->
                    throw new IllegalArgumentException("A VictorResolver cannot be assigned to a FootballMatch with a non-match finished status.");
        }
    }
}
