package com.roumada.swiftscore.logic.match.resolver.score;

import com.roumada.swiftscore.logic.match.HorusSeries;
import com.roumada.swiftscore.logic.match.resolver.Resolver;
import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class AwaySideVictorResolver implements ScoreResolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int awaySideScoredCeiling = ThreadLocalRandom.current().nextInt(9);
        int awaySideGoalsScored = HorusSeries.getFromOne(awaySideScoredCeiling, ThreadLocalRandom.current().nextDouble());
        int homeSideGoalsScored = HorusSeries.getFromOne(awaySideGoalsScored - 1, ThreadLocalRandom.current().nextDouble()) - 1;
        footballMatch.setHomeSideGoalsScored(homeSideGoalsScored);
        footballMatch.setAwaySideGoalsScored(awaySideGoalsScored);
    }
}
