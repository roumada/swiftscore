package com.roumada.swiftscore.logic.match.simulators;

import com.roumada.swiftscore.logic.match.resolvers.score.ScoreResolverFactory;
import com.roumada.swiftscore.logic.match.resolvers.victor.VictorResolverFactory;
import com.roumada.swiftscore.model.SimulatorValues;
import com.roumada.swiftscore.model.match.FootballMatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class SimpleVarianceMatchSimulator implements MatchSimulator {

    private final SimulatorValues simValues;

    private SimpleVarianceMatchSimulator(SimulatorValues simValues) {
        this.simValues = simValues;
    }

    public static SimpleVarianceMatchSimulator withValues(SimulatorValues simValues) {
        return new SimpleVarianceMatchSimulator(simValues);
    }

    @Override
    public void simulateMatch(FootballMatch footballMatch) {
        determineResult(footballMatch);
        VictorResolverFactory.getFor(footballMatch.getMatchResult()).resolve(footballMatch);
        ScoreResolverFactory.getFor(footballMatch.getMatchResult()).resolve(footballMatch);
    }

    private void determineResult(FootballMatch footballMatch) {
        var homeSideVictoryChance = Math.max(0, footballMatch.getHomeSideVictoryChance() + calculateVariant());
        var awaySideVictoryChance = Math.max(0, footballMatch.getAwaySideVictoryChance() + calculateVariant());

        if (simValues.variance() != 0) {
            log.info("Home side base victory chance: [{}]. New victory chance: [{}]", footballMatch.getHomeSideVictoryChance(),
                    String.format("%.3f", homeSideVictoryChance));
            log.info("Away side base victory chance: [{}]. New victory chance: [{}]", footballMatch.getAwaySideVictoryChance(),
                    String.format("%.3f", awaySideVictoryChance));
        }

        if (canDrawChanceTrigger(homeSideVictoryChance, awaySideVictoryChance) && drawChanceTriggers()) {
                footballMatch.setMatchResult(FootballMatch.MatchResult.DRAW);
            }

        if (homeSideVictoryChance > awaySideVictoryChance) {
            footballMatch.setMatchResult(FootballMatch.MatchResult.HOME_SIDE_VICTORY);
        } else if (homeSideVictoryChance < awaySideVictoryChance) {
            footballMatch.setMatchResult(FootballMatch.MatchResult.AWAY_SIDE_VICTORY);
        } else {
            footballMatch.setMatchResult(FootballMatch.MatchResult.DRAW);
        }
    }

    private boolean drawChanceTriggers() {
        return simValues.drawTriggerChance() > ThreadLocalRandom.current().nextDouble();
    }

    private boolean canDrawChanceTrigger(double homeSideVictoryChance, double awaySideVictoryChance) {
        return Math.abs(homeSideVictoryChance - awaySideVictoryChance) < simValues.scoreDifferenceDrawTrigger();
    }

    private double calculateVariant() {
        if (simValues.variance() == 0) return 0;
        return ((double) ThreadLocalRandom.current()
                .nextInt(1, (int) (simValues.variance() * 2 * 100)) / 100) - simValues.variance();
    }
}
