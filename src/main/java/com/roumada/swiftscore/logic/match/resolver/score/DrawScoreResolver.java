package com.roumada.swiftscore.logic.match.resolver.score;

import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class DrawScoreResolver implements ScoreResolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int goalsScored = ThreadLocalRandom.current().nextInt(6);
        footballMatch.getHomeSideStatistics().setGoalsScored(goalsScored);
        footballMatch.getAwaySideStatistics().setGoalsScored(goalsScored);
    }
}
