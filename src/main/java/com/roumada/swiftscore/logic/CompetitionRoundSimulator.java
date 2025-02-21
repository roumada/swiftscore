package com.roumada.swiftscore.logic;

import com.roumada.swiftscore.logic.match.simulator.MatchSimulator;
import com.roumada.swiftscore.model.organization.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;

public class CompetitionRoundSimulator {
    private final MatchSimulator matchSimulator;

    private CompetitionRoundSimulator(MatchSimulator matchSimulator) {
        this.matchSimulator = matchSimulator;
    }

    public static CompetitionRoundSimulator withMatchSimulator(MatchSimulator matchSimulator) {
        return new CompetitionRoundSimulator(matchSimulator);
    }

    public void simulate(CompetitionRound competitionRound) {
        for (FootballMatch match : competitionRound.getMatches()) {
            matchSimulator.simulateMatch(match);
        }
    }
}
