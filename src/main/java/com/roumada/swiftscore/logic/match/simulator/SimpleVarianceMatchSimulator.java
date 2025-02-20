package com.roumada.swiftscore.logic.match.simulator;

import com.roumada.swiftscore.logic.match.resolver.extra.ExtraScoreResolverFactory;
import com.roumada.swiftscore.logic.match.resolver.score.ScoreResolverFactory;
import com.roumada.swiftscore.model.SimulationParameters;
import com.roumada.swiftscore.model.match.FootballMatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class SimpleVarianceMatchSimulator implements MatchSimulator {

    private final SimulationParameters simParameters;

    private SimpleVarianceMatchSimulator(SimulationParameters simParameters) {
        this.simParameters = simParameters;
    }

    public static SimpleVarianceMatchSimulator withValues(SimulationParameters simParameters) {
        return new SimpleVarianceMatchSimulator(simParameters);
    }

    @Override
    public void simulateMatch(FootballMatch footballMatch) {
        determineResult(footballMatch);
        ScoreResolverFactory.getFor(footballMatch.getMatchResult())
                .ifPresent(resolver -> resolver.resolve(footballMatch));
        ExtraScoreResolverFactory.getFor(footballMatch.getMatchResult())
                .ifPresent(resolver -> resolver.resolve(footballMatch));
    }

    private void determineResult(FootballMatch footballMatch) {
        var homeSideVictoryChance = Math.min(1,
                Math.max(0, footballMatch.getHomeSideVictoryChance() + calculateVariant()));
        var awaySideVictoryChance = Math.min(1,
                Math.max(0, footballMatch.getAwaySideVictoryChance() + calculateVariant()));
        footballMatch.setHomeSideCalculatedVictoryChance(homeSideVictoryChance);
        footballMatch.setAwaySideCalculatedVictoryChance(awaySideVictoryChance);

        if (simParameters.variance() != 0) {
            log.debug("Home side base victory chance: [{}]. New victory chance: [{}]",
                    footballMatch.getHomeSideVictoryChance(),
                    String.format("%.3f", homeSideVictoryChance));
            log.debug("Away side base victory chance: [{}]. New victory chance: [{}]",
                    footballMatch.getAwaySideVictoryChance(),
                    String.format("%.3f", awaySideVictoryChance));
        }

        if (canDrawChanceTrigger(homeSideVictoryChance, awaySideVictoryChance) && drawChanceTriggers()) {
            footballMatch.setMatchResult(FootballMatch.MatchResult.DRAW);
            return;
        }

        if (homeSideVictoryChance > awaySideVictoryChance) {
            footballMatch.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        } else if (homeSideVictoryChance < awaySideVictoryChance) {
            footballMatch.setMatchResult(FootballMatch.MatchResult.AWAY_SIDE_VICTORY);
        } else {
            footballMatch.setMatchResult(FootballMatch.MatchResult.DRAW);
        }
    }

    private boolean canDrawChanceTrigger(double homeSideVictoryChance, double awaySideVictoryChance) {
        return Math.abs(homeSideVictoryChance - awaySideVictoryChance) <= simParameters.scoreDifferenceDrawTrigger();
    }

    private boolean drawChanceTriggers() {
        return simParameters.drawTriggerChance() > ThreadLocalRandom.current().nextDouble();
    }

    private double calculateVariant() {
        if (simParameters.variance() == 0) return 0;
        return ((double) ThreadLocalRandom.current()
                .nextInt(1, (int) (simParameters.variance() * 2 * 100)) / 100) - simParameters.variance();
    }
}
