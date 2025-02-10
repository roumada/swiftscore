package com.roumada.swiftscore.logic.match.resolver.score;

import com.roumada.swiftscore.logic.match.HorusSeries;
import com.roumada.swiftscore.logic.match.resolver.Resolver;
import com.roumada.swiftscore.model.match.FootballMatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class HomeSideVictorResolver implements ScoreResolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int homeSideScoredCeiling = ThreadLocalRandom.current().nextInt(8);
        int homeSideGoalsScored = HorusSeries.getFromOne(homeSideScoredCeiling, ThreadLocalRandom.current().nextDouble());
        int awaySideGoalsScored = HorusSeries.getFromOne(homeSideGoalsScored - 1, ThreadLocalRandom.current().nextDouble()) - 1;
        footballMatch.setHomeSideGoalsScored(homeSideGoalsScored);
        footballMatch.setAwaySideGoalsScored(awaySideGoalsScored);
    }
}
