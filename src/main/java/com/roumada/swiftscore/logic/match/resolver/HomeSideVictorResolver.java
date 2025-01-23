package com.roumada.swiftscore.logic.match.resolver;

import com.roumada.swiftscore.model.match.FootballMatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class HomeSideVictorResolver implements Resolver {
    @Override
    public void resolve(FootballMatch footballMatch) {
        int homeSideGoalsScored = ThreadLocalRandom.current().nextInt(6) + 1;
        int awaySideGoalsScored = ThreadLocalRandom.current().nextInt(homeSideGoalsScored);
        footballMatch.setHomeSideGoalsScored(homeSideGoalsScored);
        footballMatch.setAwaySideGoalsScored(awaySideGoalsScored);
    }
}
