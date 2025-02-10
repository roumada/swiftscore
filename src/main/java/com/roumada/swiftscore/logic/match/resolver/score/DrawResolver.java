package com.roumada.swiftscore.logic.match.resolver.score;

import com.roumada.swiftscore.logic.match.HorusSeries;
import com.roumada.swiftscore.logic.match.resolver.Resolver;
import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class DrawResolver implements ScoreResolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int goalsScored = HorusSeries.getFromZero(
                ThreadLocalRandom.current().nextInt(9),
                ThreadLocalRandom.current().nextDouble());
        footballMatch.setHomeSideGoalsScored(goalsScored);
        footballMatch.setAwaySideGoalsScored(goalsScored);
    }
}
