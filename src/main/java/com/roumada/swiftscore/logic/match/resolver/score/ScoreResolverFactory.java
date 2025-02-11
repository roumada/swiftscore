package com.roumada.swiftscore.logic.match.resolver.score;

import com.roumada.swiftscore.model.match.FootballMatch;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ScoreResolverFactory {
    private static final Map<FootballMatch.MatchResult, ScoreResolver> resolverMap = new EnumMap<>(FootballMatch.MatchResult.class);

    static {
        resolverMap.put(FootballMatch.MatchResult.HOME_SIDE_VICTORY, new HomeSideVictorResolver());
        resolverMap.put(FootballMatch.MatchResult.AWAY_SIDE_VICTORY, new AwaySideVictorResolver());
        resolverMap.put(FootballMatch.MatchResult.DRAW, new DrawResolver());
    }

    private ScoreResolverFactory() {
    }

    public static Optional<ScoreResolver> getFor(FootballMatch.MatchResult matchResult) {
        return Optional.ofNullable(resolverMap.get(matchResult));
    }
}
