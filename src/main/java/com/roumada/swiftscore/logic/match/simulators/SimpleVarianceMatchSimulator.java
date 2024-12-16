package com.roumada.swiftscore.logic.match.simulators;

import com.roumada.swiftscore.logic.match.resolvers.MatchResolverFactory;
import com.roumada.swiftscore.data.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class SimpleVarianceMatchSimulator implements MatchSimulator {

    private final float variance;

    public static SimpleVarianceMatchSimulator withVariance(float variance){
        if (variance < 0 || variance > 1){
            throw new IllegalArgumentException("Variance cannot be lower than 0 and higher than 1");
        }
        return new SimpleVarianceMatchSimulator(variance);
    }

    private SimpleVarianceMatchSimulator(float variance) {
        this.variance = variance;
    }

    @Override
    public void simulateMatch(FootballMatch footballMatch) {
        determineResult(footballMatch);
        MatchResolverFactory.getFor(footballMatch).resolve(footballMatch);
    }

    private void determineResult(FootballMatch footballMatch) {
        var homeSideVictoryChance = footballMatch.getHomeSideVictoryChance() +
                ((float) ThreadLocalRandom.current().nextInt(1, (int) (variance * 100)) / 100);
        var awaySideVictoryChance = footballMatch.getAwaySideVictoryChance() +
                ((float) ThreadLocalRandom.current().nextInt(1, (int) (variance * 100)) / 100);

        if (homeSideVictoryChance > awaySideVictoryChance) {
            footballMatch.setMatchResult(FootballMatch.Result.HOME_SIDE_VICTORY);
        } else if (homeSideVictoryChance < awaySideVictoryChance) {
            footballMatch.setMatchResult(FootballMatch.Result.AWAY_SIDE_VICTORY);
        } else {
            footballMatch.setMatchResult(FootballMatch.Result.DRAW);
        }
    }
}
