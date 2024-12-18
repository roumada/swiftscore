package com.roumada.swiftscore.logic.competition;

import com.roumada.swiftscore.logic.match.simulators.MatchSimulator;
import com.roumada.swiftscore.model.match.CompetitionRound;
import com.roumada.swiftscore.model.match.FootballMatch;

public class CompetitionRoundSimulator {
    private final MatchSimulator matchSimulator;

    private CompetitionRoundSimulator(MatchSimulator matchSimulator) {
        this.matchSimulator = matchSimulator;
    }

    public static CompetitionRoundSimulator withMatchSimulator(MatchSimulator matchSimulator) {
        return new CompetitionRoundSimulator(matchSimulator);
    }

    public CompetitionRound simulate(CompetitionRound competitionRound) {
        for (FootballMatch match : competitionRound.getMatches()) {
            matchSimulator.simulateMatch(match);
        }
        return competitionRound;
    }
}
