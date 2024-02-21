package com.roumada.swiftscore.match.simulators;

import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class SimpleMatchSimulator implements MatchSimulator {


    @Override
    public void simulateMatch(FootballMatch footballMatch) {
        float homeSideVictoryChance = footballMatch.getHomeSideStatistics().getFootballClub().getVictoryChance();
        float awaySideVictoryChance = footballMatch.getAwaySideStatistics().getFootballClub().getVictoryChance();

        if (homeSideVictoryChance == awaySideVictoryChance) {
            int goalsScored = ThreadLocalRandom.current().nextInt(6);
            footballMatch.getHomeSideStatistics().setGoalsScored(goalsScored);
            footballMatch.getAwaySideStatistics().setGoalsScored(goalsScored);
            footballMatch.setMatchStatus(FootballMatch.Status.DRAW);
        } else if (homeSideVictoryChance > awaySideVictoryChance) {
            int homeSideGoalsScored = ThreadLocalRandom.current().nextInt(6) + 1;
            int awaySideGoalsScored = homeSideGoalsScored - ThreadLocalRandom.current().nextInt(6);
            homeSideGoalsScored++;
            footballMatch.getHomeSideStatistics().setGoalsScored(homeSideGoalsScored);
            footballMatch.getAwaySideStatistics().setGoalsScored(awaySideGoalsScored);
            footballMatch.setMatchStatus(FootballMatch.Status.HOME_SIDE_VICTORY);
        } else {
            int awaySideGoalsScored = ThreadLocalRandom.current().nextInt(6) + 1;
            int homeSideGoalsScored = awaySideGoalsScored - ThreadLocalRandom.current().nextInt(6);
            awaySideGoalsScored++;
            footballMatch.getHomeSideStatistics().setGoalsScored(homeSideGoalsScored);
            footballMatch.getAwaySideStatistics().setGoalsScored(awaySideGoalsScored);
            footballMatch.setMatchStatus(FootballMatch.Status.AWAY_SIDE_VICTORY);
        }
    }
}
