package com.roumada.swiftscore.logic.match.resolver.score;

import com.roumada.swiftscore.model.match.FootballMatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class HomeSideVictorScoreResolver implements ScoreResolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int homeSideGoalsScored = ThreadLocalRandom.current().nextInt(6) + 1;
        int awaySideGoalsScored = ThreadLocalRandom.current().nextInt(homeSideGoalsScored);
        homeSideGoalsScored++;
        footballMatch.getHomeSideStatistics().setGoalsScored(homeSideGoalsScored);
        footballMatch.getAwaySideStatistics().setGoalsScored(awaySideGoalsScored);
    }
}
