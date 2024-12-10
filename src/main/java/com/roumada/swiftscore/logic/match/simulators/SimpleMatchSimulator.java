package com.roumada.swiftscore.logic.match.simulators;

import com.roumada.swiftscore.logic.match.resolvers.MatchResolverFactory;
import com.roumada.swiftscore.model.match.FootballMatch;

public class SimpleMatchSimulator implements MatchSimulator {

    @Override
    public void simulateMatch(FootballMatch footballMatch) {
        determineResult(footballMatch);
        MatchResolverFactory.getFor(footballMatch).resolve(footballMatch);
    }

    private void determineResult(FootballMatch footballMatch) {
        if (footballMatch.getHomeSideVictoryChance() > footballMatch.getAwaySideVictoryChance()) {
            footballMatch.setMatchResult(FootballMatch.Result.HOME_SIDE_VICTORY);
        } else if (footballMatch.getHomeSideVictoryChance() < footballMatch.getAwaySideVictoryChance()) {
            footballMatch.setMatchResult(FootballMatch.Result.AWAY_SIDE_VICTORY);
        } else {
            footballMatch.setMatchResult(FootballMatch.Result.DRAW);
        }
    }
}
