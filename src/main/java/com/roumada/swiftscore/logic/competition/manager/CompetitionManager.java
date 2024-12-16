package com.roumada.swiftscore.logic.competition.manager;

import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.logic.competition.CompetitionRoundSimulator;
import com.roumada.swiftscore.logic.match.simulators.MatchSimulator;
import com.roumada.swiftscore.logic.match.simulators.NoVarianceMatchSimulator;
import com.roumada.swiftscore.logic.match.simulators.SimpleVarianceMatchSimulator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompetitionManager {

    private CompetitionManager() {}

    public static Competition simulateCurrentRound(Competition competition) {
        if (!competition.canSimulate()) {
            log.error("Cannot simulate competition with {} and current round {}.", competition.getId(), competition.getCurrentRoundNumber());
            return null;
        }

        var roundSimulator = CompetitionRoundSimulator.withMatchSimulator(pickMatchSimulatorFor(competition.getVariance()));
        roundSimulator.simulate(competition.getCurrentRound());
        competition.incrementCurrentRoundNumber();
        return competition;
    }

    private static MatchSimulator pickMatchSimulatorFor(float variance) {
        if (variance == 0) return new NoVarianceMatchSimulator();
        else return SimpleVarianceMatchSimulator.withVariance(variance);
    }
}
