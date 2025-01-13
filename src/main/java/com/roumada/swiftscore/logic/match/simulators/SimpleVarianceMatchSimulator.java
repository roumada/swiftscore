package com.roumada.swiftscore.logic.match.simulators;

import com.roumada.swiftscore.logic.match.resolvers.MatchResolverFactory;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.FootballMatchStatistics;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class SimpleVarianceMatchSimulator implements MatchSimulator {

    private final double variance;

    public static SimpleVarianceMatchSimulator withVariance(double variance){
        if (variance < 0 || variance > 1){
            throw new IllegalArgumentException("Variance cannot be lower than 0 and higher than 1");
        }
        return new SimpleVarianceMatchSimulator(variance);
    }

    private SimpleVarianceMatchSimulator(double variance) {
        this.variance = variance;
    }

    @Override
    public void simulateMatch(FootballMatch footballMatch) {
        determineResult(footballMatch);
        MatchResolverFactory.getFor(footballMatch).resolve(footballMatch);
    }

    private void determineResult(FootballMatch footballMatch) {
        var homeSideVictoryChance = Math.max(0, footballMatch.getHomeSideVictoryChance() + calculateVariant());
        var awaySideVictoryChance = Math.max(0, footballMatch.getAwaySideVictoryChance() + calculateVariant());

        if(variance != 0){
            log.info("Home side base victory chance: [{}]. New victory chance: [{}]", footballMatch.getHomeSideVictoryChance(),
                    String.format("%.3f", homeSideVictoryChance));
            log.info("Away side base victory chance: [{}]. New victory chance: [{}]", footballMatch.getAwaySideVictoryChance(),
                    String.format("%.3f", awaySideVictoryChance));
        }

        if (homeSideVictoryChance > awaySideVictoryChance) {
            footballMatch.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
            footballMatch.getHomeSideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.VICTORY);
            footballMatch.getAwaySideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.LOSS);
        } else if (homeSideVictoryChance < awaySideVictoryChance) {
            footballMatch.setMatchResult(FootballMatch.MatchResult.AWAY_SIDE_VICTORY);
            footballMatch.getHomeSideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.LOSS);
            footballMatch.getAwaySideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.VICTORY);
        } else {
            footballMatch.setMatchResult(FootballMatch.MatchResult.DRAW);
            footballMatch.getHomeSideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.DRAW);
            footballMatch.getAwaySideStatistics().setResult(FootballMatchStatistics.MatchStatisticsResult.DRAW);
        }
    }

    private double calculateVariant() {
        if (variance == 0) return 0;
        return ((double) ThreadLocalRandom.current().nextInt(1, (int) (variance * 2 * 100)) / 100) - variance;
    }
}
