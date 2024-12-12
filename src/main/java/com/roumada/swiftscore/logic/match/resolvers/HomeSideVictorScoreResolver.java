package com.roumada.swiftscore.logic.match.resolvers;

import com.roumada.swiftscore.data.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class HomeSideVictorScoreResolver implements ScoreResolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int homeSideGoalsScored = ThreadLocalRandom.current().nextInt(6) + 1;
        int awaySideGoalsScored = homeSideGoalsScored - ThreadLocalRandom.current().nextInt(6);
        homeSideGoalsScored++;
        footballMatch.getHomeSideStatistics().setGoalsScored(homeSideGoalsScored);
        footballMatch.getAwaySideStatistics().setGoalsScored(awaySideGoalsScored);
    }
}
