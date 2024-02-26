package com.roumada.swiftscore.match;

import com.roumada.swiftscore.match.simulators.MatchSimulator;
import com.roumada.swiftscore.model.match.FootballMatch;
import com.roumada.swiftscore.model.match.MatchWeek;

public class MatchWeekSimulator {
    private final MatchSimulator matchSimulator;

    private MatchWeekSimulator(MatchSimulator matchSimulator) {
        this.matchSimulator = matchSimulator;
    }

    public static MatchWeekSimulator withMatchSimulator(MatchSimulator matchSimulator) {
        return new MatchWeekSimulator(matchSimulator);
    }

    public void simulate(MatchWeek matchWeek) {
        for (FootballMatch match : matchWeek.matches()) {
            matchSimulator.simulateMatch(match);
        }
    }
}
