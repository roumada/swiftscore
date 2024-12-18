package com.roumada.swiftscore.logic.match.resolvers;

import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class AwaySideVictorScoreResolver implements ScoreResolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int awaySideGoalsScored = ThreadLocalRandom.current().nextInt(6) + 1;
        int homeSideGoalsScored = ThreadLocalRandom.current().nextInt(awaySideGoalsScored);
        awaySideGoalsScored++;
        footballMatch.getHomeSideStatistics().setGoalsScored(homeSideGoalsScored);
        footballMatch.getAwaySideStatistics().setGoalsScored(awaySideGoalsScored);
    }
}
