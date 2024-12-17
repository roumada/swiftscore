package com.roumada.swiftscore.logic.competition;

import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.data.model.match.CompetitionRound;
import com.roumada.swiftscore.logic.match.simulators.MatchSimulator;
import com.roumada.swiftscore.logic.match.simulators.NoVarianceMatchSimulator;
import com.roumada.swiftscore.logic.match.simulators.SimpleVarianceMatchSimulator;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionDataLayer competitionDataLayer;

    public CompetitionRound simulateRound(Competition competition) {
        var compSimulated = simulateCurrentRound(competition);
        if (compSimulated == null) return null;

        var currRound = competition.getCurrentRound();
        persistChanges(compSimulated);
        return currRound;
    }

    private void persistChanges(Competition compSimulated) {
        competitionDataLayer.saveCompetitionRound(compSimulated.getCurrentRound());
        compSimulated.incrementCurrentRoundNumber();
        competitionDataLayer.saveCompetition(compSimulated);
    }

    private Competition simulateCurrentRound(Competition competition) {
        if (!competition.canSimulate()) {
            log.error("Cannot simulate competition with {} and current round {}.", competition.getId(), competition.getCurrentRoundNumber());
            return null;
        }

        var roundSimulator = CompetitionRoundSimulator.withMatchSimulator(pickMatchSimulatorFor(competition.getVariance()));
        roundSimulator.simulate(competition.getCurrentRound());
        return competition;
    }

    private MatchSimulator pickMatchSimulatorFor(float variance) {
        if (variance == 0) return new NoVarianceMatchSimulator();
        else return SimpleVarianceMatchSimulator.withVariance(variance);
    }
}
