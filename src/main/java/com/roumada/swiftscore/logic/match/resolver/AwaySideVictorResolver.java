package com.roumada.swiftscore.logic.match.resolver;

import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class AwaySideVictorResolver implements Resolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int awaySideGoalsScored = ThreadLocalRandom.current().nextInt(6) + 1;
        int homeSideGoalsScored = ThreadLocalRandom.current().nextInt(awaySideGoalsScored);
        footballMatch.setHomeSideGoalsScored(homeSideGoalsScored);
        footballMatch.setAwaySideGoalsScored(awaySideGoalsScored);
    }
}
