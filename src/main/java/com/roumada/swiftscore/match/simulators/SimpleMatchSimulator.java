package com.roumada.swiftscore.match.simulators;

import com.roumada.swiftscore.model.match.FootballMatch;

public class SimpleMatchSimulator implements MatchSimulator {


    @Override
    public void simulateMatch(FootballMatch footballMatch) {
        float homeSideVictoryChance = footballMatch.getHomeSideStatistics().getFootballClub().getVictoryChance();
        float awaySideVictoryChance = footballMatch.getAwaySideStatistics().getFootballClub().getVictoryChance();

        if (homeSideVictoryChance == awaySideVictoryChance) {
            footballMatch.setMatchStatus(FootballMatch.Status.DRAW);
        } else if (homeSideVictoryChance > awaySideVictoryChance) {
            footballMatch.setMatchStatus(FootballMatch.Status.HOME_SIDE_VICTORY);
        } else {
            footballMatch.setMatchStatus(FootballMatch.Status.AWAY_SIDE_VICTORY);
        }
    }
}
