package com.roumada.swiftscore.logic.match.resolver.score;

import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.EnumMap;
import java.util.Map;

public class ScoreResolverFactory {
    private static final Map<FootballMatch.MatchResult, ScoreResolver> resolverMap = new EnumMap<>(FootballMatch.MatchResult.class);

    static {
        resolverMap.put(FootballMatch.MatchResult.HOME_SIDE_VICTORY, new HomeSideVictorResolver());
        resolverMap.put(FootballMatch.MatchResult.AWAY_SIDE_VICTORY, new AwaySideVictorResolver());
        resolverMap.put(FootballMatch.MatchResult.DRAW, new DrawResolver());
    }

    private ScoreResolverFactory() {
    }

    public static ScoreResolver getFor(FootballMatch.MatchResult matchResult) {
        var resolver = resolverMap.get(matchResult);
        if (resolver == null) {
            throw new IllegalArgumentException("A ScoreResolver cannot be assigned to a FootballMatch with a match unfinished status.");
        }
        return resolver;
    }
}
