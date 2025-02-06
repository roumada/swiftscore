package com.roumada.swiftscore.logic.match.resolver;

import com.roumada.swiftscore.logic.match.HorusSeries;
import com.roumada.swiftscore.model.match.FootballMatch;

import java.util.concurrent.ThreadLocalRandom;

public class AwaySideVictorResolver implements Resolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int awaySideScoredCeiling = ThreadLocalRandom.current().nextInt(9);
        int awaySideGoalsScored = HorusSeries.getGoalsScored(awaySideScoredCeiling, ThreadLocalRandom.current().nextDouble());
        int homeSideGoalsScored = HorusSeries.getGoalsScored(awaySideGoalsScored - 1, ThreadLocalRandom.current().nextDouble()) - 1;
        footballMatch.setHomeSideGoalsScored(homeSideGoalsScored);
        footballMatch.setAwaySideGoalsScored(awaySideGoalsScored);
    }
}
