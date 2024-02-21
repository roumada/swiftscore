package com.roumada.swiftscore.match.simulators;

import com.roumada.swiftscore.match.resolvers.MatchResolverFactory;
import com.roumada.swiftscore.match.resolvers.ScoreResolver;
import com.roumada.swiftscore.model.match.FootballMatch;

public class SimpleMatchSimulator implements MatchSimulator {

    @Override
    public void simulateMatch(FootballMatch footballMatch) {
        determineMatchStatus(footballMatch);
        ScoreResolver scoreResolver = MatchResolverFactory.getFor(footballMatch);
        scoreResolver.resolve(footballMatch);
    }

    private void determineMatchStatus(FootballMatch footballMatch) {
        if (footballMatch.getHomeSideVictoryChance() > footballMatch.getAwaySideVictoryChance()) {
            footballMatch.setMatchStatus(FootballMatch.Status.HOME_SIDE_VICTORY);
        } else if (footballMatch.getHomeSideVictoryChance() < footballMatch.getAwaySideVictoryChance()) {
            footballMatch.setMatchStatus(FootballMatch.Status.AWAY_SIDE_VICTORY);
        } else {
            footballMatch.setMatchStatus(FootballMatch.Status.DRAW);
        }
    }
}
