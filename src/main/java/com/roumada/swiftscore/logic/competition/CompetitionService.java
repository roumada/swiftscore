package com.roumada.swiftscore.logic.competition;

import com.roumada.swiftscore.data.model.match.Competition;
import com.roumada.swiftscore.data.model.match.CompetitionRound;
import com.roumada.swiftscore.logic.match.simulators.MatchSimulator;
import com.roumada.swiftscore.logic.match.simulators.NoVarianceMatchSimulator;
import com.roumada.swiftscore.logic.match.simulators.SimpleVarianceMatchSimulator;
import com.roumada.swiftscore.persistence.CompetitionDataLayer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionDataLayer competitionDataLayer;

    public CompetitionRound simulateRound(Competition competition) {
        CompetitionRoundSimulator simulator =
                CompetitionRoundSimulator.withMatchSimulator(pickMatchSimulatorFor(competition.getVariance()));

        CompetitionRound currentRound = competition.getRounds().get(competition.getCurrentRound());
        currentRound = simulator.simulate(currentRound);
        competition.setCurrentRound(competition.getCurrentRound() + 1);

        competitionDataLayer.saveRound(currentRound);
        competitionDataLayer.saveCompetition(competition);
        return currentRound;
    }

    private MatchSimulator pickMatchSimulatorFor(float variance) {
        if (variance == 0) return new NoVarianceMatchSimulator();
        else return SimpleVarianceMatchSimulator.withVariance(variance);
    }


}
