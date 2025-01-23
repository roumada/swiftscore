package com.roumada.swiftscore.logic.match.resolver;

import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class DrawResolver implements Resolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int goalsScored = ThreadLocalRandom.current().nextInt(6);
        footballMatch.setHomeSideGoalsScored(goalsScored);
        footballMatch.setAwaySideGoalsScored(goalsScored);
    }
}
