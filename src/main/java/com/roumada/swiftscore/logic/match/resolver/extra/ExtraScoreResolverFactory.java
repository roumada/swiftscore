package com.roumada.swiftscore.logic.match.resolver.extra;

import com.roumada.swiftscore.logic.match.resolver.Resolver;
import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.EnumMap;
import java.util.Map;

public class ExtraScoreResolverFactory {
    private static final Map<FootballMatch.MatchResult, Resolver> resolverMap = new EnumMap<>(FootballMatch.MatchResult.class);

    static {
        resolverMap.put(FootballMatch.MatchResult.HOME_SIDE_VICTORY, new HomeSideExtraScoreResolver());
        resolverMap.put(FootballMatch.MatchResult.AWAY_SIDE_VICTORY, new AwaySideExtraScoreResolver());
    }

    private ExtraScoreResolverFactory() {
    }

    public static Resolver getFor(FootballMatch.MatchResult matchResult) {
        return resolverMap.get(matchResult);
    }
}
