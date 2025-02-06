package com.roumada.swiftscore.logic.match.resolver;

import com.roumada.swiftscore.logic.match.HorusSeries;
import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class DrawResolver implements Resolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int goalsScored = HorusSeries.getGoalsScoredForDraw(
                ThreadLocalRandom.current().nextInt(9),
                ThreadLocalRandom.current().nextDouble());
        footballMatch.setHomeSideGoalsScored(goalsScored);
        footballMatch.setAwaySideGoalsScored(goalsScored);
    }
}
