package com.roumada.swiftscore.logic.match.resolver.extra;

import com.roumada.swiftscore.model.match.FootballMatch;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ExtraScoreResolverFactory {
    private static final Map<FootballMatch.MatchResult, ExtraScoreResolver> resolverMap = new EnumMap<>(FootballMatch.MatchResult.class);

    static {
        resolverMap.put(FootballMatch.MatchResult.HOME_SIDE_VICTORY, new HomeSideExtraScoreResolver());
        resolverMap.put(FootballMatch.MatchResult.AWAY_SIDE_VICTORY, new AwaySideExtraScoreResolver());
    }

    private ExtraScoreResolverFactory() {
    }

    public static Optional<ExtraScoreResolver> getFor(FootballMatch.MatchResult matchResult) {
        var resolver = resolverMap.get(matchResult);
        if (resolver == null) {
            log.warn("An ExtraScoreResolver can only be assigned to a HOME_SIDE_VICTORY or AWAY_SIDE_VICTORY status.");
            return Optional.empty();
        }
        return Optional.of(resolver);
    }
}
